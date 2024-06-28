import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { CustomHttpRespone } from '../model/custom-http-response';

@Injectable({providedIn: 'root'})
export class UserService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient) {}

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.host}/user/list`);
  }

  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(`${this.host}/user/add`, formData);
  }

  public updateUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.host}/user/update`, this.createUserFormDate(user));
  }

  public resetPassword(email: string, language: string): Observable<CustomHttpRespone> {
    return this.http.get<CustomHttpRespone>(`${this.host}/user/resetpassword/${email}/${language}`);
  }

  public subscriptionRequest(email: string, plan: string, language: string){
    console.log("LANG: " + language)
    return this.http.get<CustomHttpRespone>(`${this.host}/user/subscription/${email}/${plan}/${language}`);
  }

  public sentMessage(email: string, language: string, text: string) {
    return this.http.get<CustomHttpRespone>(`${this.host}/user/message/${email}/${language}/${text}`);
  }

  public updateProfileImage(formData: FormData): Observable<HttpEvent<User>> {
    return this.http.post<User>(`${this.host}/user/updateProfileImage`, formData,
    {reportProgress: true,
      observe: 'events'
    });
  }

  public deleteUser(username: string): Observable<CustomHttpRespone> {
    return this.http.delete<CustomHttpRespone>(`${this.host}/user/delete/${username}`);
  }

  public getSubscriptionUntilDate(username: string): Observable<Date>{
    return this.http.get<Date>(`${this.host}/user/subscription_until/${username}`);
  }

  public createUserFormDate(user: User): FormData {
    const formData = new FormData();
    formData.append('currentUsername', user.username);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('language', user.language);
    formData.append('role', user.role);
    formData.append('isActive', JSON.stringify(user.active));
    formData.append('isNonLocked', JSON.stringify(user.notLocked));
    const sourceIds = [];
    for(let source of user.sources) {
      sourceIds.push(source.id);
    }
    formData.append('sourceIds', JSON.stringify(sourceIds));
    return formData;
  }
}