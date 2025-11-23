import { PersonaModel } from "../models/persona.model";

export interface CuentaModel {
    id?: string; //Lo dejo opcional para cuando se cree una nueva cuenta
    personaId: string; // <--- asociación
    nombreUsuario: string;
    contraseña: string;
    rol: string;
    alta: boolean;
}
