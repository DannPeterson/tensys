import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { NotificationType } from '../enum/notification-type.enum';
import { CustomHttpRespone } from '../model/custom-http-response';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

@Component({
  selector: 'app-password-reset', 
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.css']
})
export class PasswordResetComponent implements OnInit {
  public showLoading: boolean;

  constructor(private router: Router, 
    private authenticationService: AuthenticationService,
    private notificationService: NotificationService,
    private userService: UserService) { }

  ngOnInit(): void {
    this.authenticationService.checkSubscription();
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/tender');
    } 
  }

  onPasswordReset(emailForm: NgForm){
    this.showLoading = true;
    const email = emailForm.value['email'];
    this.userService.resetPassword(email, this.authenticationService.getLanguage()).subscribe(
      (response: CustomHttpRespone) => {
        this.showLoading = false;
        this.sendNotification(NotificationType.SUCCESS, response.message);
      },
      (error: HttpErrorResponse) => {
        this.showLoading = false;
        this.sendNotification(NotificationType.WARNING, error.error.message);
      }
    )
  }

  private sendNotification(notificationType: NotificationType, message: string): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(notificationType, 'An error occurred. Please try again.');
    }
  }
}