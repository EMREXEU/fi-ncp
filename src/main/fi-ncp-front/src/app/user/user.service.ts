import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { IUser } from './user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private url = environment.getUserUrl;
  user$ = this.http.get<IUser>(this.url);

  constructor(private http: HttpClient) {}
}
