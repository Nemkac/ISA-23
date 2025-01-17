import { LiveLocation } from './../../models/LiveLocation';
import { Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import * as L from 'leaflet';
import { faLocation, faLocationDot } from '@fortawesome/free-solid-svg-icons';
import 'leaflet-routing-machine';
import { ActivatedRoute } from '@angular/router';
import { CompanyService } from 'src/app/services/company.service';
import { Company } from 'src/company';
import { RabbitmqLiveLocationService } from 'src/app/services/rabbitmq-live-location.service';
import { NgToastService } from 'ng-angular-popup';
import { RxStomp } from '@stomp/rx-stomp';
import { RxStompService } from 'src/app/services/rx-stomp.service';
import { rxStompServiceFactory } from 'src/app/rx-stomp-service-factory';
import { HttpHeaders } from '@angular/common/http';
import { StompService } from 'src/app/services/stomp.service';

import * as SockJS from 'sockjs-client';
import * as Stomp from 'stompjs';


@Component({
  selector: 'app-request-delivery-page',
  templateUrl: './request-delivery-page.component.html',
})
export class RequestDeliveryPageComponent implements OnInit{
	@ViewChild('mapContainer', { static: true }) mapContainer!: ElementRef;

	companyId! : number;
	selectedCompany: Company | undefined;
	@Input() companyLatitude: number = 0; 
	@Input() companyLongitude: number = 0; 

	faLocation = faLocation;
	faLocationDot = faLocationDot;

	startLocation: L.LatLng | null = null;
	map : L.Map | null = null;
	destinationMarker: L.Marker | null = null
	destinationLatitude: number = 0;
	destinationLongitude: number = 0;
	destinationAddress: string = '';
	routeCoordinates : any;
	marker: any;

	deliveryStarted : boolean = false;

	constructor(private route: ActivatedRoute,
				private companyService: CompanyService,
				private rabbitmqLiveLocationService: RabbitmqLiveLocationService,
				private toast: NgToastService) {} 

	ngOnInit(): void {
		this.establishWebSocketConnection();
		const idFromRoute = this.route.snapshot.paramMap.get('id');
		if(idFromRoute != null) {
			this.companyId =+ idFromRoute
			this.getCompanyData();
		}
	}

	public establishWebSocketConnection(): void {
		const socket = new SockJS('http://localhost:8081/livelocation-websocket');
		const stompClient = Stomp.over(socket);
	
		stompClient.connect({}, (frame) => {
			console.log('Connected to WebSocket');
			stompClient.subscribe('/livelocation-websocket', (message) => {
				console.log("Subscribed to topic");
				const location: LiveLocation = JSON.parse(message.body);
				console.log('New location received:', location);
				this.updateMarkerPosition(location.latitude, location.longitude);
			});
		});
	}
	
	private updateMarkerPosition(latitude: number, longitude: number): void {
		if (this.marker) {
			this.marker.setLatLng([latitude, longitude]);
		}
	}

	public getCompanyData() : void{
		this.companyService.getCompany(this.companyId).subscribe(
		  (response:Company) => {
			this.selectedCompany = response;
			this.companyLatitude = response.latitude; 
			this.companyLongitude = response.longitude;
			console.log(this.selectedCompany);
			this.loadMap();
		  },
		  (error) => {
			console.error('Error fetching company data.', error);
		  }
		);
	}

	loadMap() {
		this.map = L.map(this.mapContainer.nativeElement).setView(
			[this.companyLatitude, this.companyLongitude],
			12
		);
		L.tileLayer('https://{s}.tile.osm.org/{z}/{x}/{y}.png', {attribution: "OSM"}).addTo(this.map);
		
		var deliveryTruckIcon = L.icon({
			iconUrl:'../../../assets/logotip-blue.png',
			iconSize: [50, 40]
		})
		this.marker = L.marker([this.companyLatitude, this.companyLongitude], {icon: deliveryTruckIcon}).addTo(this.map);
		
		this.map.on('click', (e) => {
			if(this.destinationMarker === null){
				this.destinationLatitude = e.latlng.lat;
				this.destinationLongitude = e.latlng.lng;
				this.destinationMarker = L.marker([e.latlng.lat, e.latlng.lng]).addTo(this.map!);
				L.Routing.control({
					waypoints: [
						L.latLng(this.companyLatitude, this.companyLongitude),
						L.latLng([e.latlng.lat, e.latlng.lng])
					]
				}).on('routesfound', (e) => {
					console.log(e);
					const lastRoute = e.routes[e.routes.length - 1];
					const lastStep = lastRoute.instructions[lastRoute.instructions.length - 1];
					this.destinationAddress = lastStep.road;
					this.routeCoordinates = e.routes[0].coordinates;
					this.adjustLeafletControlPosition();
				})
				.addTo(this.map!);
			}
		});
	}

	sendCoordinateToRabbitMQ(latitude: number, longitude: number): void{
		const liveLocation : LiveLocation = {
			latitude: latitude,
			longitude: longitude,
		};

		const token = localStorage.getItem('token');
		const headers = new HttpHeaders({
		'Content-Type': 'application/json',
		'Authorization': `Bearer ${token}`
		});
		this.rabbitmqLiveLocationService.sendLiveLocationMessage(liveLocation,headers).subscribe();
	}

	startDelivery() : void{
		let index = 0;
		if(this.destinationLatitude === 0 || this.destinationLongitude == 0){
			this.toast.error({detail:"Error message", summary:"You must select destination!"});
		} else {
			this.toast.info({detail:"Delivery started", summary:"We will notify you when the delivery person is close to the destination."})	
			const intervalId = setInterval(() => {
				if (index < this.routeCoordinates.length) {
					if(index === this.routeCoordinates.length - 20){
						this.toast.info({detail:"Almost there", summary:"The equipment will be delivered soon. Please be ready to pick up."})
					}	
					const currentCoordinate = this.routeCoordinates[index];
					this.sendCoordinateToRabbitMQ(currentCoordinate.lat, currentCoordinate.lng);
					index++;
					this.deliveryStarted = true;
				} else {
					clearInterval(intervalId);
					setTimeout(() => {
						this.deliveryStarted = false;
						this.destinationAddress = '';
						this.destinationLatitude = 0;
						this.destinationLongitude = 0;
						this.routeCoordinates = null;
						this.destinationMarker = null;
					 }, 2000);
					 this.toast.success({detail:"Equipment delivered", summary:"If the equipment was damaged during transport, please contact us by email."})	
				}
			}, 3000);
		}
	}
	
	private adjustLeafletControlPosition() {
		const leafletTopRight = document.querySelector('.leaflet-top.leaflet-right') as HTMLElement;
		if (leafletTopRight) {
			leafletTopRight.style.position = 'absolute';
			leafletTopRight.style.top = '50%';
			leafletTopRight.style.right = '0';
			leafletTopRight.style.transform = 'translateY(-50%)';
			leafletTopRight.style.zIndex = '1000';
		}
	}
}
