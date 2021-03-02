import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { combineLatest, Observable, of, pipe } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { ICourseResponse } from './course';

@Injectable({
  providedIn: 'root',
})
export class CoursesService {
  issuers$ = this.http.get(environment.getIssuersUrl);
  courses$ = this.http.get<ICourseResponse>(environment.getAllCoursesUrl).pipe(
    map((response) => {
      let degrees = [];
      response.virta.opiskelija.forEach((HEI) => {
        degrees = HEI.opintosuoritukset.opintosuoritus.filter(
          (suoritus) => +suoritus.laji === 1
        );
        if (degrees.length > 0) {
          degrees.forEach((degree, i) => {
            degree.isDegree = true;
            degree.type = 'degree';
            degree.hasPart = [];
            degree.sisaltyvyys.forEach((sisaltyvyys) => {
              degree.hasPart.push(sisaltyvyys.sisaltyvaOpintosuoritusAvain);
            });
            HEI.opintosuoritukset.opintosuoritus.sort((a, b) =>
              a.nimi[0].value.localeCompare(b.nimi[0].value)
            );
            HEI.opintosuoritukset.opintosuoritus.forEach((suoritus, j) => {
              if (+suoritus.laji === 2 && suoritus.sisaltyvyys.length > 0) {
                suoritus.isModule = true;
                suoritus.type = 'module';
                suoritus.hasPart = [];
                suoritus.sisaltyvyys.forEach((sisaltyvyys, k) => {
                  const course = HEI.opintosuoritukset.opintosuoritus.find(
                    (c) => c.avain === sisaltyvyys.sisaltyvaOpintosuoritusAvain
                  );
                  course.isPartOfModule = true;
                  course.weight = suoritus.weight =
                    i + 100 * (j + 1) + 1 * (k + 1);
                  course.module = suoritus.avain;
                  course.type = 'course';
                  suoritus.hasPart.push(course);
                });
                suoritus.weight = i + 100 * (j + 1);
              }
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
              suoritus.sisaltyvyys.forEach((sisaltyvyys, j) => {
                const course = HEI.opintosuoritukset.opintosuoritus.find(
                  (c) => c.avain === sisaltyvyys.sisaltyvaOpintosuoritusAvain
                );
                course.isPartOfModule = true;
                course.weight = suoritus.weight = i + 100 * (j + 1);
                course.module = suoritus.avain;
                course.type = 'course';
                suoritus.hasPart.push(course);
              });
            } else {
              suoritus.type = 'course';
            }
          });
        }

        HEI.opintosuoritukset.opintosuoritus = HEI.opintosuoritukset.opintosuoritus.filter(
          (suoritus) => +suoritus.laji === 1 || +suoritus.laji === 2
        );
        HEI.opintosuoritukset.opintosuoritus.sort(
          (a, b) => a.weight - b.weight
        );
      });
      console.log(response);
      return response;
    })
  );

  private selectedCourses: string[] = [];

  coursesWithIssuers$ = combineLatest([this.issuers$, this.courses$]).pipe(
    map(([issuers, courses]) => {
      const coursesByIssuer = {};
      courses.virta.opiskelija.map((student) => {
        const issuer =
          issuers[student.opintosuoritukset.opintosuoritus[0].myontaja].title;
        coursesByIssuer[issuer] = student.opintosuoritukset.opintosuoritus.map(
          (course) => ({
            ...course,
            myontaja: issuers[course.myontaja].title,
          })
        );
      });

      return coursesByIssuer;
    })
  );

  constructor(private http: HttpClient, private router: Router) {}

  setSelectedCourses(courses: string[]): void {
    console.log(courses);
    this.selectedCourses = courses;
  }

  getPreview(): Observable<any> {
    console.log(this.selectedCourses.toString());
    if (this.selectedCourses && this.selectedCourses.length > 0) {
      return this.http.get(
        environment.getSelectedCoursesUrl +
          '?courses=' +
          this.selectedCourses.toString()
      );
    } else {
      console.log('No selected courses to preview');
      this.router.navigate(['/courses']);
      return of();
    }
  }
}
