import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { ICourseResponse, IssuerResponseData, Opintosuoritus, Opiskelija, Sisaltyvyys } from './course';

@Injectable({
  providedIn: 'root',
})
export class CoursesService {
  issuers$ = this.http.get<IssuerResponseData>(environment.getIssuersUrl);
  courses$ = this.http
    .get<ICourseResponse>(environment.getAllCoursesUrl, {
      withCredentials: true,
    })
    .pipe(
      map((response: ICourseResponse) => {
        let degrees: Opintosuoritus[] = [];

        // Find only relevant data and filter out everything else.
        // Emrex assumes data must have opintosuoritukset-field.
        response.virta.opiskelija = response.virta.opiskelija
          .filter(opiskelija => opiskelija.hasOwnProperty("opintosuoritukset"));

        // Quick and dirty way to filter out data. Should filter in backend instead.
        // Opintosuoritukset is the only thing emrex need.
        response.virta.opiskelija.forEach((HEI) => {
          if (HEI.lukukausiIlmoittautumiset) {
            delete HEI.lukukausiIlmoittautumiset;
          }
          if (HEI.opiskeluoikeudet) {
            delete HEI.opiskeluoikeudet;
          }
          if (HEI.liikkuvuusjaksot) {
            delete HEI.liikkuvuusjaksot;
          }
        });
        // FIXME this whole file is a bomb, potential NPEs left and right.
        // START by removing @ts-ignores
        // Had to add ignores after fixing interface to match virta spec
        response.virta.opiskelija.forEach((HEI) => {
          // @ts-ignore
          HEI.opintosuoritukset.opintosuoritus = HEI.opintosuoritukset.opintosuoritus.filter(
            (suoritus) => +suoritus.laji === 1 || +suoritus.laji === 2
          );
          // @ts-ignore
          degrees = HEI.opintosuoritukset.opintosuoritus.filter(
            (suoritus) => +suoritus.laji === 1
          );
          if (degrees.length > 0) {
            degrees.forEach((degree, i) => {
              degree.isDegree = true;
              degree.type = 'degree';
              degree.hasPart = [];
              // @ts-ignore
              degree.sisaltyvyys.forEach((sisaltyvyys: Sisaltyvyys) => {
                // @ts-ignore
                degree.hasPart.push(sisaltyvyys.sisaltyvaOpintosuoritusAvain);
              });
              // @ts-ignore
              HEI.opintosuoritukset.opintosuoritus.sort((a, b) =>
                // @ts-ignore
                a.nimi[0].value.localeCompare(b.nimi[0].value)
              );
              // @ts-ignore
              HEI.opintosuoritukset.opintosuoritus.forEach((suoritus: Opintosuoritus, j) => {
                // @ts-ignore
                if (+suoritus.laji === 2 && suoritus.sisaltyvyys.length > 0) {
                  suoritus.isModule = true;
                  suoritus.type = 'module';
                  suoritus.hasPart = [];
                  // @ts-ignore
                  suoritus.sisaltyvyys.forEach((sisaltyvyys, k) => {
                    // @ts-ignore
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
              // @ts-ignore
              HEI.opintosuoritukset.opintosuoritus
                .filter((c) => c.isModule)
                .forEach((m) =>
                  // @ts-ignore
                  m.hasPart.forEach((c) => (c.degree = m.degree))
                );

              degree.weight = i * 100000;
              // @ts-ignore
              degree.hasPart = HEI.opintosuoritukset.opintosuoritus.filter(
                (c) => c.isPartOfDegree
              );
            });
          } else {
            // @ts-ignore
            HEI.opintosuoritukset.opintosuoritus.sort((a, b) =>
              // @ts-ignore
              a.nimi[0].value.localeCompare(b.nimi[0].value)
            );
            // @ts-ignore
            HEI.opintosuoritukset.opintosuoritus.forEach((suoritus, i) => {
              // @ts-ignore
              if (+suoritus.laji === 2 && suoritus.sisaltyvyys.length > 0) {
                suoritus.isModule = true;
                suoritus.type = 'module';
                suoritus.hasPart = [];
                suoritus.weight = (i + 1) * 100;
                // @ts-ignore
                suoritus.sisaltyvyys.forEach((sisaltyvyys, j) => {
                  // @ts-ignore
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
        });
        return response;
      })
    );

  selectedCourses: string[] = [];
  selectedIssuer = '';
  courses: Opintosuoritus[] = [];
  count = 0;
  credits = 0;
  errors: string[] = [];

  // The sort in courses$ was not working properly between different browsers so we try to separate the sorting logic here
  // Otherwise the degrees -> modules -> courses classification in courses$ seem to work so we'll utilize it here (degrees.hasParts has all modules related to that degree etc...)
  // NOTE: not all modules are "linked" to a degree (at least such is the case with the test user), not sure if all courses are linked to a module but this needs to be handled just in case
  sortedCourses$ = this.courses$.pipe(
    map(response => {
      const result: Opintosuoritus[] = [];

      // First we go through degrees -> modules -> courses
      // After that modules (without degree) -> courses
      // And finally courses (without module)

      response.virta.opiskelija.forEach(student => {
        const sortedStudy: Opintosuoritus[] = [];
        const all = student.opintosuoritukset?.opintosuoritus ?? [];

        const degrees = all.filter(o => o.isDegree);
        const modules = all.filter(o => o.isModule);
        const courses = all.filter(o => o.type === 'course');

        // Degrees
        degrees.forEach(degree => {
          sortedStudy.push(degree)

          degree.hasPart?.forEach(module => {
            sortedStudy.push(module)

            module.hasPart?.forEach(course => sortedStudy.push(course))
          });
        })


        // Modules (not linked with degree)
        modules.forEach(module => {
          if (!sortedStudy.includes(module)) {
            sortedStudy.push(module)

            module.hasPart?.forEach(course => sortedStudy.push(course))
          }
        })

        // Courses (not linked with module)
        courses.forEach(course => {
          if (!sortedStudy.includes(course)) {
            sortedStudy.push(course)
          }
        })

        // Set new sorted array to response
        if (student.opintosuoritukset && sortedStudy.length > 0) {
          student.opintosuoritukset.opintosuoritus = sortedStudy;
        }
      })

      return response;
    })
  )

  /**
   * Group courses by issuer example data: const coursesByIssuer = {
   *   "issuerTitle1": [
   *     { ...course1, myontaja: "issuerTitle1key" },
   *     { ...course2, myontaja: "issuerTitle1key" },
   *     // other courses with issuerTitle1
   *   ],
   *   "issuerTitle2": [
   *     { ...course3, myontaja: "issuerTitle2key" },
   *     // other courses with issuerTitle2
   *   ],
   *   // additional issuers and their grouped courses
   * }
   */
  coursesWithIssuers$ = combineLatest([this.issuers$, this.sortedCourses$]).pipe(
    map(([issuers, courses]) => {
      const coursesByIssuer: { [key: string]: Opintosuoritus[] } = {};
      courses.virta.opiskelija.map((student: Opiskelija) => {
        // Filter out everything that is not opintosuoritukset(course data).
        if (student.opintosuoritukset
          && student.opintosuoritukset.opintosuoritus
          && student.opintosuoritukset.opintosuoritus.length) {

          student.opintosuoritukset.opintosuoritus.forEach((course: Opintosuoritus) => {
            // This could be null when course.myontaja is empty or course.myontaja does not match any issuer.
            // When course.myontaja is empty, possible cause could due bug or incomplete source data.
            // When course.myontaja does not match any issuer, then EMREX issuers data could be incomplete.
            const issuerTitle = course.myontaja ? issuers[course.myontaja]?.title || null : null;

            // Skip processing for this course because course MUST have issuer.
            if (!issuerTitle) {
              console.log(`Unrecoverable error: incomplete data: course.nimi:${course.nimi?.[0]?.value ?? ''}, course.avain:${course.avain}, course.myontaja:${course.myontaja}.\n`, course);
              this.postError(`Unrecoverable error: incomplete data: course.nimi:${course.nimi?.[0]?.value ?? ''}, course.avain:${course.avain}, course.myontaja:${course.myontaja}.`);
              if (this.errors.length < 3) {
                this.errors.push(`Course data: ${course.nimi?.[0]?.value ?? ''}|${course.avain}|${course.myontaja} cannot be displayed.`);
              }
              return;
            }

            // Group courses by issuer title string
            if (!coursesByIssuer[issuerTitle]) {
              coursesByIssuer[issuerTitle] = [];
            }

            coursesByIssuer[issuerTitle].push({
              ...course,
              myontaja: issuerTitle,
            });
          });
        }
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

  /**
   * Post error to server.
   * Never log personal data and make sure your server will encrypt the data.
   * @param errorContent
   */
  postError(errorContent: any) {
    this.http.post("api/error", errorContent).subscribe({
      error: errorRes => {
        console.error("Logging failure", errorRes);
      },
    });
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
