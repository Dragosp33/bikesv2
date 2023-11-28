import { bikes_typeService } from './services/bikes_type.service';
import { Bikes_type } from './interfaces/bikes_type';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Bikes4All';
}
