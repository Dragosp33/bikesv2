import { firebaseConfig } from './../environments/environment';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { ProductsComponent } from './components/products/products.component';
import { CarouselModule } from 'ngx-owl-carousel-o';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LandingComponent } from './components/landing/landing.component';
import { PricingComponent } from './components/pricing/pricing.component';
import { HttpClientModule } from '@angular/common/http';
import { GoogleMapsModule } from '@angular/google-maps';
import { BikesTemplateComponent } from './components/bikes/bikes-template.component';
import { SuccessComponent } from './components/success/success.component';
import { CancelComponent } from './components/cancel/cancel.component';
import { CheckoutComponent } from './components/checkout/checkout.component';

import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { FormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { ProfileComponent } from './components/profile/profile.component';
import { AdminPage } from './components/admin/admin.component';
import { TeamComponent } from './components/team/team.component';
import { OurServicesComponent } from './components/our-services/our-services.component';
import { AboutComponent } from './components/about/about.component';
import { HeroComponent } from './components/hero/hero.component';
import { SignupComponent } from './components/signup/signup.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    ProductsComponent,
    LandingComponent,
    PricingComponent,
    BikesTemplateComponent,
    SuccessComponent,
    CancelComponent,
    CheckoutComponent,
    LoginComponent,
    ProfileComponent,
    AdminPage,
    TeamComponent,
    AboutComponent,
    OurServicesComponent,
    HeroComponent,
    SignupComponent,
  ],
  imports: [
    BrowserModule,

    AppRoutingModule,
    BrowserAnimationsModule,
    CarouselModule,
    HttpClientModule,
    GoogleMapsModule,
    FormsModule,
    // AngularFireModule.initializeApp(environment.firebase),
    //AngularFireAuthModule,
  ],
  providers: [CheckoutComponent],
  bootstrap: [AppComponent],
})
export class AppModule {
  constructor() {
    initializeApp(firebaseConfig);
  }
}
