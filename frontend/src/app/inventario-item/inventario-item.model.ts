import { VideojuegoModel } from '../videojuego/videojuego.model';

export interface InventarioItemModel {
  inventarioId?: number;
  videojuego: VideojuegoModel;
  plataforma: string;
  precioDiario: number;
  stockTotal: number;
  stockDisponible: number;
}
