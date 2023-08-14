import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { combineLatest, Observable, of, pipe, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import {ICourseResponse, Opintosuoritus, Opiskelija, Sisaltyvyys} from './course';

@Injectable({
  providedIn: 'root',
})
export class CoursesService {
  issuers$ = this.http.get(environment.getIssuersUrl);
  courses$ = this.http
    .get<ICourseResponse>(environment.getAllCoursesUrl, {
      withCredentials: true,
    })
    .pipe(
      map((response:ICourseResponse) => {
        let degrees: Opintosuoritus[] = [];
        response.virta.opiskelija.forEach((HEI) => {
          HEI.opintosuoritukset.opintosuoritus = HEI.opintosuoritukset.opintosuoritus.filter(
            (suoritus) => +suoritus.laji === 1 || +suoritus.laji === 2
          );
          degrees = HEI.opintosuoritukset.opintosuoritus.filter(
            (suoritus) => +suoritus.laji === 1
          );
          if (degrees.length > 0) {
            degrees.forEach((degree, i) => {
              degree.isDegree = true;
              degree.type = 'degree';
              degree.hasPart = [];
              degree.sisaltyvyys.forEach((sisaltyvyys: Sisaltyvyys) => {
                // @ts-ignore
                degree.hasPart.push(sisaltyvyys.sisaltyvaOpintosuoritusAvain);
              });
              HEI.opintosuoritukset.opintosuoritus.sort((a, b) =>
                a.nimi[0].value.localeCompare(b.nimi[0].value)
              );
              HEI.opintosuoritukset.opintosuoritus.forEach((suoritus: Opintosuoritus, j) => {
                if (+suoritus.laji === 2 && suoritus.sisaltyvyys.length > 0) {
                  suoritus.isModule = true;
                  suoritus.type = 'module';
                  suoritus.hasPart = [];
                  suoritus.sisaltyvyys.forEach((sisaltyvyys, k) => {
                    const course = HEI.opintosuoritukset.opintosuoritus.find(
                      (c) =>
                        c.avain === sisaltyvyys.sisaltyvaOpintosuoritusAvain
                    );
                    if (course) {
                      course.isPartOfModule = true;
                      course.weight = i + 100 * (j + 1) + 1 * (k + 1);
                      course.module = suoritus.avain;
                      course.type = 'course';
                      // @ts-ignore
                      suoritus.hasPart.push(course);
                    } else {
                      suoritus.isModule = false;
                      suoritus.type = 'course';
                      suoritus.sisaltyvyys = [];
                    }
                  });
                  suoritus.weight = i + 100 * (j + 1);
                }
                // @ts-ignore
                if (degree.hasPart.includes(suoritus.avain)) {
                  suoritus.isPartOfDegree = true;
                  suoritus.degree = degree.avain;
                  if (!suoritus.weight) {
                    suoritus.weight = i + 100 * (j + 1);
                  }
                  if (!suoritus.isDegree && !suoritus.isModule) {
                    suoritus.type = 'course';
                  }
                }
              });
              HEI.opintosuoritukset.opintosuoritus
                .filter((c) => c.isModule)
                .forEach((m) =>
                  // @ts-ignore
                  m.hasPart.forEach((c) => (c.degree = m.degree))
                );

              degree.weight = i * 100000;
              degree.hasPart = HEI.opintosuoritukset.opintosuoritus.filter(
                (c) => c.isPartOfDegree
              );
            });
          } else {
            HEI.opintosuoritukset.opintosuoritus.sort((a, b) =>
              a.nimi[0].value.localeCompare(b.nimi[0].value)
            );
            HEI.opintosuoritukset.opintosuoritus.forEach((suoritus, i) => {
              if (+suoritus.laji === 2 && suoritus.sisaltyvyys.length > 0) {
                suoritus.isModule = true;
                suoritus.type = 'module';
                suoritus.hasPart = [];
                suoritus.weight = (i + 1) * 100;
                suoritus.sisaltyvyys.forEach((sisaltyvyys, j) => {
                  const course = HEI.opintosuoritukset.opintosuoritus.find(
                    (c) => c.avain === sisaltyvyys.sisaltyvaOpintosuoritusAvain
                  );
                  if (course) {
                    course.isPartOfModule = true;
                    course.weight = (i + 1) * 100 + (j + 1);
                    course.module = suoritus.avain;
                    course.type = 'course';
                    // @ts-ignore
                    suoritus.hasPart.push(course);
                  } else {
                    suoritus.isModule = false;
                    suoritus.type = 'course';
                    suoritus.sisaltyvyys = [];
                  }
                });
              } else {
                suoritus.type = 'course';
                suoritus.weight = (i + 1) * 100;
              }
            });
          }
          HEI.opintosuoritukset.opintosuoritus.sort(
            // @ts-ignore
            (a, b) => a.weight - b.weight
          );
        });
        return response;
      })
    );

  selectedCourses: string[] = [];
  selectedIssuer = '';
  courses: Opintosuoritus[] = [];
  count = 0;
  credits = 0;

  coursesWithIssuers$ = combineLatest([this.issuers$, this.courses$]).pipe(
    map(([issuers, courses]) => {
      const coursesByIssuer:any = {};
      courses.virta.opiskelija.map((student: Opiskelija) => {
        const issuer =
          // @ts-ignore
          issuers[student.opintosuoritukset.opintosuoritus[0].myontaja].title;
        coursesByIssuer[issuer] = student.opintosuoritukset.opintosuoritus.map(
          (course: Opintosuoritus) => ({
            ...course,
            // @ts-ignore
            myontaja: issuers[course.myontaja].title,
          })
        );
      });

      return coursesByIssuer;
    })
  );

  constructor(private http: HttpClient, private router: Router) {}

  setSelectedCourses(courses: string[]): void {
    this.selectedCourses = courses;
  }

  getPreview(): Observable<any> {
    if (this.selectedCourses && this.selectedCourses.length > 0) {
      return this.http.get(
        environment.getSelectedCoursesUrl +
          '?courses=' +
          this.selectedCourses.toString(),
        {
          withCredentials: true,
        }
      );
    } else {
      this.router.navigate(['/courses']);
      return of();
    }
  }

  sendReport(): void {
    this.http
      .post(
        environment.sendReportUrl,
        {},
        {
          withCredentials: true,
        }
      )
      .subscribe((_) => {
        return;
      });
  }
}
