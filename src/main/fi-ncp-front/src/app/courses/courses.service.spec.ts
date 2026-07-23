import { TestBed } from '@angular/core/testing';

import { CoursesService } from './courses.service';
import {provideHttpClient} from "@angular/common/http";
import {HttpTestingController, provideHttpClientTesting} from "@angular/common/http/testing";
import {environment} from "src/environments/environment";
import {ICourseResponse, Opintosuoritus} from "./course";

describe('CoursesService', () => {
  let service: CoursesService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CoursesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // --- Test fixture helpers -------------------------------------------------
  // Builds minimal-but-valid Opintosuoritus entries. Only the fields the
  // sorting logic actually reads are populated with meaningful values.
  function nimi(value: string) {
    return [{ value }];
  }

  function courseEntry(avain: string, name: string): Opintosuoritus {
    return {
      avain,
      nimi: nimi(name),
      laji: '2',
      sisaltyvyys: [],
      myontaja: 'issuer1',
      laajuus: { opintopiste: 5 },
      arvosana: {},
      opiskelijaAvain: 'S1',
      koulutusmoduulitunniste: 'x',
    } as unknown as Opintosuoritus;
  }

  function degreeOrModuleEntry(avain: string, name: string, laji: '1' | '2', childAvains: string[]): Opintosuoritus {
    return {
      avain,
      nimi: nimi(name),
      laji,
      sisaltyvyys: childAvains.map(a => ({ sisaltyvaOpintosuoritusAvain: a })),
      myontaja: 'issuer1',
      laajuus: { opintopiste: 5 },
      arvosana: {},
      opiskelijaAvain: 'S1',
      koulutusmoduulitunniste: 'x',
    } as unknown as Opintosuoritus;
  }

  function respondWith(entries: Opintosuoritus[]): ICourseResponse {
    return {
      virta: {
        opiskelija: [
          {
            avain: 'S1',
            opintosuoritukset: { opintosuoritus: entries },
          } as any,
        ],
      },
    } as unknown as ICourseResponse;
  }

  function flushAndGetOrder(entries: Opintosuoritus[], done: (order: string[]) => void) {
    service.sortedCourses$.subscribe(response => {
      const order = response.virta.opiskelija[0].opintosuoritukset!.opintosuoritus.map(c => c.avain);
      done(order);
    });
    const req = httpMock.expectOne(environment.getAllCoursesUrl);
    req.flush(respondWith(entries));
  }

  // --- Regression tests for CSCTIE-1368 sorting fix -------------------------

  it('orders degree -> modules -> courses alphabetically, including a course linked directly to the degree', () => {
    const entries = [
      degreeOrModuleEntry('D1', 'Bachelor of Science', '1', ['M2', 'M1', 'C5']),
      degreeOrModuleEntry('M1', 'Advanced Module', '2', ['C2', 'C1']),
      degreeOrModuleEntry('M2', 'Basic Module', '2', ['C4', 'C3']),
      courseEntry('C5', 'Zzz Direct Course'),
      courseEntry('C1', 'Alpha Course'),
      courseEntry('C2', 'Beta Course'),
      courseEntry('C3', 'Gamma Course'),
      courseEntry('C4', 'Delta Course'),
    ];

    let actual: string[] = [];
    flushAndGetOrder(entries, order => (actual = order));

    expect(actual).toEqual(['D1', 'M1', 'C1', 'C2', 'M2', 'C4', 'C3', 'C5']);
  });

  it('appends a module not linked to any degree, sorted alphabetically with its own courses', () => {
    const entries = [
      degreeOrModuleEntry('D1', 'Degree', '1', ['M1']),
      degreeOrModuleEntry('M1', 'Linked Module', '2', ['C1']),
      courseEntry('C1', 'Linked Course'),
      degreeOrModuleEntry('M2', 'Zeta Unlinked Module', '2', ['C3', 'C2']),
      courseEntry('C2', 'Echo Course'),
      courseEntry('C3', 'Foxtrot Course'),
    ];

    let actual: string[] = [];
    flushAndGetOrder(entries, order => (actual = order));

    expect(actual).toEqual(['D1', 'M1', 'C1', 'M2', 'C2', 'C3']);
  });

  it('does not duplicate a module that is both linked to a degree and present in the top-level modules list', () => {
    const entries = [
      degreeOrModuleEntry('D1', 'Degree', '1', ['M1']),
      degreeOrModuleEntry('M1', 'Only Module', '2', ['C1']),
      courseEntry('C1', 'Course'),
    ];

    let actual: string[] = [];
    flushAndGetOrder(entries, order => (actual = order));

    expect(actual.filter(a => a === 'M1').length).toBe(1);
  });

  it('sorts modules and courses alphabetically when the student has no degree at all', () => {
    const entries = [
      degreeOrModuleEntry('M1', 'Zebra Module', '2', ['C2', 'C1']),
      courseEntry('C1', 'Alpha'),
      courseEntry('C2', 'Beta'),
      courseEntry('C3', 'Standalone Course'),
    ];

    let actual: string[] = [];
    flushAndGetOrder(entries, order => (actual = order));

    expect(actual).toEqual(['M1', 'C1', 'C2', 'C3']);
  });

  // Regression test for a bug found while adding this suite: a course that
  // (a) belongs to a student who has at least one degree in their data, and
  // (b) is not referenced by ANY degree's or module's `sisaltyvyys`, used to
  // never get a `type` assigned in courses$ (the `degree.hasPart.includes(...)`
  // check only fired for courses listed directly under a degree), so
  // sortedCourses$'s `type === 'course'` filter silently dropped it instead of
  // appending it as an unlinked course. Fixed by tagging any laji=2 entry that
  // is still untyped after the degree/module walk as a plain course.
  it('keeps a course that is linked to neither a degree nor a module, when the student also has a degree', () => {
    const entries = [
      degreeOrModuleEntry('D1', 'Degree', '1', ['M1']),
      degreeOrModuleEntry('M1', 'Linked Module', '2', ['C1']),
      courseEntry('C1', 'Linked Course'),
      courseEntry('C2', 'Truly Orphan Course'), // not referenced anywhere
    ];

    let actual: string[] = [];
    flushAndGetOrder(entries, order => (actual = order));

    expect(actual).toEqual(['D1', 'M1', 'C1', 'C2']);
  });
});
