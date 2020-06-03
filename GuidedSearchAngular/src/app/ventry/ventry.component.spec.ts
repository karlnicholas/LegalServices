import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VentryComponent } from './ventry.component';

describe('VentryComponent', () => {
  let component: VentryComponent;
  let fixture: ComponentFixture<VentryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VentryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VentryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
