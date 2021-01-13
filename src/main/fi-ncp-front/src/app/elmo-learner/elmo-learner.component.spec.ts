import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElmoLearnerComponent } from './elmo-learner.component';

describe('ElmoLearnerComponent', () => {
  let component: ElmoLearnerComponent;
  let fixture: ComponentFixture<ElmoLearnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ElmoLearnerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ElmoLearnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
