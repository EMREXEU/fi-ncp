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
    generated: false,
    report: false
  };
  loading = true;
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;

  constructor(private coursesService: CoursesService, private i18nService: I18nService) {}

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
      console.log(data);
      this.elmo = data;
      this.loading = false;
    });
  }
}
