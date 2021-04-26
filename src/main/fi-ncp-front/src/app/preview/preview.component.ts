import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { I18nService } from 'src/i18n/i18n.service';
import { CoursesService } from '../courses/courses.service';
import { SessionService } from '../session/session.service';

@Component({
  selector: 'app-preview',
  templateUrl: './preview.component.html',
  styleUrls: ['./preview.component.css'],
})
export class PreviewComponent implements OnInit {
  @ViewChild('form') form: ElementRef;

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
  payload = {};
  returnUrl = '';

  constructor(
    private coursesService: CoursesService,
    private i18nService: I18nService,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.coursesService.getPreview().subscribe((data) => {
      switch (data.elmoXml.learner.gender) {
        case 1:
          data.elmoXml.learner.gender = 'male';
          break;
        case 2:
          data.elmoXml.learner.gender = 'female';
          break;
        case 0:
          data.elmoXml.learner.gender = 'not known';
          break;
        case 9:
          data.elmoXml.learner.gender = 'not applicable';
          break;

        default:
          data.elmoXml.learner.gender = 'not known';
          break;
      }
      this.elmo = data.elmoXml;
      this.returnUrl = data.returnUrl;
      this.payload = {
        sessionId: data.sessionId,
        returnMessage: data.returnMessage,
        returnCode: data.returnCode,
        elmo: data.elmo,
      };
      this.loading = false;
      this.courses = this.coursesService.courses;
      this.coursesCount = this.coursesService.count;
      this.modulesCount = this.coursesService.courses.filter(
        (c) => c.isModule
      ).length;
      this.degreesCount = this.coursesService.courses.filter(
        (c) => c.isDegree
      ).length;
      this.credits = this.coursesService.credits;
      this.ssn = data.elmoXml.learner.identifier.filter(
        (id) => id.type === 'nationalIdentifier'
      )[0]?.value;
      this.oid = data.elmoXml.learner.identifier.filter(
        (id) => id.type === 'nationalLearnerId'
      )[0]?.value;
    });
  }

  sendReport(): void {
    this.sessionService.logout().subscribe((_) => {
      this.form.nativeElement.submit();
      // document.getElementById('haka-logout').click();
    });
  }
}
