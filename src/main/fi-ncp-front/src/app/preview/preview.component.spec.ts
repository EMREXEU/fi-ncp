import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewComponent } from './preview.component';
import {provideHttpClientTesting} from "@angular/common/http/testing";
import {provideHttpClient} from "@angular/common/http";
import {NotifierService} from "angular-notifier";
import {Component, Input} from "@angular/core";

@Component({
  selector: 'app-loading',
  template: '<div></div>'
})
class MockAppLoading {
  @Input() message: any;
}

describe('PreviewComponent', () => {
  let component: PreviewComponent;
  let fixture: ComponentFixture<PreviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PreviewComponent, MockAppLoading ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: NotifierService, useValue:{} }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PreviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
