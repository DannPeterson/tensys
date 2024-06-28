import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Source } from '../model/source';

@Injectable({
  providedIn: 'root'
})
export class SourceService {
  private host = environment.apiUrl;
  
  constructor(private http: HttpClient) { }

  public getSources(): Observable<Source[]> {
    return this.http.get<Source[]>(`${this.host}/source/all`);
  }
}
