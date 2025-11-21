import { MetodoPagoEfectivoModel } from "./metodo-pago-efectivo.model";
import { MetodoPagoTarjetaModel } from "./metodo-pago-tarjeta.model";

// Unión de todos los tipos de métodos de pago:
export type MetodoPagoUnion = MetodoPagoEfectivoModel | MetodoPagoTarjetaModel;

export interface MetodoPagoModel {
    tipo: string; //  'EFECTIVO' | 'TARJETA' discriminador para Angular
}
