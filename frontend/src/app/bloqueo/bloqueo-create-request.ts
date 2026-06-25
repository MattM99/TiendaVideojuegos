export interface BloqueoCreateRequest {
    personaDni: string;
    fechaInicio: string;
    fechaFin?: string | null;
    motivo: string;
}