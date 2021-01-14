import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import * as ncpConf from "../ncp-config";

@Component({
  selector: 'app-course-selection-view',
  templateUrl: './course-selection-view.component.html',
  styleUrls: ['./course-selection-view.component.css']
})
export class CourseSelectionViewComponent implements OnInit {
  getCoursesResponse: any;

  constructor(private httpClient: HttpClient) {
    const url = ncpConf.default.getAllCoursesUrl;
    console.log("URL:" + url);
    httpClient.get(url, {responseType: 'text'}).subscribe(res => {
      this.getCoursesResponse = JSON.parse(res);
    });
  }

  ngOnInit(): void {
  }

}
