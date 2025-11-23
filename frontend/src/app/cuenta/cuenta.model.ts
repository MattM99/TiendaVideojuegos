import { PersonaModel } from "../persona/persona.model";

export interface CuentaModel {
    id?: number; //Lo dejo opcional para cuando se cree una nueva cuenta
    persona: PersonaModel; // <--- asociación
    nombreUsuario: string;
    password: string;
    rol: string;
    alta: boolean;
}
