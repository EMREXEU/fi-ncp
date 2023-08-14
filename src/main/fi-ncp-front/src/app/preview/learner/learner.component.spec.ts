import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LearnerComponent } from './learner.component';
import {Component, Input} from "@angular/core";

@Component({
  selector: 'app-loading',
  template: '<div></div>'
})
class MockAppLoading {
  @Input() message: any;
}

@Component({
  selector: 'app-identifiers',
  template: '<div></div>'
})
class MockAppIdentifierComponent {
  @Input() identifiers: any;
}

describe('LearnerComponent', () => {
  let component: LearnerComponent;
  let fixture: ComponentFixture<LearnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LearnerComponent, MockAppLoading, MockAppIdentifierComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LearnerComponent);
    component = fixture.componentInstance;
    // Init test data
    component.learner = "{givenNames: 'Angular Joe', familyNames: 'Angular Family', bday: '1.1.1900',gender: 'Male',identifier: '1234'}"
    component.collapse = false;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
