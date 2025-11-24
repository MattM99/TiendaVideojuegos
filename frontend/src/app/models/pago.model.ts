import { MetodoPagoUnion } from './metodo-pago.model';

export interface PagoModel {
  id: string;
  alquilerId: string; // <--- asociación
  estado: string;
  descuento: number;
  metodoPago: MetodoPagoUnion; // <--- composición
  total: number;
}
