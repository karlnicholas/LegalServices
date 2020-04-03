import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StatuteDetailComponent } from './statute-detail.component';

describe('StatuteDetailComponent', () => {
  let component: StatuteDetailComponent;
  let fixture: ComponentFixture<StatuteDetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StatuteDetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StatuteDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
