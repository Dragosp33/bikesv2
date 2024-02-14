import { Component, OnInit } from '@angular/core';
import { UserDetails } from 'src/app/interfaces/UserDetails';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit {
  protected userProfile?: UserDetails;

  showEmailForm = false;
  newEmail: String = '';
  text_class = '';
  message = '';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.authService
      .getCurrentUser()
      ?.getIdToken(true)
      .then((token) => {
        console.log(token);
        this.authService.verifyTokenOnBackend(token).subscribe(
          (responsetwo) => {
            console.log(responsetwo);
            this.userProfile = {
              uid: responsetwo.uid,
              email: responsetwo.email,
              role: responsetwo.customClaims.admin === true ? 'admin' : 'user',
              customerid: responsetwo.customClaims.customerid,
            };
            console.log('userprofile: ', this.userProfile);
          },
          (verificationError) => {
            console.error('Token verification failed', verificationError);
            // Handle the verification error
          }
        );
      });
  }

  getUserProfile() {
    return this.userProfile;
  }

  logout() {
    this.authService.logout().then((response) => {
      console.log('logged out: ', response);
    });
  }

  showEmailChangeForm() {
    this.showEmailForm = true;
  }

  closeEmailChangeForm() {
    this.showEmailForm = false;
  }

  submitEmailChangeForm() {
    this.message = 'loading..';
    if (this.newEmail.replace(' ', '').length < 5) {
      this.message = 'email should be at least 5 characters long.';
      this.text_class = 'text-danger';
      this.newEmail = '';
      return;
    }

    this.authService
      .getCurrentUser()
      ?.getIdToken(true)
      .then((token) => {
        this.authService.sendEmailChangeForm(token, this.newEmail).subscribe(
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
      });
  }
}
