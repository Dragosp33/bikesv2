import { DOCUMENT } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { fromEvent, throttleTime } from 'rxjs';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  enableSticky = false;
  loggedInUser: any;

  constructor(
    @Inject(DOCUMENT) private document: Document,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    fromEvent(window, 'scroll')
      .pipe(throttleTime(20))
      .subscribe((event) => {
        if (this.document.defaultView!.scrollY > 40) {
          this.enableSticky = true;
        } else {
          this.enableSticky = false;
        }
      });
    // this.authService.getCurrentUser()((user) => {
    this.authService.getCurrentAuth().onAuthStateChanged((user) => {
      this.loggedInUser = user;
      // });
    });
  }
}
