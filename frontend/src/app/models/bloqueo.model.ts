import { PersonaModel } from "./persona.model";

export interface BloqueoModel {
    id?: number;
    personaId: string;
    motivo: string;
    fechaInicio: Date;
    fechaFin: Date;
}
