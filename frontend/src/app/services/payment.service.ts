import { AuthService } from './auth.service';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, from, of, switchMap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  private apiUrl = `${environment.APIURL}/success-details`;

  constructor(private http: HttpClient, private auth: AuthService) {}

  getSuccessDetails(): Observable<any> {
    return new Observable((observer) => {
      // Listen for changes in the authentication state
      const unsubscribe = this.auth
        .getCurrentAuth()
        .onAuthStateChanged((user) => {
          if (user) {
            // User is logged in, get the ID token or perform any necessary actions
            user.getIdToken().then((token) => {
              const stripeSession = sessionStorage.getItem('stripe_sess');
              const headers = new HttpHeaders()
                .set('X-Session-ID', stripeSession || '')
                .set('Authorization', token || '');

              this.http.get<any>(this.apiUrl, { headers }).subscribe(
                (data) => observer.next(data),
                (error) => observer.error(error),
                () => observer.complete()
              );
            });
          } else {
            // User is not logged in, handle accordingly
            observer.error('User is not logged in');
            observer.complete();
          }
        });

      // Return a function to unsubscribe from the auth state changes
      return () => unsubscribe();
    });
  }

  initiateRefund(): Observable<any> {
    const tokenPromise = this.auth.getCurrentUser()?.getIdToken();
    if (!tokenPromise) {
      // Return an observable with an error or empty data
      return new Observable(); // Modify this line as needed
    }

    return from(tokenPromise).pipe(
      switchMap((token) => {
        console.log('token present in switch map:  ', token);
        const headers = new HttpHeaders().set('Authorization', token || '');
        console.log('HEADERS:: ', headers);
        return this.http.post<any>(`${environment.APIURL}/refund`, null, {
          headers,
        });
      })
    );
  }

  getCurrentReservation(): Observable<any> {
    const tokenPromise = this.auth.getCurrentUser()?.getIdToken();
    if (!tokenPromise) {
      // Return an observable with an error or empty data
      return new Observable(); // Modify this line as needed
    }

    return from(tokenPromise).pipe(
      switchMap((token) => {
        const headers = new HttpHeaders().set('Authorization', token || '');
        return this.http.get<any>(`${environment.APIURL}/current-reservation`, {
          headers,
        });
      })
    );
  }
}
