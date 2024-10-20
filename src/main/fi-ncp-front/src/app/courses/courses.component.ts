import {
  AfterContentChecked,
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
} from '@angular/core';
import { Router } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { I18nService } from 'src/i18n/i18n.service';
import { SessionService } from '../session/session.service';
import { Opintosuoritus } from './course';
import { CoursesService } from './courses.service';
import Utils from "../utils";

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css'],
})
export class CoursesComponent
  implements OnDestroy, AfterViewInit, AfterContentChecked {
  private form!: ElementRef;
  @ViewChild('form', { static: false }) set f(f: ElementRef) {
    if (f) {
      this.form = f;
    }
  }
  lang$ = this.i18nService.langAction$;
  session$ = this.sessionService.session$;
  i18n = this.i18nService.i18n;

  private sessionSub: any;
  private coursesSub: any;

  // @ts-ignore
  coursesByIssuer: { [key: string]: Opintosuoritus[] };
  loading = true;
  issuers: string[] = [];
  selectedIssuer = '';
  selectedCourses: Set<string> = new Set();
  creditsCount = 0;
  session: any;
  returnCode = 'SOMETHING_ELSE';
  returnMessage = '';
  error = '';
  ready = false;

  constructor(
    private coursesService: CoursesService,
    private router: Router,
    private i18nService: I18nService,
    private sessionService: SessionService
  ) {}

  ngAfterViewInit(): void {
    this.loading = true;
    this.sessionSub = this.sessionService.session$.subscribe((session) => {
      this.session = session;
      this.coursesSub = this.coursesService.coursesWithIssuers$
        .pipe(
          catchError((err) => {
            // Error logging is number one priority
            // Global error handler can't catch this error because it is explicitly handled here.
            this.coursesService.postError(
  `Source: ngAfterViewInit catchError handler\n
              Name: ${err.name}\n
              Message: ${err.message}\n
              Error: ${err.error}\n
              Status: ${err.status}\n
              Stack:${err.stack}`
            );

            if (err.error === 'Either Unique ID or Learner ID required') {
              this.returnMessage = err;
            }
            if (err.status === 500) {
              this.returnMessage = 'Internal Server Error';
            }

            if (!this.returnMessage) {
              this.returnMessage = "Unknown Emrex Error, please check Emrex browser console log for more details."
            }
            this.returnCode = 'NCP_ERROR';
            this.ready = true;
            this.loading = false;
            console.log("If you can read this please report this error:");
            console.error(err);
            return EMPTY;
          })
        )
        .subscribe((coursesByIssuer) => {
          this.coursesByIssuer = coursesByIssuer;
          this.issuers = Object.keys(coursesByIssuer);
          if (this.issuers.length === 0) {
            this.returnCode = 'NCP_NO_RESULTS';
            this.returnMessage = '';
            this.ready = true;
            console.log("Courses not found...");
          }
          if (this.issuers.length === 1) {
            this.selectedIssuer = this.issuers[0];
          }
          this.loading = false;
          if (this.coursesService.selectedIssuer) {
            this.selectedIssuer = this.coursesService.selectedIssuer;
          }
          if (
            this.coursesService.selectedCourses &&
            this.coursesService.selectedCourses.length > 0
          ) {
            this.coursesService.selectedCourses.forEach((course) =>
              this.selectCourse(course)
            );
          }
        });
    });
  }

  ngAfterContentChecked(): void {
    if (
      environment.production &&
      this.form &&
      this.ready &&
      this.session.returnUrl &&
      (document.getElementById('returnCode') as HTMLInputElement).value ===
        this.returnCode
    ) {
      this.sessionService.logout().subscribe((_) => {
        this.form.nativeElement.submit();
        // document.getElementById('haka-logout').click();
      });
    }
    if (
      !environment.production &&
      this.form &&
      this.ready &&
      (document.getElementById('returnCode') as HTMLInputElement).value ===
        this.returnCode
    ) {
      // this.sessionService.logout().subscribe((_) => {
        // this.form.nativeElement.submit();
        // document.getElementById('haka-logout').click();
      // });
      console.log('no redirects in dev');
    }
  }

  ngOnDestroy(): void {
    if (this.sessionSub) {
      this.sessionSub.unsubscribe();
    }
    if (this.coursesSub) {
      this.coursesSub.unsubscribe();
    }
  }

  selectIssuer(event: Event): void {
    const target = (event.target as unknown) as { value: string };
    this.selectedIssuer = target.value;
    this.creditsCount = 0;
    this.selectedCourses.clear();
  }

  courseSelected(course: string | undefined): boolean {
    if (course === undefined) return false;
    return this.selectedCourses.has(course);
  }

  selectCourse(course: string): void {
    const selected = this.coursesByIssuer[this.selectedIssuer].find(
      (c) => c.avain === course
    );
    if (selected &&
      (this.courseSelected(selected.degree) ||
      this.courseSelected(selected.module))
    ) {
      return;
    }
    if (this.selectedCourses.has(course)) {
      const courseToRemove = this.coursesByIssuer[this.selectedIssuer].find(
        (c) => c.avain === course
      );
      if (courseToRemove) {
        courseToRemove.hasPart?.forEach((los1) => {
          los1.hasPart?.forEach((los2) => {
            this.selectedCourses.delete(los2.avain);
          });
          this.selectedCourses.delete(los1.avain);
        });
      }
      this.selectedCourses.delete(course);
    } else {
      this.selectedCourses.add(course);
      const courseToAdd = this.coursesByIssuer[this.selectedIssuer].find(
        (c) => c.avain === course
      );
      if (courseToAdd) {
        courseToAdd.hasPart?.forEach((los1) => {
          los1.hasPart?.forEach((los2) => {
            this.selectedCourses.add(los2.avain);
          });
          this.selectedCourses.add(los1.avain);
        });
      }
      this.selectedCourses.add(course);
    }
    this.creditsCount = 0;
    Array.from(this.selectedCourses.values()).forEach((key) => {
      const selectedCourse = this.coursesByIssuer[this.selectedIssuer].find(
        (c) => c.laji === '2' && !c.isModule && c.avain === key
      );
      if (selectedCourse) {
        this.creditsCount += +selectedCourse.laajuus.opintopiste;
      }
    });
  }

  reviewSelectedCourses(): void {
    this.coursesService.count = this.calculateSize();
    const partOfModule = this.coursesByIssuer[this.selectedIssuer].filter(
      (course) => course.isPartOfModule
    );
    const partOfDegree = this.coursesByIssuer[this.selectedIssuer].filter(
      (course) => course.isPartOfDegree
    );
    partOfModule.forEach((course) => {
      if (this.selectedCourses.has(course.avain)) {
        if (this.courseSelected(course.module)) {
          this.selectedCourses.delete(course.avain);
        }
      }
    });
    partOfDegree.forEach((course) => {
      if (this.selectedCourses.has(course.avain)) {
        if (this.courseSelected(course.degree)) {
          this.selectedCourses.delete(course.avain);
        }
      }
    });
    const courses = Array.from(this.selectedCourses.values());
    this.coursesService.setSelectedCourses(courses);
    this.coursesService.courses = this.coursesByIssuer[
      this.selectedIssuer
    ].filter(
      (course) =>
        courses.includes(course.avain) ||
        (course.degree && courses.includes(course.degree)) ||
        (course.module && courses.includes(course.module))
    );
    this.coursesService.credits = this.creditsCount;
    this.coursesService.selectedIssuer = this.selectedIssuer;
    this.router.navigate(['/preview']);
  }

  toggleSelectAll(): void {
    if (
      this.selectedCourses.size ===
      this.coursesByIssuer[this.selectedIssuer].length
    ) {
      this.selectedCourses.clear();
      this.creditsCount = 0;
    } else {
      this.selectedCourses.clear();
      this.creditsCount = 0;
      this.coursesByIssuer[this.selectedIssuer].forEach((c) => {
        this.selectedCourses.add(c.avain);
        if (c.laji === '2' && !c.isModule) {
          this.creditsCount += +c.laajuus.opintopiste;
        }
      });
    }
  }

  calculateSize(): number {
    const courses = this.coursesByIssuer[this.selectedIssuer].filter(
      (course) =>
        this.selectedCourses.has(course.avain) &&
        course.laji === '2' &&
        !course.isModule
    );
    return courses.length;
  }

  resolveCourseTitle(opintosuoritus: Opintosuoritus, lang: string): string {
    if (!opintosuoritus || !opintosuoritus.nimi) {
      return '';
    } else if (!opintosuoritus.type) {
      return this.courseSelected(opintosuoritus.avain) ?
        this.i18n.courses.table.deselect[lang]  // Valitse <opintosuoritus>
        + ' '
        + Utils.resolveCourseLabel(opintosuoritus)
        : this.i18n.courses.table.select[lang]  // Poista valinta <opintosuoritus>
        + ' '
        + Utils.resolveCourseLabel(opintosuoritus);
    } else {
      return this.courseSelected(opintosuoritus.avain) ?
        this.i18n.courses.table.deselect[lang]  // Valitse <opintosuorituksen tyyppi> <opintosuoritus>
        + ' '
        + this.i18n.courses.type[opintosuoritus.type][lang]
        + ' '
        + Utils.resolveCourseLabel(opintosuoritus)
        : this.i18n.courses.table.select[lang]  // Poista valinta <opintosuorituksen tyyppi> <opintosuoritus> -> Poista valinta tutkinto|moduuli|kurssi xyz.
        + ' '
        + this.i18n.courses.type[opintosuoritus.type][lang]
        + ' '
        + Utils.resolveCourseLabel(opintosuoritus);
    }
  }

  protected readonly Utils = Utils;
}
