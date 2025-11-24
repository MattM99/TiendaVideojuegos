import { CarritoModel } from "../models/carrito.model";
import { PenalizacionModel } from "../models/penalizacion.model";
import { PersonaModel } from "../persona/persona.model";

export interface AlquilerModel {
    id?: string;
    personaId: string;
    videojuegoId: string;
    fechaInicio: string;
    fechaFin: string;
    detalles: CarritoModel[]; // <--- composición
    montoFijo: number;
    fechaDevolucion?: string; // Opcional, porque será nulo hasta que se devuelva el alquiler
    penalizaciones: PenalizacionModel[]; // <--- composición
}
