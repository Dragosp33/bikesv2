import { LandingComponent } from './components/landing/landing.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './components/products/products.component';
import { BikesTemplateComponent } from './components/bikes/bikes-template.component';
import { CheckoutComponent } from './components/checkout/checkout.component';
import { CancelComponent } from './components/cancel/cancel.component';
import { SuccessComponent } from './components/success/success.component';
import { LoginComponent } from './components/login/login.component';
import { AuthGuard } from './auth.guard';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminPage } from './components/admin/admin.component';
import { TeamComponent } from './components/team/team.component';
import { SignupComponent } from './components/signup/signup.component';

const routes: Routes = [
  { path: '', component: LandingComponent },
  { path: 'team', component: TeamComponent },
  { path: 'products', component: ProductsComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminPage, canActivate: [AuthGuard] },
  {
    path: 'bikes',
    component: BikesTemplateComponent,
    canActivate: [AuthGuard],
  },
  { path: 'cancel', component: CancelComponent },
  { path: 'success', component: SuccessComponent },
  { path: 'checkout', component: CheckoutComponent },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
