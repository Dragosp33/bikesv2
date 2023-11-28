// user.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = `${environment.APIURL}/api/auth/admin/users`;

  constructor(private http: HttpClient) {}

  getUsers(idToken: string): Observable<any> {
    const headers = new HttpHeaders({ Authorization: idToken });

    return this.http.get<any>(this.apiUrl, { headers });
  }
}
