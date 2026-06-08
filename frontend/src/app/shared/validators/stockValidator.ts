import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export function stockValidator(stockDisponible: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const cantidad = control.value;

    if (cantidad == null) return null;

    return cantidad > stockDisponible
      ? { stockInsuficiente: true }
      : null;
  };
}
