import { Injectable } from '@angular/core';
import { environment } from './../../environments/environment';
import {
  getAuth,
  createUserWithEmailAndPassword,
  signInWithEmailAndPassword,
  signOut,
  Auth,
  UserCredential,
  User,
} from 'firebase/auth';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.APIURL;
  private auth: Auth;
  private currentUser: User | null = null;

  constructor(private http: HttpClient) {
    // Initialize Firebase Authentication and get a reference to the service
    this.auth = getAuth();
    this.currentUser = this.getCurrentUser();
  }
  /* getcurrentUser() {
    return this.auth.currentUser;
  }*/

  /* getisLoggedIn() {
    return !!this.auth.currentUser;
  }*/

  signUp(
    email: string,
    password: string
  ): Promise<{ message: string; userCredential?: UserCredential }> {
    return createUserWithEmailAndPassword(this.auth, email, password)
      .then((userCredential) => {
        return { message: 'signed up!', userCredential };
      })
      .catch((error) => {
        // You can customize the error messages based on the error code
        let message = 'An error occurred during signup.';

        if (error.code === 'auth/email-already-in-use') {
          message = 'Email is already taken.';
        }

        return { message };
      });
  }

  login(email: string, password: string): Promise<UserCredential> {
    console.log('authentication: ', this.auth);
    console.log('email and pass: ', email, password);
    return signInWithEmailAndPassword(this.auth, email, password);
  }

  logout(): Promise<void> {
    return signOut(this.auth);
  }

  getCurrentUser() {
    // console.log('logged in user:', this.auth.currentUser);
    return this.auth.currentUser;
  }

  getCurrentAuth() {
    return this.auth;
  }

  verifyTokenOnBackend(firebaseToken: any): Observable<any> {
    console.log('firebasetoken: ', firebaseToken);
    const endpoint = `${this.apiUrl}/api/auth/verify-token`;
    const body = { idToken: firebaseToken };
    console.log('THIS IS THE POST BODY: ', body);

    return this.http.post(endpoint, body);
  }

  createCustomerID(uid: any, email: any): Observable<any> {
    console.log();
    const endpoint = `${this.apiUrl}/create-stripe-customer`;
    const body = { uid: uid, email: email };
    return this.http.post(endpoint, body);
  }
}
