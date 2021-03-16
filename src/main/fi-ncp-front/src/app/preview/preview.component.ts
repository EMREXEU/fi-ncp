import { Component, OnInit } from '@angular/core';
import { I18nService } from 'src/i18n/i18n.service';
import { CoursesService } from '../courses/courses.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css'],
})
export class PreviewComponent implements OnInit {
  elmo = {};
  collapse = {
    elmo: true,
    generated: false,
    report: false,
  };
  loading = true;
  courses = [];
  coursesCount = 0;
  modulesCount = 0;
  degreesCount = 0;
  credits = 0;
  ssn = '';
  oid = '';
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;

  constructor(
    private coursesService: CoursesService,
    private i18nService: I18nService
  ) {}

  ngOnInit(): void {
    this.loading = true;
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
      this.elmo = data;
      this.courses = this.coursesService.courses;
      this.coursesCount = this.coursesService.count;
      this.modulesCount = this.coursesService.courses.filter(
        (c) => c.isModule
      ).length;
      this.degreesCount = this.coursesService.courses.filter(
        (c) => c.isDegree
      ).length;
      this.credits = this.coursesService.credits;
      this.ssn = data.learner.identifier.filter(
        (id) => id.type === 'nationalIdentifier'
      )[0]?.value;
      this.oid = data.learner.identifier.filter(
        (id) => id.type === 'nationalLearnerId'
      )[0]?.value;
      this.loading = false;
    });
  }

  sendReport(): void {
    this.coursesService.sendReport();
  }
}
