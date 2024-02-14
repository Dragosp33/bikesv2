import { PaymentService } from './../../services/payment.service';
import { CheckoutComponent } from '../checkout/checkout.component';
import {
  Component,
  OnInit,
  ViewChild,
  AfterViewInit,
  ViewChildren,
  QueryList,
  OnDestroy,
} from '@angular/core';
import { Bikes } from 'src/app/interfaces/bikes';
import { environment } from 'src/environments/environment';
import { bikes_typeService } from '../../services/bikes_type.service';
import { BikesService } from '../../services/bikes.service';
import { GoogleMap, MapInfoWindow, MapMarker } from '@angular/google-maps';
import { HttpErrorResponse } from '@angular/common/http';

import { Observable, Subscription, interval, timer, of } from 'rxjs';
import { map, takeUntil } from 'rxjs/operators';
import { response } from 'express';

@Component({
  selector: 'app-bike-template',
  templateUrl: './bikes-template.component.html',
  // templateUrl: './bikestest.template.component.html',
  styleUrls: ['./bikes-template.component.css'],
})
export class BikesTemplateComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  public loading = false;
  public message = '';
  public apikey = environment.google_maps_api;
  public bikes: Bikes[];
  public reservation: any;
  private intervalSubscription!: Subscription;
  public remainingTime$: Observable<string> = of('');
  public percentage: number | null = null;

  public mapOptions: google.maps.MapOptions = {
    //center: { lat: 44.439663, lng: 26.096306 },
    center: { lat: 22, lng: 12 },
    zoom: 4,
  };

  // TODO:

  // CENTER MAP => FIT BOUNDS

  //public markers: google.maps.Marker;

  constructor(
    private BikesService: BikesService,
    private CheckoutComponent: CheckoutComponent,
    private paymentService: PaymentService // private intervalSubscription: Subscription
  ) {
    this.bikes = [];
    // this.intervalSubscription = interval(60000);
  }

  ngOnInit(): void {
    this.get_all_bikes();
    // this.get_current_reservation();
    this.setInitialTimer();

    // Fetch updated data every minute
    this.intervalSubscription = interval(60000).subscribe(() => {
      this.get_all_bikes();
      this.get_current_reservation();
    });
  }

  ngAfterViewInit(): void {
    console.log(this.map);
  }

  ngOnDestroy(): void {
    // Unsubscribe from the interval observable to avoid memory leaks
    if (this.intervalSubscription) {
      this.intervalSubscription.unsubscribe();
    }
  }

  @ViewChild(GoogleMap) map!: GoogleMap;
  @ViewChild(MapInfoWindow) infoWindow!: MapInfoWindow;

  protected get_coords(marker: Bikes) {
    let lat = Number(marker.latitudine);
    let long = Number(marker.longitudine);
    let coordinates = new google.maps.LatLng(lat, long);
    return coordinates;
  }

  public get_all_bikes(): any {
    /*this.BikesService.getBikes().subscribe({
      error: (e) => console.log(e),
      complete: this.bikes = Response
    })*/
    this.BikesService.getBikes().subscribe(
      (Response: Bikes[]) => {
        this.bikes = Response;
        console.log('getbikesresponse::: ', Response);
      },
      (error: HttpErrorResponse) => {
        alert(error.message);
        console.log(error);
      }
    );
  }

  public get_current_reservation(): void {
    this.paymentService.getCurrentReservation().subscribe(
      (response) => {
        this.reservation = response;
        console.log('Current Reservation:', response);
        //  this.setupLiveTimer();
      },
      (error) => {
        console.error('Error fetching current reservation:', error);
        this.reservation = null;
        this.remainingTime$ = of('');
      }
    );
  }

  initiate_refund(): void {
    this.loading = true;
    this.paymentService.initiateRefund().subscribe(
      (response) => {
        this.loading = false;

        this.remainingTime$ = of('');
        this.message =
          'Successfully refunded, you will shortly receive an email.';
        console.log('response in refund:  ', response);
        setTimeout(() => {
          this.reservation = null;
        }, 5000);
      },
      (error) => {
        this.loading = false;
        this.message = 'Sorry, we could not process your refund...';
        console.error('Error fetching current reservation:', error);
      }
    );
  }

  public setInitialTimer(): void {
    this.paymentService.getCurrentReservation().subscribe(
      (response) => {
        this.reservation = response;
        console.log('Current Reservation:', response);
        this.setupLiveTimer();
      },
      (error) => {
        console.error('Error fetching current reservation:', error);
        this.reservation = null;
      }
    );
  }

  private setupLiveTimer(): void {
    if (this.reservation && this.reservation.expiryDate) {
      console.log('expiry date:::::::::', this.reservation.expiryDate);
      //const expiryTime = new Date(this.reservation.expiryDate).getTime();
      // const expiryTime = Date.parse())
      const dateParts = this.reservation.expiryDate.split(/[-T:.]/);
      const expiryTime = Date.UTC(
        parseInt(dateParts[0], 10),
        parseInt(dateParts[1], 10) - 1, // months are 0-indexed
        parseInt(dateParts[2], 10),
        parseInt(dateParts[3], 10),
        parseInt(dateParts[4], 10),
        parseInt(dateParts[5], 10)
        // parseInt(dateParts[6], 10)
      );
      console.log('expiry time::::', expiryTime);

      // Create an observable that emits every second
      const timer$ = timer(0, 1000);

      // Calculate remaining time every second
      this.remainingTime$ = timer$.pipe(
        map(() => {
          const now = Date.now();
          //  console.log('NOW: ', now, 'EXPIRY: ', expiryTime);

          const timeDifference = expiryTime - now;

          if (timeDifference <= 0) {
            return 'Expired';
          } else {
            const minutes = Math.floor(timeDifference / (1000 * 60));
            const seconds = Math.floor((timeDifference % (1000 * 60)) / 1000);
            const maxTime = 3600 * 1000; // 1 hour in milliseconds
            const remainingPercentage = (timeDifference / maxTime) * 100;
            this.percentage = (timeDifference / maxTime) * 100;
            return `${minutes}mins:${seconds}s`;
          }
        }),
        takeUntil(timer(expiryTime)) // Stop the timer when expiry time is reached
      );
    } else {
      this.remainingTime$ = of(''); // No reservation or expiry date, emit an empty string
      this.percentage = null;
    }
  }

  openInfoWindow(marker: MapMarker, location: google.maps.LatLng) {
    this.infoWindow.open(marker, true);
  }

  @ViewChildren(MapInfoWindow)
  infoWindowsView!: QueryList<MapInfoWindow>;

  openInfoWindow2(marker: MapMarker, windowIndex: number) {
    /// stores the current index in forEach
    let curIdx = 0;
    this.infoWindowsView.forEach((window: MapInfoWindow) => {
      if (windowIndex === curIdx) {
        window.open(marker);
        curIdx++;
      } else {
        window.close();
        curIdx++;
      }
    });
  }
  /*
  public pay_this(name1: string, amount1: number) {
    //window.open("localhost:4200/bikes", "_blank");
    this.CheckoutComponent.pay(name1, amount1*10);
    console.log(name1, amount1);
  }
*/
  public pay_this(id_bike: string) {
    //window.open("localhost:4200/bikes", "_blank");
    var id2 = Number(id_bike);
    this.CheckoutComponent.pay(id2);
    //console.log(name1, amount1);
  }
}
