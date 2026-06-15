export interface CrearAlquilerRequest {
  personaDni: number;
  fechaInicio: string;
  fechaFin: string;
  detalles: CrearDetalleAlquilerRequest[];
}

export interface CrearDetalleAlquilerRequest {
  inventarioItemId: number;
  cantidad: number;
}

