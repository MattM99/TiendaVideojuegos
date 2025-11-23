import { CarritoModel } from "./carrito.model";
import { PenalizacionModel } from "./penalizacion.model";
import { PersonaModel } from "./persona.model";

export interface AlquilerModel {
    id?: number;
    persona: PersonaModel; // <--- asociación
    fechaInicio: string;
    fechaFin: string;
    detalles: CarritoModel[]; // <--- composición
    montoFijo: number;
    fechaDevolucion?: string; // Opcional, porque será nulo hasta que se devuelva el alquiler
    penalizaciones: PenalizacionModel[]; // <--- composición
}
