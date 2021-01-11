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
  selectedCourses: Set<any>;
  organizationNames: Map<any, any>;


  constructor(private httpClient: HttpClient) {

    const url = 'http://localhost:9001/test/ncp/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536';
    this.selectedCourses = new Set();
    this.organizationNames = new Map<any, any>();
    // TODO: fetch real mapping
    this.organizationNames.set("02536", "Testikoulu");

    // Default response type: JSON
    httpClient.get(url, {responseType: 'text'}).subscribe(res => {
      this.jsonRes = JSON.parse(res);
      this.plainTextRes = res;
      console.log("jsonRes:" + this.jsonRes);
      console.log("plainTextRes:" + this.plainTextRes);
    });

    httpClient.get(url).subscribe(res => this.jsonRes = res);
    console.log("HELLOOOO!");

  }

  courseSelected(x: any) {
    // TODO: add selected course / remove deselected...
    // TODO: Update checkbox
    // TODO: Use map (no dupplicates)

    if (this.selectedCourses.has(x)) {
      this.selectedCourses.delete(x);
      console.log("Removed course:" + x);
    } else {
      console.log("Added course:" + x);
      this.selectedCourses.add(x);
    }

    console.log("Courses to review:" + Array.from(this.selectedCourses.values()).toString());

  }

  reviewSelectedCourses() {
    console.log("Courses to review:" + Array.from(this.selectedCourses.values()).toString());
    window.alert("Valitsit kurssit:" + Array.from(this.selectedCourses.values()).toString());
  }
}
