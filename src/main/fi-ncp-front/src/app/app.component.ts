import {Component} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'fi-ncp-front';
  plainTextRes: any;
  jsonRes: any;

  constructor(private httpClient: HttpClient) {

    const url = 'http://localhost:9001/test/ncp/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536';

    // Default response type: JSON
    httpClient.get(url, {responseType: 'text'}).subscribe(res => this.plainTextRes = res);

    httpClient.get(url).subscribe(res => this.jsonRes = res);

  }
}
