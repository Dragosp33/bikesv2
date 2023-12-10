import { Component, OnInit } from '@angular/core';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-success',
  templateUrl: './success.component.html',
  styleUrls: ['./success.component.css'],
})
export class SuccessComponent implements OnInit {
  successDetails: any = null;

  constructor(public paymentService: PaymentService) {}

  //ngOnInit(): void {}

  ngOnInit(): void {
    // Retrieve the session ID and expiration timestamp from sessionStorage
    this.paymentService.getSuccessDetails().subscribe(
      (data) => {
        if (data === undefined) {
          // Handle case when user is not authenticated
          console.log('User is not authenticated');
        } else {
          // Handle the success details when available
          this.successDetails = data;
          console.log('Success Details:', this.successDetails);
        }
      },
      (error) => {
        console.error('Error fetching success details:', error);
      }
    );
  }
}
