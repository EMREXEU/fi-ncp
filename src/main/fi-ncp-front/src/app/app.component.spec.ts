import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import {Component} from "@angular/core";

@Component({
  selector: 'app-header',
  template: '<div></div>'
})
class MockAppHeader {}

@Component({
  selector: 'router-outlet role',
  template: '<div></div>'
})
class MockAppRouter {}

@Component({
  selector: 'app-footer',
  template: '<div></div>'
})
class MockAppFooter {}

@Component({
  selector: 'notifier-container',
  template: '<div></div>'
})
class MockAppNotifier {}

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
      declarations: [
        AppComponent, MockAppNotifier, MockAppHeader, MockAppFooter, MockAppRouter, MockAppNotifier
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'fi-ncp-front'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('fi-ncp-front');
  });
});
