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
        //  console.log(result);
        console.log(
          'signup credential',
          result.userCredential?.user.email,
          result.userCredential?.user.uid
        );
        const id = result.userCredential?.user.uid;
        const email = result.userCredential?.user.email;
        this.authService.createCustomerID(id, email).subscribe(
          (responsetwo) => {
            console.log('customer create response: ', responsetwo);
          },
          (customerError) => {
            console.error('Customer creation error', customerError);
            // Handle the verification error
          }
        );
      })
      //this.signupMessage = result.message;

      // Optionally, you can redirect the user or perform additional actions

      .catch((error) => {
        console.error('Signup error:', error);
      });
  }
}
