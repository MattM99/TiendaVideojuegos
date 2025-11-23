import { VideojuegoModel } from "../videojuego/videojuego.model";

export interface InventarioItemModel {
  id: string;
  videojuegoId: string;
  plataforma: string;
  precioDiario: number;
  stockTotal: number;
  enLocal: number;
}
