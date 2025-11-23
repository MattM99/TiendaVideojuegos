import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Wip } from './wip';

describe('Wip', () => {
  let component: Wip;
  let fixture: ComponentFixture<Wip>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Wip]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Wip);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
