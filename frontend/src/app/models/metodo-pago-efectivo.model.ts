import { MetodoPagoModel } from "./metodo-pago.model";

export interface MetodoPagoEfectivoModel extends MetodoPagoModel {
    tipo: 'EFECTIVO';
}