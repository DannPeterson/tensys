import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { AuthenticationGuard } from './guard/authentication.guard';
import { TenderComponent } from './tender/tender.component';
import { AboutComponent } from './about/about.component';
import { PricesComponent } from './prices/prices.component';
import { ContactComponent } from './contact/contact.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { MainComponent } from './main/main.component';
import { RegistrationSuccessComponent } from './registration-success/registration-success.component';


const routes: Routes = [
  { path: 'main', component: MainComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'registration-success', component: RegistrationSuccessComponent },
  { path: 'how-to-use', component: AboutComponent },
  { path: 'prices', component: PricesComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'password-reset', component: PasswordResetComponent },
  { path: 'tender', component: TenderComponent, canActivate: [AuthenticationGuard]},
  { path: '', redirectTo: '/main', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}