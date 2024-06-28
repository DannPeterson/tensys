import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthenticationService } from './service/authentication.service';
import { UserService } from './service/user.service';
import { AuthInterceptor } from './interceptor/auth.interceptor';
import { AuthenticationGuard } from './guard/authentication.guard';
import { NotificationModule } from './notification.module';
import { NotificationService } from './service/notification.service';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { TenderComponent } from './tender/tender.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ModalModule } from 'ngx-bootstrap/modal';
import { BsDatepickerModule } from 'ngx-bootstrap/datepicker';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { AboutComponent } from './about/about.component';
import { PricesComponent } from './prices/prices.component';
import { ContactComponent } from './contact/contact.component';
import { FooterComponent } from './footer/footer.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { MainComponent } from './main/main.component';
import { RegistrationSuccessComponent } from './registration-success/registration-success.component';
import { NgcCookieConsentModule, NgcCookieConsentConfig } from 'ngx-cookieconsent';
import { environment } from 'src/environments/environment';
import { NavbarComponent } from './navbar/navbar.component';
import { SanitizeHtmlPipePipe } from './sanitize-html-pipe.pipe';

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}


const cookieConfig: NgcCookieConsentConfig = {
  cookie: {
    domain: environment.cookieDomain
  },
  position: "bottom-left",
  theme: "classic",
  palette: {
    popup: {
      background: "#212529",
      text: "#ffffff",
      link: "#ffffff"
    },
    button: {
      background: "#ffc107",
      text: "#000000",
      border: "transparent"
    }
  },
  type: "info",
  content: {
    message: "This website uses cookies to ensure you get the best experience on our website.",
    dismiss: "Got it!",
    policy: "Cookie Policy"
  }
};



@NgModule({
    declarations: [
      AppComponent,
      LoginComponent,
      RegisterComponent,
      TenderComponent,
      AboutComponent,
      PricesComponent,
      ContactComponent,
      FooterComponent,
      PasswordResetComponent,
      MainComponent,
      RegistrationSuccessComponent,
      NavbarComponent,
      SanitizeHtmlPipePipe
    ],
    imports: [
      NgcCookieConsentModule.forRoot(cookieConfig),
      BrowserModule,
      AppRoutingModule,
      FormsModule,
      HttpClientModule,
      TranslateModule.forRoot({
        loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
        },
        defaultLanguage: 'en'
      }),
      NotificationModule,
      BrowserAnimationsModule,
      ModalModule.forRoot(),
      BsDatepickerModule.forRoot(),
      TooltipModule.forRoot()
    ],
    providers: [NotificationService, AuthenticationGuard, AuthenticationService, UserService,
      { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
    bootstrap: [AppComponent]
  })
export class AppModule { }
