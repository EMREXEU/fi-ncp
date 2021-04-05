import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { shareReplay } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { ISession } from './session';

@Injectable({
  providedIn: 'root',
})
export class SessionService {
  private url = environment.getSessionUrl;
  private logoutUrl = environment.getLogoutUrl;
  session$ = this.http.get<ISession>(this.url).pipe(shareReplay(1));

  constructor(private http: HttpClient) {}

  logout(): Observable<any> {
    return this.http.get(this.logoutUrl);
  }
}
