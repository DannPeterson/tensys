import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Tender } from '../model/tender';
import { formatDate } from '@angular/common';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class TenderService {
  private host = environment.apiUrl;

  constructor(private http: HttpClient,
    private authService: AuthenticationService,
    @Inject(LOCALE_ID) private locale: string) { }

  public getTendersForPeriod(startDate: Date, endDate: Date): Observable<Tender[]> {
    const username = this.authService.getUserFromLocalCache().username;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    const lang = this.authService.getUserFromLocalCache().language;
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<Tender[]>(`${this.host}/tender/listforperiod/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTendersForPeriodSize(startDate: Date, endDate: Date): Observable<number> {
    const username = this.authService.getUserFromLocalCache().username;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    const lang = this.authService.getUserFromLocalCache().language;
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<number>(`${this.host}/tender/listforperiod-size/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTendersCpvNotShowFilteredForPeriod(startDate: Date, endDate: Date): Observable<Tender[]> {
    const username = this.authService.getUserFromLocalCache().username;
    const lang = this.authService.getUserFromLocalCache().language;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<Tender[]>(`${this.host}/tender/listNotShowCpvFilteredForPeriod/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTendersCpvNotShowFilteredForPeriodSize(startDate: Date, endDate: Date): Observable<number> {
    const username = this.authService.getUserFromLocalCache().username;
    const lang = this.authService.getUserFromLocalCache().language;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<number>(`${this.host}/tender/listNotShowCpvFilteredForPeriod-size/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTendersCpvShowFilteredForPeriod(startDate: Date, endDate: Date): Observable<Tender[]> {
    const username = this.authService.getUserFromLocalCache().username;
    const lang = this.authService.getUserFromLocalCache().language;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<Tender[]>(`${this.host}/tender/listShowCpvFilteredForPeriod/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTendersCpvShowFilteredForPeriodSize(startDate: Date, endDate: Date): Observable<number> {
    const username = this.authService.getUserFromLocalCache().username;
    const lang = this.authService.getUserFromLocalCache().language;
    const start = formatDate(startDate, 'yyyy-MM-dd HH:mm', this.locale);
    const end = formatDate(endDate, 'yyyy-MM-dd HH:mm', this.locale);
    let sources: number[] = [];
    for(let source of this.authService.getUserFromLocalCache().sources) {
      sources.push(source.id);
    }
    return this.http.get<number>(`${this.host}/tender/listShowCpvFilteredForPeriod-size/${username}/${start}/${end}/${lang}/${sources}`);
  }

  public getTenderOriginal(id: number): Observable<Tender> {
    return this.http.get<Tender>(`${this.host}/tender/original/${id}`);
  }

  public addTendersToLocalCache(tenders: Tender[]): void {
    localStorage.setItem('tenders', JSON.stringify(tenders));
  }

  public getTendersFromLocalCache(): Tender[] {
    if (localStorage.getItem('tenders')) {
      return JSON.parse(localStorage.getItem('tenders'));
    }
    return null;
  }
}