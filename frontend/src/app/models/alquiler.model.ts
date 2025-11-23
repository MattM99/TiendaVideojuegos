import { CarritoItemModel } from "./carritoItem.model";
import { PenalizacionModel } from "./penalizacion.model";

export interface AlquilerModel {
    id?: string;
    personaId: string;
    fechaInicio: string;
    fechaFin: string;
    detalles: CarritoItemModel[]; // <--- composición
    montoFijo: number;
    fechaDevolucion?: string; // Opcional, porque será nulo hasta que se devuelva el alquiler
    penalizaciones: PenalizacionModel[]; // <--- composición
}
