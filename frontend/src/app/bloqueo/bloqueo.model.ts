import { PersonaModel } from "../persona/persona.model";

export interface BloqueoModel {
    bloqueoId: number;
    persona: PersonaModel;
    fechaInicio: string;
    fechaFin: string;
    motivo: string;
    vigente: boolean;
}