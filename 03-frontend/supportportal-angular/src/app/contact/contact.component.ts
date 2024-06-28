import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NavigationEnd, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { NotificationType } from '../enum/notification-type.enum';
import { CustomHttpRespone } from '../model/custom-http-response';
import { AuthenticationService } from '../service/authentication.service';
import { NotificationService } from '../service/notification.service';
import { UserService } from '../service/user.service';

declare var FB: any;

@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent implements OnInit {
  public showLoading: boolean;
  public email;

  // Notifications:
  public errorOccured = "";
  public messageEmpty = "";
  public emailIncorrect = "";

  constructor(public translate: TranslateService,
              public authenticationService: AuthenticationService,
              private notificationService: NotificationService,
              private userService: UserService,
              private router: Router) { }

  ngOnInit(): void {
    if (FB != null && FB.XFBML != null)
      FB.XFBML.parse();
    if(this.authenticationService.isUserLoggedIn()) {
      this.email = this.authenticationService.getUserFromLocalCache().email;
    }
    this.router.events.subscribe(x => {
      if(x instanceof NavigationEnd)
      {
        window.scrollTo(0, 0);
      }
    });
    this.translateNotifications();
  }

  onSendMessage(emailForm: NgForm){
    this.showLoading = true;
 
    let language = this.authenticationService.getLanguage();
    let text = emailForm.value['message'];

    if(!this.authenticationService.isUserLoggedIn()) {
      this.email = emailForm.value['email'];
    } 

    if(text.length < 3) {
      this.sendNotification(NotificationType.WARNING, this.messageEmpty);
      this.showLoading = false;
      return;
    }

    if(this.email.length < 5 || !this.email.includes('@')) {
      this.sendNotification(NotificationType.WARNING, "Email некорректен!");
      this.showLoading = false;
      return;
    }

    this.userService.sentMessage(this.email, language, text).subscribe(
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
      this.notificationService.notify(notificationType, this.errorOccured);
    }
  }

  private translateNotifications(){
    this.translate.get("CONTACT.MESSAGE_EMPTY").subscribe((res: string) => {
      this.messageEmpty = res;
    });
    this.translate.get("CONTACT.EMAIL_INCORRECT").subscribe((res: string) => {
      this.emailIncorrect = res;
    });
  }
}
