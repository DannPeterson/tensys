import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { User } from '../model/user';
import { JwtHelperService } from '@auth0/angular-jwt';


@Injectable({providedIn: 'root'})
export class AuthenticationService {
  public host = environment.apiUrl;
  private token: string;
  private loggedInUsername: string;
  private jwtHelper = new JwtHelperService();

  constructor(private http: HttpClient) {}

  public login(user: User): Observable<HttpResponse<User>> {
    return this.http.post<User>(`${this.host}/user/login`, user, { observe: 'response' });
  }

  public register(user: User): Observable<User> {
    return this.http.post<User>(`${this.host}/user/register`, user);
  }
 
  public logOut(): void {
    this.token = null;
    this.loggedInUsername = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  }

  public saveLanguage(language: string){
    localStorage.setItem('language', language);
  }

  public getLanguage(): string{
    return localStorage.getItem('language');
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public addUserToLocalCache(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user'));
  }

  public loadToken(): void {2
    this.token = localStorage.getItem('token');
  }

  public getToken(): string {
    return this.token;
  }

  public getSubscriptionUntilDate(username: string): Observable<Date>{
    return this.http.get<Date>(`${this.host}/user/subscription_until/${username}`);
  }

  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (this.token != null && this.token !== ''){
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        } else return false;
      } else return false;
    } else {
      this.logOut();
      return false;
    }
  }

  public checkSubscription() {
    if (this.isUserLoggedIn()) {
      let user = this.getUserFromLocalCache();
      let paidUntil = user.paidUntil;
      this.getSubscriptionUntilDate(user.username).subscribe(
        (response: Date) => {
          paidUntil = response;
          user.paidUntil = paidUntil;
          this.addUserToLocalCache(user);
          this.isUserLoggedIn();
        }
      )
    }
  }
}