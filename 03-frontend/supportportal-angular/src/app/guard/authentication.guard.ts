import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { NotificationType } from '../enum/notification-type.enum';
import { formatDate } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class AuthenticationGuard implements CanActivate{

  constructor(private authenticationService: AuthenticationService,
    private router: Router,
    private notificationService: NotificationService) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.isUserLoggedIn();
  }

  private isUserLoggedIn(): boolean {
    const language = this.authenticationService.getLanguage();
    
    if (this.authenticationService.isUserLoggedIn() && this.isAccountPaid()) {
      return true;
    }
    if (this.authenticationService.isUserLoggedIn() && !this.isAccountPaid()) {
      this.router.navigate(['/prices']);
      
      if(language === 'ru') {
        this.notificationService.notify(NotificationType.ERROR, `Срок действия аккаунта закончился`);
      } else {
        this.notificationService.notify(NotificationType.ERROR, `Account expired`);
      }
      
      return true;
    }

    this.router.navigate(['/login']);

    if(language === 'ru') {
      this.notificationService.notify(NotificationType.ERROR, `Необходимо войти, чтобы попасть на эту страницу`);
    } else {
      this.notificationService.notify(NotificationType.ERROR, `You need to log in to access this page`);
    }
    return false;
  }

  private isAccountPaid(): boolean {
    if (formatDate(this.authenticationService.getUserFromLocalCache().paidUntil, 'yyyy-MM-dd', 'en_US') < formatDate((new Date), 'yyyy-MM-dd', 'en_US')) {
      return false;
    }
    return true;
  }
}