import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpRespone } from '../model/custom-http-response';
import { UserCpvNotShow } from '../model/usercpvnotshow';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class UsercpvnotshowService {
  private host = environment.apiUrl;
  
  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  public getUserNotShowCpvs(username: string): Observable<UserCpvNotShow[]> {
    let lang = this.auth.getUserFromLocalCache().language;
    return this.http.get<UserCpvNotShow[]>(`${this.host}/userCpvNotShow/${username}/${lang}`);
  }

  public deleteUserNotShowCpv(id: number): Observable<CustomHttpRespone>{
    let lang = this.auth.getUserFromLocalCache().language;
    return this.http.delete<CustomHttpRespone>(`${this.host}/userCpvNotShow/delete/${id}/${lang}`);
  }

  public addUserNotShowCpv(username: string, cpvId: number): Observable<UserCpvNotShow> {
    return this.http.post<UserCpvNotShow>(`${this.host}/userCpvNotShow/add`, this.createUserCpvNotShowFormData(username, cpvId));
  }

  public addCustomUserNotShowCpv(username: string, cpvCode: string, cpvDescription: string): Observable<UserCpvNotShow>{
    return this.http.post<UserCpvNotShow>(`${this.host}/userCpvNotShow/addCustom`, this.createCustomUserCpvNotShowFormData(username, cpvCode, cpvDescription));
  }

  public createUserCpvNotShowFormData(username: string, cpvId: number): FormData{
    const formData = new FormData();
    formData.append('username', username);
    formData.append('cpvId', JSON.stringify(cpvId));
    formData.append('lang', this.auth.getUserFromLocalCache().language);
    return formData;
  }

  public createCustomUserCpvNotShowFormData(username: string, cpvCode: string, cpvDescription: string): FormData{
    const formData = new FormData();
    formData.append('username', username);
    formData.append('cpvCode', cpvCode);
    formData.append('cpvDescription', cpvDescription);
    formData.append('lang', this.auth.getUserFromLocalCache().language);
    return formData;
  }

  public addUserNotShowCpvsToLocalCache(cpvs: UserCpvNotShow[]): void {
    localStorage.setItem('userNotShowCpvs', JSON.stringify(cpvs));
  }

  public getUserNotShowCpvsFromLocalCache(): UserCpvNotShow[] {
    if(localStorage.getItem('userNotShowCpvs')) {
      return JSON.parse(localStorage.getItem('userNotShowCpvs'));
    }
    return null;
  }
}
