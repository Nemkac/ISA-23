import { Component, OnInit } from '@angular/core';
import { EquipmentPickupSlot } from 'src/app/models/EquipmentPickupSlot';
import { faClose } from '@fortawesome/free-solid-svg-icons';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { EquipmentPickupSlotService } from 'src/app/services/equipment-pickup-slot.service';
import { HttpErrorResponse } from '@angular/common/http';
import { NgToastService } from 'ng-angular-popup'
import { UserService } from 'src/app/services/user.service';
import { User } from 'src/user';

@Component({
  selector: 'app-equipment-pickup-slot-display-modal',
  templateUrl: './equipment-pickup-slot-display-modal.component.html',
})
export class EquipmentPickupSlotDisplayModalComponent implements OnInit{
  public slot: EquipmentPickupSlot | undefined;
  public expired: boolean = false;
  public pickedUp: boolean = false;
  public active: boolean = false;
  public free: boolean = false;
  public token = localStorage.getItem('token');

  public companyAdmin: User | undefined;

  handleDeliveryComplete: (() => void) | undefined;

  faClose = faClose;


  constructor(private modalService: NgbActiveModal,
              private equipmentPickupSlotService: EquipmentPickupSlotService,
              private userService: UserService,
              private toast: NgToastService){}
  
  public ngOnInit(): void {
    this.getSlotStatus();
  }

  public getSlotStatus() : void{
    if(this.slot?.status === "EXPIRED"){
      this.expired = true;
      this.pickedUp = false
      this.active = false;
      this.free = false;
    } else if(this.slot?.status === "PICKED_UP"){
      this.pickedUp = true;
      this.expired = false;
      this.active = false;
      this.free = false;
    } else if(this.slot?.reservedBy){
      this.pickedUp = false;
      this.expired = false;
      this.active = true;
      this.free = false;
    } else {
      this.pickedUp = false;
      this.expired = false;
      this.active = false;
      this.free = true;
    }
  }

  public closeModal(): void {
    this.modalService.close();
  }

  public deliverEquipment(slotId : number | undefined, version: number | undefined) : void{

    this.equipmentPickupSlotService.deliverEquipment(slotId, version).subscribe(
      (response:EquipmentPickupSlot) => {
        //this.toast.success({detail:"Delivery successful!", summary:"Equipment successfully delivered. Appointment status: PICKED_UP"});
        this.pickedUp = true;
        this.expired = false;
        this.active = false;
        this.free = false;

        setTimeout(() => {
          this.closeModal();
          if(this.handleDeliveryComplete){
            this.handleDeliveryComplete();
          }
        }, 1000);
      },
      (error: HttpErrorResponse) => {
        this.toast.error({detail:"Error message", summary:"Error during delivery of equipment! Please refresh the page and try again."});
        this.pickedUp = false;
        this.expired = false;
        this.active = true;
        this.free = false;
      }
    )
  }
}


