import { AlquilerModel } from "./alquiler.model";
import { MetodoPagoUnion } from "./metodo-pago.model";

export interface PagoModel {
  id: number;
  alquiler: AlquilerModel;
  estado: string;
  metodoPago: MetodoPagoUnion;
  descuento?: number;
  penalizacion?: number;
  total: number;
}
