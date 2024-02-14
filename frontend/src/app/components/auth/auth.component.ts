import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

import { UserManagementActions } from '../../interfaces/UserManagementActions';
import { AuthService } from 'src/app/services/auth.service';

import {
  verifyPasswordResetCode,
  confirmPasswordReset,
  reauthenticateWithCredential,
} from 'firebase/auth';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.css'],
})
export class AuthComponent implements OnInit, OnDestroy {
  ngUnsubscribe: Subject<any> = new Subject<any>();
  actions = UserManagementActions;

  // The user management actoin to be completed
  mode: string = '';
  // Just a code Firebase uses to prove that
  // this is a real password reset.
  actionCode: string = '';

  newPassword: string = '';
  confirmPassword: string = '';

  message: string = '';

  actionCodeChecked: boolean = false;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.activatedRoute.queryParams
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((params) => {
        // if we didn't receive any parameters,
        // we can't do anything
        if (!params) this.router.navigate(['/home']);

        this.mode = params['mode'];
        this.actionCode = params['oobCode'];

        switch (params['mode']) {
          case UserManagementActions.resetPassword:
            {
              // Verify the password reset code is valid.
              const k = this.authService.getCurrentAuth();
              verifyPasswordResetCode(k, this.actionCode)
                .then((email) => {
                  this.actionCodeChecked = true;
                })
                .catch((e) => {
                  // Invalid or expired action code. Ask user to try to
                  // reset the password again.
                  alert(e);
                  // set an error message here.
                  //this.router.navigate(['/login']);
                });
            }
            break;
          case UserManagementActions.recoverEmail:
            {
            }
            break;
          case UserManagementActions.verifyEmail:
            {
            }
            break;
          case UserManagementActions.changeEmail:
            {
              this.authService.verifyEmailCode(this.actionCode).subscribe(
                (result) => {
                  this.actionCodeChecked = true;
                },
                (error) => {
                  console.log('error: ', error);
                  this.message = error.error.message;
                }
              );
            }
            break;
          default: {
            console.log('query parameters are missing');
            this.router.navigate(['/login']);
          }
        }
      });
  }

  ngOnDestroy() {
    // End all subscriptions listening to ngUnsubscribe
    // to avoid memory leaks.
    this.ngUnsubscribe;
    this.ngUnsubscribe.complete();
  }

  /**
   * Attempt to confirm the password reset with firebase and
   * navigate user back to home.
   */
  handleResetPassword() {
    if (this.newPassword != this.confirmPassword) {
      alert('New Password and Confirm Password do not match');
      return;
    }
    // Save the new password.
    const k = this.authService.getCurrentAuth();
    confirmPasswordReset(k, this.actionCode, this.newPassword)
      .then((resp) => {
        // Password reset has been confirmed and new password updated.
        alert('New password has been saved');
        this.router.navigate(['/login']);
      })
      .catch((e) => {
        // Error occurred during confirmation. The code might have
        // expired or the password is too weak.
        alert(e);
      });
  }
}
