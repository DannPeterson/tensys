import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Cpv } from '../model/cpv';

@Injectable({
  providedIn: 'root'
})
export class CpvService {
  private host = environment.apiUrl;
  
  constructor(private http: HttpClient) { }

  public getCpvsStartsWith(code: string, language: string): Observable<Cpv[]> {
    return this.http.get<Cpv[]>(`${this.host}/cpv/${code}/${language}`);
  }
}