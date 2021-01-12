import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseSelectionViewComponent } from './course-selection-view.component';

describe('CourseSelectionViewComponent', () => {
  let component: CourseSelectionViewComponent;
  let fixture: ComponentFixture<CourseSelectionViewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CourseSelectionViewComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CourseSelectionViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
