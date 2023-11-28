import { environment } from './../../environments/environment';

import { Bikes_type } from '../interfaces/bikes_type';
import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';



@Injectable({
  providedIn: 'root'
})
export class bikes_typeService {

  private apiUrl = environment.APIURL;

  constructor(private http: HttpClient) { }

  public getBikesType(): Observable<Bikes_type[]> {
    return this.http.get<Bikes_type[]>(`${this.apiUrl}/bikes_type`);
  }

  public addBikeType(bike: Bikes_type): Observable<Bikes_type> {
    return this.http.post<Bikes_type>(`${this.apiUrl}/bikes_type/add`, bike);
  }
}
