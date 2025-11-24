import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DateValidator } from './date.validator';

describe('DateValidator', () => {
  let component: DateValidator;
  let fixture: ComponentFixture<DateValidator>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DateValidator]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DateValidator);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
