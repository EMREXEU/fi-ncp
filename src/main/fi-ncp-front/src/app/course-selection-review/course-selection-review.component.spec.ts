import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseSelectionReviewComponent } from './course-selection-review.component';

describe('CourseSelectionReviewComponent', () => {
  let component: CourseSelectionReviewComponent;
  let fixture: ComponentFixture<CourseSelectionReviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseSelectionReviewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseSelectionReviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
