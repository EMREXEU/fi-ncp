import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoiComponent } from './loi.component';

describe('LoiComponent', () => {
  let component: LoiComponent;
  let fixture: ComponentFixture<LoiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoiComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
