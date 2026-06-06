export interface CrearAlquilerRequest {
  personaDni: Number;
  fechaInicio: string;
  fechaFin: string;
  detalles: CrearDetalleAlquilerRequest[];
}

export interface CrearDetalleAlquilerRequest {
  inventarioItemId: number;
  cantidad: number;
}
