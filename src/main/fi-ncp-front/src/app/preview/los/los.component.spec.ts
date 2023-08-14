import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LosComponent } from './los.component';
import {Component, Input} from "@angular/core";

@Component({
  selector: 'app-loi',
  template: '<div></div>'
})
class MockLoiComponent {
  @Input() loi: any;
}

@Component({
  selector: 'app-identifiers',
  template: '<div></div>'
})
class MockIdentifiersComponent {
  @Input() identifiers: any;
}

describe('LosComponent', () => {
  let component: LosComponent;
  let fixture: ComponentFixture<LosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LosComponent, MockLoiComponent, MockIdentifiersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LosComponent);
    component = fixture.componentInstance;
    component.los = {title: [{lang: 'FI', value: 'Suomi', identifier: 1234}], specifies: []};
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
