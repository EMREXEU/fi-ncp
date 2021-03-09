import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LosComponent } from './los.component';

describe('LosComponent', () => {
  let component: LosComponent;
  let fixture: ComponentFixture<LosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
