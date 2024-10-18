import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Router} from '@angular/router';
import {combineLatest, Observable, of} from 'rxjs';
import {map} from 'rxjs/operators';
import {environment} from 'src/environments/environment';
import {ICourseResponse, IssuerResponseData, Opintosuoritus, Opiskelija, Sisaltyvyys} from './course';

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
      map((response:ICourseResponse) => {
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
          } if (HEI.opiskeluoikeudet) {
            delete HEI.opiskeluoikeudet;
          } if (HEI.liikkuvuusjaksot) {
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
          // @ts-ignore
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
        if (student.opintosuoritukset && student.opintosuoritukset.opintosuoritus && student.opintosuoritukset.opintosuoritus.length) {
          const myontaja: string = (student.opintosuoritukset.opintosuoritus[0].myontaja) || "";
          console.log("issuer for opintosuoritus[0]: " + myontaja);
          if (myontaja) {
            const issuerTitle: string =
              issuers[myontaja].title
            coursesByIssuer[issuerTitle] = student.opintosuoritukset.opintosuoritus.map(
              (course: Opintosuoritus) => ({
                ...course,
                myontaja: issuers[course.myontaja].title,
              })
            );
          } else {
            console.warn("myontaja is empty, skip processing!");
            let myontajat = "";
            student.opintosuoritukset.opintosuoritus.forEach((opintosuoritus: Opintosuoritus) => {
              myontajat += opintosuoritus.myontaja + "\n";
            })
            this.postError(
              `Unrecoverable error: myontaja is empty, skip processing for student.opintosuoritukset.opintosuoritus!\n
              Source: coursesWithIssuers$ handler\n
              student.opintosuoritukset.opintosuoritus.length: ${student.opintosuoritukset.opintosuoritus.length} skipped!
              Issuers from student.opintosuoritukset.opintosuoritus: \n
              ${myontajat}\n`
            );
          }
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
