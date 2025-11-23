import { PersonaModel } from "../persona/persona.model";

export interface CuentaModel {
    id?: string; //Lo dejo opcional para cuando se cree una nueva cuenta
    personaId: string; // <--- asociación
    nombreUsuario: string;
    password: string;
    rol: string;
    alta: boolean;
}
