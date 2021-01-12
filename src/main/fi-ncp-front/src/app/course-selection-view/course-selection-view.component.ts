import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-course-selection-view',
  templateUrl: './course-selection-view.component.html',
  styleUrls: ['./course-selection-view.component.css']
})
export class CourseSelectionViewComponent implements OnInit {
  getCoursesResponse: any;

  constructor(private httpClient: HttpClient) {
    const url = 'http://localhost:9001/test/ncp/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536';

    httpClient.get(url, {responseType: 'text'}).subscribe(res => {
      this.getCoursesResponse = JSON.parse(res);
    });
  }

  ngOnInit(): void {
  }

}
