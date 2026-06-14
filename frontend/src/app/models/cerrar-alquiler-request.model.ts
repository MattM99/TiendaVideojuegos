import { MetodoPago } from './metodo-pago.model';

export interface PenalizacionManualRequest {
  motivo: string;
  monto: number;
}

export interface CerrarAlquilerRequest {
  metodoPago: MetodoPago;
  descuento: number;
  penalizaciones: PenalizacionManualRequest[];
}
