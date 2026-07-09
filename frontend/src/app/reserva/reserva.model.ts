export interface ReservaRequest {
  personaDni: string;
}

export interface ReservaModel {
  reservaId: number;
  personaDni: string;
  inventarioItemId: number;
  estadoReserva: 'PENDIENTE' | 'NOTIFICADA' | 'EXPIRADA' | 'CONCLUIDA' | 'CANCELADA';
  fechaReserva: string;
  fechaNotificacion?: string | null;
}