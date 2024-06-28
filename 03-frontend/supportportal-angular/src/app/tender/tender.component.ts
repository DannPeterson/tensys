import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, Subscription, interval } from 'rxjs';
import { NotificationType } from '../enum/notification-type.enum';
import { Cpv } from '../model/cpv';
import { CustomHttpRespone } from '../model/custom-http-response';
import { Tender } from '../model/tender';
import { User } from '../model/user';
import { UserCpvNotShow } from '../model/usercpvnotshow';
import { UserCpvShow } from '../model/usercpvshow';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { TenderService } from '../service/tender.service';
import { UsercpvnotshowService } from '../service/usercpvnotshow.service';
import { UsercpvshowService } from '../service/usercpvshow.service';
import { BsModalService, BsModalRef } from 'ngx-bootstrap/modal';
import { BsLocaleService } from 'ngx-bootstrap/datepicker';
import { defineLocale } from 'ngx-bootstrap/chronos';
import { enGbLocale } from "ngx-bootstrap/locale";
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Source } from '../model/source';
import { SourceService } from '../service/source.service';
import { UserService } from '../service/user.service';
import { CpvService } from '../service/cpv.service';
import { NgForm } from '@angular/forms';
import { formatDate } from '@angular/common';
import { Clipboard } from "@angular/cdk/clipboard";
defineLocale("engb", enGbLocale);
declare var $: any;

@Component({
  selector: 'app-tender',
  templateUrl: './tender.component.html',
  styleUrls: ['./tender.component.css']
})
export class TenderComponent implements OnInit {
  modalRef?: BsModalRef | null;
  modalRef2?: BsModalRef;
  private titleSubject = new BehaviorSubject<string>('All Tenders');
  public titleAction$ = this.titleSubject.asObservable();
  public tenders: Tender[];
  public cpvs: Cpv[];
  public showCpvs: UserCpvShow[];
  public notShowCpvs: UserCpvNotShow[];
  public sources: Source[];
  public user: User;
  public selectedTender: Tender;
  public originalTender: Tender;
  private subscriptions: Subscription[] = [];
  public refreshing: boolean;
  public notShowCpvFilterOn: boolean;
  public inCpvSearch: boolean;
  public cpvSearchItem: string;
  public bsInlineRangeValue: Date[];
  public today = new Date();
  public sub: Subscription;
  public isFiltersFooterVisible = true;

  public mode = "";
  public title = "";

  public searchClient = "";
  public searchTitle = "";
  public searchDescription = "";
  public searchCpvCode = "";

  // Notifications:
  public tendersLoaded = "";
  public filterRemoved = "";
  public searchFieldsCleared = ""
  public tendersShown = "";
  public tendersFound = "";
  public loggedOut = "";
  public errorOccured = "";
  public filterAdded = "";
  public cpvEmpty = "";
  public noDataSourceSelected = "";
  public sourcesUpdated = "";
  public cpvFound = "";

  // Source switches
  public rhrSwitch = false;
  public eisSwitch = false;
  public cvppSwitch = false;

  // Badges
  public unloadedTenders = 0;

  constructor(private router: Router,
    private authenticationService: AuthenticationService,
    private tenderService: TenderService,
    private sourceService: SourceService,
    private notificationService: NotificationService,
    private userCpvNotShowService: UsercpvnotshowService,
    private userCpvShowService: UsercpvshowService,
    private modalService: BsModalService,
    private localeService: BsLocaleService,
    public translate: TranslateService,
    private userService: UserService,
    private cpvService: CpvService,
    private clipboard: Clipboard
  ) { }

  ngOnInit(): void {
    this.refreshing = true;
    this.checkSubscription();
    this.mode = "All";
    this.user = this.authenticationService.getUserFromLocalCache();
    this.translate.use(this.user.language);
    this.translate.get("HEADER.ALL_TENDERS").subscribe((res: string) => {
      this.title = res;
    });
    this.translateNotifications();
    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.onLangChange();
    });
    this.notShowCpvFilterOn = false;
    this.getUserShowCpvs(false);
    this.getUserNotShowCpvs(false);
    this.setSources();
    this.inCpvSearch = false;
    this.bsInlineRangeValue = [this.today, this.today];
    this.localeService.use('engb');
    this.timerFunction();
  }

  isVisible() {
    const elementToTrack = document.getElementById("sensor");
    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          this.isFiltersFooterVisible = true;
        } else {
          this.isFiltersFooterVisible = false;
        }
      });
    })
    observer.observe(elementToTrack);
  }

  timerFunction() {
    this.sub = interval(300_000).subscribe((val) => {
      if (this.mode === 'All') {
        console.log('timer All');
        this.tenderService.getTendersForPeriodSize(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
          (tendersInDb: number) => {
            let tendersShown = this.tenderService.getTendersFromLocalCache().length;
            if (tendersShown < tendersInDb) {
              this.unloadedTenders = tendersInDb - tendersShown;
            }
          }
        )
      }
      if (this.mode === 'Show') {
        console.log('timer Show');
        this.tenderService.getTendersCpvShowFilteredForPeriodSize(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
          (tendersInDb: number) => {
            let tendersShown = this.tenderService.getTendersFromLocalCache().length;
            if (tendersShown < tendersInDb) {
              this.unloadedTenders = tendersInDb - tendersShown;
            }
          }
        )
      }
      if (this.mode === 'NotShow') {
        console.log('timer NotShow');
        this.tenderService.getTendersCpvNotShowFilteredForPeriodSize(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
          (tendersInDb: number) => {
            let tendersShown = this.tenderService.getTendersFromLocalCache().length;
            if (tendersShown < tendersInDb) {
              this.unloadedTenders = tendersInDb - tendersShown;
            }
          }
        )
      }
    });
  }

  onNewTendersButtonClick() {
    if (this.mode === 'All') {
      this.getTenders(true);
    }
    if (this.mode === 'Show') {
      this.getTendersCpvShowFiltered(true);
    }
    if (this.mode === 'NotShow') {
      this.getTendersCpvNotShowFiltered(true);
    }
  }

  setSources() {
    this.subscriptions.push(
      this.sourceService.getSources().subscribe(
        (response: Source[]) => {
          this.sources = response;
          for (var dbSource of this.sources) {
            for (var userSource of this.user.sources) {
              if (userSource.name == dbSource.name && userSource.name == 'RHR') this.rhrSwitch = true;
              if (userSource.name == dbSource.name && userSource.name == 'eis.gov.lv') this.eisSwitch = true;
              if (userSource.name == dbSource.name && userSource.name == 'cvpp') this.cvppSwitch = true;
            }
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  checkSubscription() {
    let user = this.authenticationService.getUserFromLocalCache();
    let paidUntil = user.paidUntil;
    this.userService.getSubscriptionUntilDate(user.username).subscribe(
      (response: Date) => {
        if (formatDate((response), 'yyyy-MM-dd', 'en_US') < formatDate((new Date), 'yyyy-MM-dd', 'en_US')) {
          paidUntil = response;
          user.paidUntil = paidUntil;
          this.authenticationService.addUserToLocalCache(user);
          this.router.navigate(['/prices']);
        }
      }
    )
  }

  applySources() {
    this.modalService.hide(1);
    let sources = [];
    if (this.rhrSwitch == true) {
      let source = new Source();
      source.id = 1;
      source.name = "RHR";
      sources.push(source);
    }
    if (this.eisSwitch == true) {
      let source = new Source();
      source.id = 2;
      source.name = "eis.gov.lv";
      sources.push(source);
    }
    if (this.cvppSwitch == true) {
      let source = new Source();
      source.id = 3;
      source.name = "cvpp";
      sources.push(source);
    }
    this.user.sources = sources;
    this.authenticationService.addUserToLocalCache(this.user);
    this.subscriptions.push(
      this.userService.updateUser(this.user).subscribe(
        (response: User) => {
          this.sendNotification(NotificationType.SUCCESS, `${this.sourcesUpdated}`);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
    if (this.mode === 'All') this.getTenders(true);
    if (this.mode === 'Show') this.getTendersCpvShowFiltered(true);
    if (this.mode === 'NotShow') this.getTendersCpvNotShowFiltered(true);
  }

  onLangChange() {
    let user = this.authenticationService.getUserFromLocalCache();
    user.language = this.translate.currentLang;
    this.subscriptions.push(
      this.userService.updateUser(user).subscribe(
        (response: User) => {
          this.user = response;
        }
      )
    );
    this.user = user;
    this.authenticationService.addUserToLocalCache(user);
    this.authenticationService.saveLanguage(this.translate.currentLang);
    this.getTenders(false);
    this.translateNotifications();
  }

  openTenderModal(tender: Tender, template: TemplateRef<any>) {
    this.originalTender = new Tender();
    this.selectedTender = tender;
    this.modalRef = this.modalService.show(template, { id: 1, class: 'modal-lg' });

    this.subscriptions.push(
      this.tenderService.getTenderOriginal(tender.id).subscribe(
        (response: Tender) => {
          this.originalTender = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  onDateChange(dates: Date[]) {
    this.searchClient = "";
    this.searchTitle = "";
    this.searchDescription = "";
    this.searchCpvCode = "";
    this.bsInlineRangeValue = dates;
    if (this.mode === 'All') this.getTenders(true);
    if (this.mode === 'Show') this.getTendersCpvShowFiltered(true);
    if (this.mode === 'NotShow') this.getTendersCpvNotShowFiltered(true);
  }

  openModal(template: TemplateRef<any>) {
    this.modalRef = this.modalService.show(template, { id: 1, class: 'modal-lg' });
    this.isVisible();
  }

  openModal2(template: TemplateRef<any>) {
    this.modalRef2 = this.modalService.show(template, { id: 2, class: 'second' });
    document.getElementsByClassName('second')[0].parentElement.style.backgroundColor = 'rgba(0, 0, 0, 0.4)';
    if (this.modalRef?.onHide) {
      this.cpvs = null;
    }
  }

  closeFirstModal() {
    if (!this.modalRef) {
      return;
    }

    this.modalRef.hide();
    this.modalRef = null;
  }

  closeShowCpvModalAndApplyFilters(modalId?: number) {
    this.modalService.hide(modalId);
    this.getTendersCpvShowFiltered(true);
  }

  closeNotShowCpvModalAndApplyFilters(modalId?: number) {
    this.modalService.hide(modalId);
    this.getTendersCpvNotShowFiltered(true);
  }

  closeModal(modalId?: number) {
    this.modalService.hide(modalId);
  }

  getTenders(showNotification: boolean): void {
    this.mode = "All";
    if (this.user.sources.length == 0) {
      this.sendNotification(NotificationType.ERROR, `${this.noDataSourceSelected}`);
      return;
    }
    this.translate.get("HEADER.ALL_TENDERS").subscribe((res: string) => {
      this.title = res;
    });
    this.refreshing = true;
    this.subscriptions.push(
      this.tenderService.getTendersForPeriod(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
        (response: Tender[]) => {
          this.tenderService.addTendersToLocalCache(response);
          this.tenders = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} ${this.tendersLoaded}`);
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
    this.unloadedTenders = 0;
  }

  getTendersCpvShowFiltered(showNotification: boolean) {
    this.mode = "Show";
    this.translate.get("HEADER.SHOW_CPV").subscribe((res: string) => {
      this.title = res;
    });
    if (this.user.sources.length == 0) {
      this.sendNotification(NotificationType.ERROR, `${this.noDataSourceSelected}`);
      return;
    }
    this.refreshing = true;
    this.subscriptions.push(
      this.tenderService.getTendersCpvShowFilteredForPeriod(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
        (response: Tender[]) => {
          this.tenderService.addTendersToLocalCache(response);
          this.tenders = response;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} ${this.tendersLoaded}`);
          }
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
    this.unloadedTenders = 0;
  }

  getTendersCpvNotShowFiltered(showNotification: boolean) {
    this.mode = "NotShow";
    this.translate.get("HEADER.NOT_SHOW_CPV").subscribe((res: string) => {
      this.title = res;
    });
    if (this.user.sources.length == 0) {
      this.sendNotification(NotificationType.ERROR, `${this.noDataSourceSelected}`);
      return;
    }
    this.refreshing = true;
    this.subscriptions.push(
      this.tenderService.getTendersCpvNotShowFilteredForPeriod(this.bsInlineRangeValue[0], this.bsInlineRangeValue[1]).subscribe(
        (response: Tender[]) => {
          this.tenderService.addTendersToLocalCache(response);
          this.tenders = response;
          if (showNotification) {
            this.sendNotification(NotificationType.SUCCESS, `${response.length} ${this.tendersLoaded}`);
            this.refreshing = false;
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
    this.unloadedTenders = 0;
  }

  getUserShowCpvs(showNotification: boolean): void {
    this.subscriptions.push(
      this.userCpvShowService.getUserShowCpvs(this.user.username).subscribe(
        (response: UserCpvShow[]) => {
          this.userCpvShowService.addUserShowCpvsToLocalCache(response);
          this.showCpvs = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  getUserNotShowCpvs(showNotification: boolean): void {
    this.subscriptions.push(
      this.userCpvNotShowService.getUserNotShowCpvs(this.user.username).subscribe(
        (response: UserCpvNotShow[]) => {
          this.userCpvNotShowService.addUserNotShowCpvsToLocalCache(response);
          this.notShowCpvs = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public searchUserNotShowCpv(searchItem: string): void {
    const results: UserCpvNotShow[] = [];
    for (const userNotShowCpv of this.userCpvNotShowService.getUserNotShowCpvsFromLocalCache()) {
      if (userNotShowCpv.cpv.code.toLowerCase().indexOf(searchItem.toLowerCase()) !== -1 ||
        userNotShowCpv.cpv.description.toLowerCase().indexOf(searchItem.toLowerCase()) !== -1) {
        results.push(userNotShowCpv);
      }
    }
    this.notShowCpvs = results;
    if (results.length === 0 || !searchItem) {
      this.notShowCpvs = this.userCpvNotShowService.getUserNotShowCpvsFromLocalCache();
    }
  }

  public searchUserShowCpv(searchItem: string): void {
    this.cpvSearchItem = searchItem;
    const results: UserCpvShow[] = [];
    for (const showCpv of this.userCpvShowService.getUserShowCpvsFromLocalCache()) {
      if (showCpv.cpv.code.toLowerCase().indexOf(searchItem.toLowerCase()) !== -1 ||
        showCpv.cpv.description.toLowerCase().indexOf(searchItem.toLowerCase()) !== -1) {
        results.push(showCpv);
      }
    }
    this.showCpvs = results;
  }

  public resetSearch() {
    this.searchClient = "";
    this.searchTitle = "";
    this.searchDescription = "";
    this.searchCpvCode = "";
    this.tenders = this.tenderService.getTendersFromLocalCache();
    this.sendNotification(NotificationType.SUCCESS, `${this.searchFieldsCleared} ${this.tenders.length} ${this.tendersShown}`);
  }

  public searchTender() {
    if (this.searchClient.length == 0 &&
      this.searchTitle.length == 0 &&
      this.searchDescription.length == 0 &&
      this.searchCpvCode.length == 0) {
      this.tenders = this.tenderService.getTendersFromLocalCache();
      this.sendNotification(NotificationType.SUCCESS, `${this.searchFieldsCleared} ${this.tenders.length} ${this.tendersShown}`);
    } else {
      const results: Tender[] = [];
      for (const tender of this.tenderService.getTendersFromLocalCache()) {
        let match = 0;

        if (this.searchClient.length > 0) {
          if (tender.client.toLocaleLowerCase().indexOf(this.searchClient.toLocaleLowerCase()) !== -1) match++;
        } else {
          match++;
        }

        if (this.searchTitle.length > 0) {
          if (tender.title.toLocaleLowerCase().indexOf(this.searchTitle.toLocaleLowerCase()) !== -1) match++;
        } else {
          match++;
        }

        if (this.searchDescription.length > 0) {
          if (tender.description.toLocaleLowerCase().indexOf(this.searchDescription.toLocaleLowerCase()) !== -1) match++;
        } else {
          match++;
        }

        if (this.searchCpvCode.length > 0) {
          for (const cpv of tender.cpv) {
            if (cpv.code.indexOf(this.searchCpvCode) !== -1) match++;
          }
        } else {
          match++;
        }

        if (match >= 4) results.push(tender);
      }
      this.sendNotification(NotificationType.SUCCESS, `${results.length} ${this.tendersFound}`);
      this.tenders = results;
    }
  }



  deleteUserShowCpv(showNotification: boolean, id: number): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userCpvShowService.deleteUserShowCpv(id).subscribe(
        (response: CustomHttpRespone) => {
          this.getUserShowCpvs(false);
          this.refreshing = false;
          this.sendNotification(NotificationType.SUCCESS, this.filterRemoved);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  deleteUserNotShowCpv(showNotification: boolean, id: number): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userCpvNotShowService.deleteUserNotShowCpv(id).subscribe(
        (response: CustomHttpRespone) => {
          this.getUserNotShowCpvs(false);
          this.refreshing = false;
          this.sendNotification(NotificationType.SUCCESS, this.filterRemoved);
        },
        (error: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, error.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  // **********************************

  public searchTenders(searchItem: string): void {
    const results: Tender[] = [];

    for (const tender of this.tenderService.getTendersFromLocalCache()) {
      if (tender.sourceRefNumber.toLowerCase().indexOf(searchItem.toLowerCase()) !== -1) {
        results.push(tender);
      }
    }
    this.tenders = results;
  }

  public onLogOut(): void {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
    this.sendNotification(NotificationType.SUCCESS, `${this.loggedOut}`);
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, this.errorOccured);
    }
  }

  public onAddNewUserCpvNotShow(cpv: Cpv) {
    this.refreshing = true;
    this.subscriptions.push(
      this.userCpvNotShowService.addUserNotShowCpv(this.user.username, cpv.id).subscribe(
        (response: UserCpvNotShow) => {
          this.sendNotification(NotificationType.SUCCESS, `'${response.cpv.code} - ${response.cpv.description}' ${this.filterAdded}`)
          this.getUserNotShowCpvs(false);
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  public searchCpv(emailForm: NgForm) {
    const code = emailForm.value['code'];
    this.subscriptions.push(
      this.cpvService.getCpvsStartsWith(code, this.authenticationService.getLanguage()).subscribe(
        (response: Cpv[]) => {
          this.sendNotification(NotificationType.SUCCESS, `${response.length} ${this.cpvFound}`)
          this.cpvs = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
        }
      )
    );
  }

  public onAddNewUserCpvShow(cpv: Cpv) {
    this.refreshing = true;
    this.subscriptions.push(
      this.userCpvShowService.addUserShowCpv(this.user.username, cpv.id).subscribe(
        (response: UserCpvShow) => {
          this.sendNotification(NotificationType.SUCCESS, `'${response.cpv.code} - ${response.cpv.description}' ${this.filterAdded}`)
          this.getUserShowCpvs(false);
          this.refreshing = false;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
          this.refreshing = false;
        }
      )
    );
  }

  public addCustomUserShowCpv(cpv: Cpv): void {
    if (cpv.code.length < 2 || !cpv.code.endsWith('*')) {
      this.sendNotification(NotificationType.WARNING, this.cpvEmpty);
    } else {
      this.refreshing = true;
      this.subscriptions.push(
        this.userCpvShowService.addCustomUserShowCpv(this.user.username, cpv.code, cpv.description).subscribe(
          (response: UserCpvShow) => {
            this.sendNotification(NotificationType.SUCCESS, `'${response.cpv.code} - ${response.cpv.description}' ${this.filterAdded}`);
            this.getUserShowCpvs(false);
            this.refreshing = false;
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            this.refreshing = false;
          }
        )
      );
      this.getUserShowCpvs(false);
      this.closeModal(2);
    }
  }

  public addCustomUserNotShowCpv(cpv: Cpv): void {
    if (cpv.code.length < 2 || !cpv.code.endsWith('*')) {
      this.sendNotification(NotificationType.WARNING, this.cpvEmpty);
    } else {
      this.refreshing = true;
      this.subscriptions.push(
        this.userCpvNotShowService.addCustomUserNotShowCpv(this.user.username, cpv.code, cpv.description).subscribe(
          (response: UserCpvNotShow) => {
            this.sendNotification(NotificationType.SUCCESS, `'${response.cpv.code} - ${response.cpv.description}' ${this.filterAdded}`);
            this.getUserNotShowCpvs(false);
            this.refreshing = false;
          },
          (errorResponse: HttpErrorResponse) => {
            this.sendNotification(NotificationType.ERROR, errorResponse.error.message);
            this.refreshing = false;
          }
        )
      );
      this.getUserNotShowCpvs(false);
      this.closeModal(2);
    }
  }

  goToLink(url: string) {
    window.open(url, "_blank");
  }

  copyText(text: string){
    this.clipboard.copy(text);
    this.sendNotification(NotificationType.SUCCESS, `Ссылка скопирована в буфер`);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.sub.unsubscribe();
  }

  translateNotifications() {
    this.translate.get("NOTIFICATION.TENDERS_LOADED").subscribe((res: string) => {
      this.tendersLoaded = res;
    });
    this.translate.get("NOTIFICATION.FILTER_REMOVED").subscribe((res: string) => {
      this.filterRemoved = res;
    });
    this.translate.get("NOTIFICATION.SEARCH_FIELDS_CLEARED").subscribe((res: string) => {
      this.searchFieldsCleared = res;
    });
    this.translate.get("NOTIFICATION.TENDERS_SHOWN").subscribe((res: string) => {
      this.tendersShown = res;
    });
    this.translate.get("NOTIFICATION.TENDERS_FOUND").subscribe((res: string) => {
      this.tendersFound = res;
    });
    this.translate.get("NOTIFICATION.LOGGED_OUT").subscribe((res: string) => {
      this.loggedOut = res;
    });
    this.translate.get("NOTIFICATION.ERROR_OCCURED").subscribe((res: string) => {
      this.errorOccured = res;
    });
    this.translate.get("NOTIFICATION.FILTER_ADDED").subscribe((res: string) => {
      this.filterAdded = res;
    });
    this.translate.get("NOTIFICATION.CPV_EMPTY").subscribe((res: string) => {
      this.cpvEmpty = res;
    });
    this.translate.get("NOTIFICATION.NO_DATA_SOURCE_SELECTED").subscribe((res: string) => {
      this.noDataSourceSelected = res;
    });
    this.translate.get("NOTIFICATION.SOURCES_UPDATED").subscribe((res: string) => {
      this.sourcesUpdated = res;
    });
    this.translate.get("NOTIFICATION.CPV_FOUND").subscribe((res: string) => {
      this.cpvFound = res;
    });
  }
}
