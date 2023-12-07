import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.css'],
})
export class SuccessComponent implements OnInit {
  sessionId: any = 'nosession';
  constructor() {}

  //ngOnInit(): void {}

  ngOnInit(): void {
    // Retrieve the session ID and expiration timestamp from sessionStorage
    this.sessionId = sessionStorage.getItem('stripeSessionId');
    const expirationTime = sessionStorage.getItem('stripeSessionExpiration');

    if (this.sessionId && expirationTime) {
      const currentTime = new Date().getTime();
      if (currentTime < parseInt(expirationTime, 10)) {
        // The session is still valid
        // Call your backend to retrieve payment details using the session ID
        //this.getPaymentDetails(this.sessionId);
      } else {
        // Session has expired, handle accordingly (e.g., redirect to an error page)
        console.log('Session has expired');
      }
    }
  }
}
