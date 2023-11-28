import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Bikes } from '../interfaces/bikes';
import { Bikes_type } from '../interfaces/bikes_type';

@Injectable({
  providedIn: 'root'
})
export class BikesService {


  private apiUrl = environment.APIURL;

  constructor(private http: HttpClient) { }

  public getBikes(): Observable<Bikes[]> {
    return this.http.get<Bikes[]>(`${this.apiUrl}/bikes`);
  }

  public addBikeType(bike: Bikes): Observable<Bikes> {
    return this.http.post<Bikes>(`${this.apiUrl}/bikes/add`, bike);
  }

  public get_location(bike: Bikes) {
  
    return [Number(bike.longitudine), Number(bike.latitudine)];
  }

}
