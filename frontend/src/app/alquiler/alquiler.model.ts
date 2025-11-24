import { CarritoItemModel } from "../models/carritoItem.model";
import { PenalizacionModel } from "../models/penalizacion.model";


export interface AlquilerModel {
    id?: string;
    personaId: string;
    inventarioId: string;   
    videojuegoId: string;
    fechaInicio: string;
    fechaFin: string;
    detalles: CarritoItemModel[]; // <--- composición
    montoFijo: number;
    fechaDevolucion?: string; // Opcional, porque será nulo hasta que se devuelva el alquiler
    penalizaciones: PenalizacionModel[]; // <--- composición
}
