import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuentaWizard } from './cuenta-wizard';

describe('CuentaWizard', () => {
  let component: CuentaWizard;
  let fixture: ComponentFixture<CuentaWizard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuentaWizard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CuentaWizard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
