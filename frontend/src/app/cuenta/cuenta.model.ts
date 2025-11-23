import { PersonaModel } from "../persona/persona.model";

export interface CuentaModel {
    id: string;
    personaId: string; // <--- asociación
    nombreUsuario: string;
    password: string;
    rol: string;
    alta: boolean;
}
