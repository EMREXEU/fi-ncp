import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import * as ncpConf from "../ncp-config";

@Component({
  selector: 'app-course-selection-review-view',
  templateUrl: './course-selection-review-view.component.html',
  styleUrls: ['./course-selection-review-view.component.css']
})
export class CourseSelectionReviewViewComponent implements OnInit {
  elmoResponse: any;

  constructor(private httpClient: HttpClient) {
    const url = ncpConf.default.getSelectedCoursesUrl;
    console.log("URL:" + url);
    httpClient.get(url, {
      responseType: 'text'
      //  , withCredentials: true
    }).subscribe(res => {
      console.log("Get selected courses res:" + res);
      this.elmoResponse = JSON.parse(res);
    });
  }

  ngOnInit(): void {
  }

}
