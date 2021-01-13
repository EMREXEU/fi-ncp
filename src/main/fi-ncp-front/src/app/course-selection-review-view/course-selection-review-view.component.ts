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
    // TODO: this is test mock URL
    const url = 'http://localhost:9001/test/ncp_review/?sessionId=1234&returnUrl=234123&unique-id=urn:mace:terena.org:schac:personalUniqueID:fi:FIC:180766-2213&SHIB_funetEduPersonLearnerId=1.2.246.562.24.17488477125&SHIB_schacHomeOrganization=oamk.fi&SHIB_schacHomeOrganizationId=02536&courses=1485021';
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
