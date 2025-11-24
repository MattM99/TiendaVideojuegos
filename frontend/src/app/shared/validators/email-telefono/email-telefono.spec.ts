import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EmailTelefono } from './email-telefono';

describe('EmailTelefono', () => {
  let component: EmailTelefono;
  let fixture: ComponentFixture<EmailTelefono>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmailTelefono]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EmailTelefono);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
