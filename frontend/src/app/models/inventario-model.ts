import { VideojuegoModel } from "./videojuego-model";

export interface Inventario {
  id: number;
  precioDiario: number;
  enLocal: boolean;
  stockTotal: number;
  videojuego: VideojuegoModel; // <--- asociación
}
