import { getAuth } from 'firebase/auth';
import {
  AfterContentInit,
  AfterViewChecked,
  AfterViewInit,
  Component,
  OnInit,
} from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  template: `
    <div *ngIf="this.authService.getCurrentUser()">
      <!-- Display user email in the navbar -->
      {{ authService.getCurrentUser()?.email }}
      <!-- Add logout button -->
      <button (click)="logout()" class="btn btn-primary">Log out</button>
    </div>
    <div
      *ngIf="!this.authService.getCurrentUser()"
      class="container-sm d-flex align-items-center justify-content-center mt-3"
      style="min-height: 400px;"
    >
      <div class="card" style="max-width: 400px">
        <div class="card-header">
          <h6 class="card-title">
            Log in to be able to access our reservation system
          </h6>
          <h6>For testing purposes, use this admin account:</h6>
          <ul>
            <li>dragosp0201@gmail.com</li>
            <li>parola2</li>
          </ul>
        </div>
        <div class="card-body">
          <!-- Add login button or other content for non-authenticated users -->
          <form (ngSubmit)="login()" class="d-flex flex-column">
            <input
              type="email"
              [(ngModel)]="email"
              name="email"
              placeholder="Email"
              required
              class="mb-3"
            />
            <input
              type="password"
              [(ngModel)]="password"
              placeholder="Password"
              name="password"
              required
              class="mb-3 px-2"
            />
            <button type="submit" class="btn btn-primary">Login</button>
            <hr />
            <a class="btn btn-dark" [routerLink]="['/signup']">Or Sign up</a>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class LoginComponent implements AfterContentInit {
  email: string = '';
  password: string = '';

  constructor(public authService: AuthService, private router: Router) {}

  ngAfterContentInit(): void {
    getAuth().onAuthStateChanged((user) => {
      if (user) {
        this.router.navigate(['/profile']);
      }
    });
  }

  login() {
    console.log('email and pass in login: ', this.email, this.password);
    this.authService
      .login(this.email, this.password)
      .then((response) => {
        console.log('Logged in successfully', response);
        console.log('token: ', response);
        // this.authService.currentUser.getIdToken(/* forceRefresh */ true).then(function(idToken)
        this.authService
          .getCurrentUser()
          ?.getIdToken(true)
          .then((token) => {
            console.log(token);
            this.authService.verifyTokenOnBackend(token).subscribe(
              (responsetwo) => {
                console.log(responsetwo);
                this.router.navigate(['/profile']);
              },
              (verificationError) => {
                console.error('Token verification failed', verificationError);
                // Handle the verification error
              }
            );
          });

        // You can redirect or perform other actions upon successful login
      })
      .catch((error) => console.error('Login failed', error));
  }
  logout() {
    this.authService.logout().then((response) => {
      console.log('logged out: ', response);
    });
  }
}
