export interface AlquilerModel {
  alquilerId: number;
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
  detalleAlquilerId?: number;
  cantidad: number;
  subtotal: number;

  inventarioItemResponse?: InventarioItemResponse;
  inventarioItem?: InventarioItemResponse;
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
