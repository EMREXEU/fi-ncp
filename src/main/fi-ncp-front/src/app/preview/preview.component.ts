import { Component, OnInit } from '@angular/core';
import { CoursesService } from '../courses/courses.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css'],
})
export class PreviewComponent implements OnInit {
  elmo = {};
  collapse = {
    generated: false,
    learner: false,
    report: false,
    issuer: false,
    los: {los: { los: {}}},
    loi: {loi: { loi: {}}},
  };

  constructor(private coursesService: CoursesService) {}

  ngOnInit(): void {
    this.coursesService.getPreview().subscribe((data) => {
      switch (data.learner.gender) {
        case 1:
          data.learner.gender = 'male';
          break;
        case 2:
          data.learner.gender = 'female';
          break;
        case 0:
          data.learner.gender = 'not known';
          break;
        case 9:
          data.learner.gender = 'not applicable';
          break;

        default:
          data.learner.gender = 'not known';
          break;
      }
      console.log(data);
      this.elmo = data;
    });
  }
}
