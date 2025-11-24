import { AbstractControl, ValidationErrors } from '@angular/forms';

export function EmailTelefono(control: AbstractControl): ValidationErrors | null {
  const email = control.get('email')?.value;
  const telefono = control.get('telefono')?.value;

  if (!email && !telefono) {
    return { emailOTelefonoRequerido: true };
  }

  return null;
}
