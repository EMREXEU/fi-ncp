import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ElmoReportComponent } from './elmo-report.component';

describe('ElmoReportComponent', () => {
  let component: ElmoReportComponent;
  let fixture: ComponentFixture<ElmoReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ElmoReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ElmoReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
