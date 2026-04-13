import { MetodoPagoEfectivoModel } from './metodo-pago-efectivo.model';
import { MetodoPagoTarjetaModel } from './metodo-pago-tarjeta.model';

// Unión de todos los tipos de métodos de pago:
export type MetodoPagoUnion = MetodoPagoEfectivoModel | MetodoPagoTarjetaModel;

export interface MetodoPagoModel {
  tipo: string; //  'EFECTIVO' | 'TARJETA' discriminador para Angular
}

/* EJEMPLO DE COMO SE TRATARÍA EL MÉTODO DE PAGO USANDO EL MODELO UNIÓN:
if (pago.metodoPago.tipo === 'EFECTIVO') {
  // nada que hacer, siempre true
} else if (pago.metodoPago.tipo === 'TARJETA') {
  const tarjeta = pago.metodoPago as MetodoPagoTarjeta;
  // usar tarjeta.numero, titular, etc.
}*/
