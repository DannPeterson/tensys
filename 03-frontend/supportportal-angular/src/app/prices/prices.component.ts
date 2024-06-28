import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { NotificationType } from '../enum/notification-type.enum';
import { CustomHttpRespone } from '../model/custom-http-response';
import { User } from '../model/user';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-prices',
  templateUrl: './prices.component.html',
  styleUrls: ['./prices.component.css']
})
export class PricesComponent implements OnInit {
  public showLoadingPlan1: boolean;
  public showLoadingPlan2: boolean;
  public showLoadingPlan3: boolean;
  public buttonsDisabled: boolean;

  public user: User | undefined;

  // Notifications:
  public errorOccured = "";

  constructor(private router: Router,
    public translate: TranslateService,
    public authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private userService: UserService) { }

  ngOnInit(): void {
    this.authenticationService.checkSubscription();
    if (this.authenticationService.isUserLoggedIn()) {
      this.user = this.authenticationService.getUserFromLocalCache();
    } 
  }

  onRenewalRequest(plan: string) {
    if (this.authenticationService.isUserLoggedIn()) {
      this.buttonsDisabled = true;
      if (plan == 'month') this.showLoadingPlan1 = true;
      if (plan == 'year') this.showLoadingPlan2 = true;
      if (plan == 'corporative') this.showLoadingPlan3 = true;

      this.userService.subscriptionRequest(this.user.email, plan, this.authenticationService.getLanguage()).subscribe(
        (response: CustomHttpRespone) => {
          this.buttonsDisabled = false;
          if (plan == 'month') this.showLoadingPlan1 = false;
          if (plan == 'year') this.showLoadingPlan2 = false;
          if (plan == 'corporative') this.showLoadingPlan3 = false;
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },
        (error: HttpErrorResponse) => {
          this.buttonsDisabled = false;
          if (plan == 'month') this.showLoadingPlan1 = false;
          if (plan == 'year') this.showLoadingPlan2 = false;
          if (plan == 'corporative') this.showLoadingPlan3 = false;
          this.sendNotification(NotificationType.WARNING, error.error.message);
        }
      )
    }
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, this.errorOccured);
    }
  }

  translateNotifications() {
    this.translate.get("NOTIFICATION.ERROR_OCCURED").subscribe((res: string) => {
      this.errorOccured = res;
    });
  }
}