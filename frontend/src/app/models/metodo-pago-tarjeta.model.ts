export interface MetodoPagoTarjetaModel {
  tipo: 'TARJETA';
  titular: string;
  numero: string;
  cvv: string;
  vencimiento: string; // "MM/YY" el backend lo recibe y parsea a YearMonth
}
