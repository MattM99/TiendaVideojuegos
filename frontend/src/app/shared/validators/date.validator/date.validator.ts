import { AbstractControl, ValidationErrors } from '@angular/forms';

export function fechaValida(control: AbstractControl): ValidationErrors | null {
  const value = control.value;
  const valido = /^\d{4}-\d{2}-\d{2}$/.test(value);
  return valido ? null : { fechaInvalida: true };
}

export function noFechaPasada(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;


   const inputDate = new Date(control.value);
  const today = new Date();

   inputDate.setHours(0, 0, 0, 0);
  today.setHours(0, 0, 0, 0);

   if (inputDate < today) {
    return { fechaPasada: true };
  }

  return null;
}

export function noFechaFutura(control: AbstractControl): ValidationErrors | null {
  if (!control.value) return null;

  const fechaIngresada = new Date(control.value);
  const hoy = new Date();

  // normalizar (sin horas)
  hoy.setHours(0, 0, 0, 0);
  fechaIngresada.setHours(0, 0, 0, 0);

  if (fechaIngresada > hoy) {
    return { fechaFutura: true };
  }

  return null;
}

export function rangoFechasValidas(group: AbstractControl): ValidationErrors | null {
  const inicio = group.get('fechaInicio')?.value;
  const fin = group.get('fechaFin')?.value;

  if (!inicio || !fin) return null;

  return new Date(inicio) <= new Date(fin) ? null : { rangoInvalido: true };
}
