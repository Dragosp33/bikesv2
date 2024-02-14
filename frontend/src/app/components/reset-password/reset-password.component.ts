import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent implements OnInit {
  email: string = '';
  password: string = '';
  message: string = '';
  text_class: string = '';

  constructor(public authService: AuthService) {}
  // constructor() {}

  ngOnInit(): void {}

  resetPassword() {
    if (this.email)
      this.authService.resetPassword(this.email).subscribe(
        (responsetwo) => {
          console.log('Response: ', responsetwo);
          this.message = responsetwo.message;
          this.text_class = 'text-success';
        },
        (error) => {
          console.error('Error', error);
          // Handle the verification error
          this.message = error.error.message;
          this.text_class = 'text-danger';
        }
      );
  }
}
