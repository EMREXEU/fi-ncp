import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IssuerComponent } from './issuer.component';
import {Component, Input} from "@angular/core";

@Component({
  selector: 'app-identifiers',
  template: '<div></div>'
})
class MockAppIdentifierComponent {
  @Input() identifiers: any;
}

describe('IssuerComponent', () => {
  let component: IssuerComponent;
  let fixture: ComponentFixture<IssuerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IssuerComponent, MockAppIdentifierComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuerComponent);
    component = fixture.componentInstance;
    component.issuer = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
