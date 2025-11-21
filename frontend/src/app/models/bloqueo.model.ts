import { PersonaModel } from "./persona.model";

export interface BloqueoModel {
    id?: number;
    persona: PersonaModel; // <--- asociación
    motivo: string;
    fechaInicio: Date;
    fechaFin: Date;
}
