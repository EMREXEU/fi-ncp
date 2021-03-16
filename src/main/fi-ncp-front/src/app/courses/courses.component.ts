import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { I18nService } from 'src/i18n/i18n.service';
import { Opintosuoritus } from './course';
import { CoursesService } from './courses.service';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.component.html',
  styleUrls: ['./courses.component.css'],
})
export class CoursesComponent implements OnInit, OnDestroy {
  private sub: any;
  coursesByIssuer: { [key: string]: Opintosuoritus[] };
  loading = true;
  issuers: string[] = [];
  selectedIssuer = '';
  selectedCourses: Set<string> = new Set();
  creditsCount = 0;
  lang$ = this.i18nService.langAction$;
  i18n = this.i18nService.i18n;

  constructor(
    private coursesService: CoursesService,
    private router: Router,
    private i18nService: I18nService
  ) {}

  ngOnInit(): void {
    this.loading = true;
    this.sub = this.coursesService.coursesWithIssuers$.subscribe(
      (coursesByIssuer) => {
        this.coursesByIssuer = coursesByIssuer;
        this.issuers = Object.keys(coursesByIssuer);
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
      }
    );
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }

  selectIssuer(event: Event): void {
    const target = (event.target as unknown) as { value: string };
    this.selectedIssuer = target.value;
    this.creditsCount = 0;
    this.selectedCourses.clear();
  }

  courseSelected(course: string): boolean {
    return this.selectedCourses.has(course);
  }

  selectCourse(course: string): void {
    const selected = this.coursesByIssuer[this.selectedIssuer].find(
      (c) => c.avain === course
    );
    if (
      this.courseSelected(selected.degree) ||
      this.courseSelected(selected.module)
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
        courses.includes(course.degree) ||
        courses.includes(course.module)
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
}
