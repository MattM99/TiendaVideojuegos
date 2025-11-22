import { VideojuegoModel } from "../videojuego/videojuego.model";

export interface InventarioItemModel {
  id?: number;
  videojuego: VideojuegoModel; // <--- asociación
  plataforma: string;
  precioDiario: number;
  stockTotal: number;
  enLocal: number;
}
