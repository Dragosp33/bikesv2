import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {
  getAuth,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  Auth,
  UserCredential,
  User,
} from 'firebase/auth';
import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(): Observable<boolean> {
    return new Observable<boolean>((observer) => {
      getAuth().onAuthStateChanged((user) => {
        if (user) {
          // User is authenticated, allow access
          observer.next(true);
          console.log('is logged in ', user);
        } else {
          // User is not authenticated, redirect to login page
          this.router.navigate(['/login']);
          observer.next(false);
        }
        observer.complete();
      });
    });
  }
}
