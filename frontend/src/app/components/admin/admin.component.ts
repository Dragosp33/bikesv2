import { Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminPage implements OnInit {
  users: any[] = [];

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Retrieve the user's ID token from your authentication service
    this.authService
      .getCurrentUser()
      ?.getIdToken()
      .then((token) =>
        this.userService.getUsers(token).subscribe(
          (response) => {
            this.users = response;
            console.log('Users:', this.users);
          },
          (error) => {
            console.error('Error fetching users:', error);
            this.router.navigate(['/profile']);
          }
        )
      );
    //  console.log(idToken)

    // Make a request to the backend to get the users
    /**/
  }
}
