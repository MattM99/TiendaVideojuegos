export interface AlquilerModel {
  alquilerId: number;
  activo?: boolean;
  personaResponse: PersonaResponse;
  fechaInicio: string;
  fechaFin: string;
  fechaDevolucion?: string | null;
  estadoAlquiler: string;
  carrito: DetalleAlquilerResponse[];
  penalizaciones: PenalizacionResponse[];
  montoDiarioAlquiler: number;
  pago?: PagoResponse | null;
}

export interface PersonaResponse {
  personaId?: number;
  dni: number;
  nombre: string;
  apellido: string;
  email?: string;
  telefono?: string;
}

export interface DetalleAlquilerResponse {
  detalleAlquilerId: number;
  cantidad: number;
  subtotal: number;
  inventario: InventarioItemResponse;
}

export interface InventarioItemResponse {
  inventarioId?: number;
  inventarioItemId?: number;
  plataforma: string;
  precioDiario: number;
  stockTotal: number;
  stockDisponible: number;
  videojuego?: VideojuegoResponse;
  videojuegoResponse?: VideojuegoResponse;
}

export interface VideojuegoResponse {
  videojuegoId: number;
  titulo: string;
  descripcion?: string;
  genero?: string;
  lanzamiento?: number;
  multijugador?: boolean | string;
  desarrollador?: string;
}

export interface PenalizacionResponse {
  motivo?: string;
  monto?: number;
}

export interface PagoResponse {
  pagoId?: number;
  monto?: number;
  fechaPago?: string;
}
export interface ValidarDisponibilidadResponse {
  puedeCrearAlquiler: boolean;
  faltantes: ItemConStockInsuficiente[];
  disponibilidad: DisponibilidadItem[];
}

export interface ItemConStockInsuficiente {
  inventarioItemId: number;
  titulo: string;
  plataforma: string;
  cantidadSolicitada: number;
  cantidadDisponible: number;
}

export interface DisponibilidadItem {
  inventarioItemId: number;
  cantidadSolicitada: number;
  stockLibre: number;
  cantidadDisponibleParaCliente: number;
  reservaPropiaVigente: boolean;
  motivo: string | null;
}
