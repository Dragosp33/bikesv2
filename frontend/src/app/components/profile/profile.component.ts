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
}
