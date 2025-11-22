import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CuentaForm } from './cuenta-form';

describe('CuentaForm', () => {
  let component: CuentaForm;
  let fixture: ComponentFixture<CuentaForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CuentaForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CuentaForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
