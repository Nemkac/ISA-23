import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { UserProfile } from "src/app/models/user-profile";
import { UserProfileToUpdate } from "src/app/models/user-profile-to-update";
import { Router } from "@angular/router";
import { User } from "src/user";


@Injectable(
    {
        providedIn:'root'
    }
)
export class UserProfileService{
    private apiServerUrl = 'http://localhost:8081';

    constructor(private http: HttpClient,private router:Router) {}

    public showUserProfile(id : number) : Observable<UserProfile> { 
        return this.http.get<UserProfile>(`${this.apiServerUrl}/api/user/profile/${id}`)
    }

    public updateUserProfile(id:number, userProfileToUpdate:UserProfileToUpdate,headers : HttpHeaders) : Observable<UserProfileToUpdate>{
        return this.http.put<UserProfileToUpdate>(`${this.apiServerUrl}/api/user/updateProfile/${id}`, userProfileToUpdate,{headers : headers})
    }

    public goToUpdateProfile1(id:number):void {
        this.router.navigate([`/api/user/updateProfile/${id}`]);
    }

    public goToUpdateProfile(id:number):void {
        this.router.navigate([`/update-profile`]);
    }

}