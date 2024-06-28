import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { NotificationType } from '../enum/notification-type.enum';
import { User } from '../model/user';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  public isLoggedIn: boolean;
  public user: User;

  // Notifications:
  public loggedOut = "";
  public errorOccured = "";

  constructor(public router: Router,
    public translate: TranslateService,
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.authenticationService.checkSubscription();
    if (this.authenticationService.isUserLoggedIn()) {
      this.isLoggedIn = true;
      this.user = this.authenticationService.getUserFromLocalCache();
    } else {
      this.isLoggedIn = false;
    }

    if (this.authenticationService.getLanguage() === null) {
      this.translate.use(this.translate.defaultLang);
    } else {
      this.translate.use(this.authenticationService.getLanguage());
    }

    this.translate.onLangChange.subscribe((event: LangChangeEvent) => {
      this.onLangChange();
    });
    this.onLangChange();
  }

  onLangChange() {
    this.authenticationService.saveLanguage(this.translate.currentLang);
    this.translateNotifications();
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

  translateNotifications() {
    this.translate.get("NOTIFICATION.LOGGED_OUT").subscribe((res: string) => {
      this.loggedOut = res;
    });
    this.translate.get("NOTIFICATION.ERROR_OCCURED").subscribe((res: string) => {
      this.errorOccured = res;
    });
  }
}
