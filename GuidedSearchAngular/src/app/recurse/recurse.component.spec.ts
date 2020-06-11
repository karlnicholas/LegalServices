import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RecurseComponent } from './recurse.component';

describe('RecurseComponent', () => {
  let component: RecurseComponent;
  let fixture: ComponentFixture<RecurseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RecurseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RecurseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
