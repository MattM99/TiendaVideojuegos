import { HttpErrorResponse } from '@angular/common/http';

interface ErrorResponseBody {
  mensaje?: unknown;
}

export function obtenerMensajeError(
  error: unknown,
  mensajeGenerico: string
): string {
  const cuerpo = error instanceof HttpErrorResponse
    ? error.error
    : obtenerCuerpoError(error);

  if (esRegistro(cuerpo)) {
    const mensaje = normalizarMensaje(
      (cuerpo as ErrorResponseBody).mensaje
    );

    if (mensaje) return mensaje;
  }

  return normalizarMensaje(cuerpo) ?? mensajeGenerico;
}

function obtenerCuerpoError(error: unknown): unknown {
  if (!esRegistro(error)) return undefined;

  return error['error'];
}

function normalizarMensaje(valor: unknown): string | null {
  if (typeof valor === 'string') {
    const mensaje = valor.trim();
    return mensaje || null;
  }

  if (Array.isArray(valor)) {
    const mensajes = valor
      .map(normalizarMensaje)
      .filter((mensaje): mensaje is string => mensaje !== null);

    return mensajes.length > 0 ? mensajes.join(' • ') : null;
  }

  return null;
}

function esRegistro(valor: unknown): valor is Record<string, unknown> {
  return typeof valor === 'object' && valor !== null;
}
