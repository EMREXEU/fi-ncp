import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-course-selection-review-view',
  templateUrl: './course-selection-review-view.component.html',
  styleUrls: ['./course-selection-review-view.component.css']
})
export class CourseSelectionReviewViewComponent implements OnInit {
  elmoResponse: any;

  constructor(private httpClient: HttpClient) {
    const url1 = 'http://localhost:9001/test/ncp/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536';

    httpClient.get(url1, {responseType: 'text'
    //  , withCredentials: true
    }).subscribe(res => {
      console.log("Get courses res:" + res);
    });

    const url = 'http://localhost:9001/review?courses=1451865';
    httpClient.get(url, {responseType: 'text'
    //  , withCredentials: true
    }).subscribe(res => {
      console.log("Get selected courses res:" + res);
      this.elmoResponse = JSON.parse(res);
    });
  }

  ngOnInit(): void {
  }

}
