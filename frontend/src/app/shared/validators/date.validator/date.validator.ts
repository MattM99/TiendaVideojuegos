import { AbstractControl, ValidationErrors } from '@angular/forms';

export function fechaValida(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  const valido = /^\d{4}-\d{2}-\d{2}$/.test(value);
  return valido ? null : { fechaInvalida: true };
}

export function noFechaPasada(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  if (!value) return null;

  const hoy = new Date();
  const fecha = new Date(value);

  hoy.setHours(0, 0, 0, 0);
  fecha.setHours(0, 0, 0, 0);

  return fecha >= hoy ? null : { fechaPasada: true };
}

export function rangoFechasValidas(group: AbstractControl): ValidationErrors | null {
  const inicio = group.get('fechaInicio')?.value;
  const fin = group.get('fechaFin')?.value;

  if (!inicio || !fin) return null;

  return new Date(inicio) <= new Date(fin) ? null : { rangoInvalido: true };
}
