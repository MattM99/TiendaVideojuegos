import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CerrarAlquiler } from './cerrar-alquiler';

describe('CerrarAlquiler', () => {
  let component: CerrarAlquiler;
  let fixture: ComponentFixture<CerrarAlquiler>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CerrarAlquiler]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CerrarAlquiler);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
