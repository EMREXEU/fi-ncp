import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseSelectionReviewViewComponent } from './course-selection-review-view.component';

describe('CourseSelectionReviewViewComponent', () => {
  let component: CourseSelectionReviewViewComponent;
  let fixture: ComponentFixture<CourseSelectionReviewViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseSelectionReviewViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseSelectionReviewViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
