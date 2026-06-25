import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  mostrar(error: unknown): void {

    console.error(error);

    if (error instanceof HttpErrorResponse) {

      const mensaje =
        error.error?.mensaje ??
        error.error ??
        error.message ??
        'Ocurrió un error inesperado.';

      alert(mensaje);
      return;
    }

    alert('Ocurrió un error inesperado.');
  }

}