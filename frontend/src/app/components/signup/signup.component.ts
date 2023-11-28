import { Component } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
})
export class SignupComponent {
  email: string = '';
  password: string = '';
  signupMessage: string = '';

  constructor(private authService: AuthService) {}

  signUp(): void {
    this.authService
      .signUp(this.email, this.password)
      .then((result) => {
        this.signupMessage = result.message;

        // Optionally, you can redirect the user or perform additional actions
      })
      .catch((error) => {
        console.error('Signup error:', error);
      });
  }
}
