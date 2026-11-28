export interface CrearAlquilerRequest {
  personaDni: string;
  fechaInicio: string;
  fechaFin: string;
  detalles: CrearDetalleAlquilerRequest[];
}

export interface CrearDetalleAlquilerRequest {
  inventarioItemId: number;
  cantidad: number;
}

export interface AlquilerUpdateRequest {
  fechaFin: string;
}

export interface DetalleAlquilerCorreccionRequest {
  inventarioItemId: number;
  cantidad: number;
}

