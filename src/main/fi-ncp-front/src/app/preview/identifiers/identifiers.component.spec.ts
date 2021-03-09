import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IdentifiersComponent } from './identifiers.component';

describe('IdentifiersComponent', () => {
  let component: IdentifiersComponent;
  let fixture: ComponentFixture<IdentifiersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IdentifiersComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IdentifiersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
