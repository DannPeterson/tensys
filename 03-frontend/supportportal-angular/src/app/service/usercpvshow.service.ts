import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { CustomHttpRespone } from '../model/custom-http-response';
import { UserCpvShow } from '../model/usercpvshow';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class UsercpvshowService {
  private host = environment.apiUrl;
  
  constructor(private http: HttpClient, private auth: AuthenticationService) { }

  public getUserShowCpvs(username: string): Observable<UserCpvShow[]> {
    let lang = this.auth.getLanguage();
    return this.http.get<UserCpvShow[]>(`${this.host}/userCpvShow/${username}/${lang}`);
  }

  public deleteUserShowCpv(id: number): Observable<CustomHttpRespone>{
    let lang = this.auth.getLanguage();
    return this.http.delete<CustomHttpRespone>(`${this.host}/userCpvShow/delete/${id}/${lang}`);
  }

  public addUserShowCpv(username: string, cpvId: number): Observable<UserCpvShow> {
    return this.http.post<UserCpvShow>(`${this.host}/userCpvShow/add`, this.createUserCpvShowFormData(username, cpvId));
  }

  public addCustomUserShowCpv(username: string, cpvCode: string, cpvDescription: string): Observable<UserCpvShow>{
    return this.http.post<UserCpvShow>(`${this.host}/userCpvShow/addCustom`, this.createCustomUserCpvShowFormData(username, cpvCode, cpvDescription));
  }

  public createUserCpvShowFormData(username: string, cpvId: number): FormData {
    const formData = new FormData();
    formData.append('username', username);
    formData.append('cpvId', JSON.stringify(cpvId));
    formData.append('lang', this.auth.getUserFromLocalCache().language);
    return formData;
  }

  public createCustomUserCpvShowFormData(username: string, cpvCode: string, cpvDescription: string): FormData{
    const formData = new FormData();
    formData.append('username', username);
    formData.append('cpvCode', cpvCode);
    formData.append('cpvDescription', cpvDescription);
    formData.append('lang', this.auth.getUserFromLocalCache().language);
    return formData;
  }

  public addUserShowCpvsToLocalCache(cpvs: UserCpvShow[]): void {
    localStorage.setItem('userShowCpvs', JSON.stringify(cpvs));
  }

  public getUserShowCpvsFromLocalCache(): UserCpvShow[] {
    if(localStorage.getItem('userShowCpvs')) {
      return JSON.parse(localStorage.getItem('userShowCpvs'));
    }
    return null;
  }
}
