import { InventarioItemModel } from "./inventario-item.model";

export interface CarritoModel {
  id?: number;
  inventarioItem: InventarioItemModel; // <--- asociación
  unidades: number;
  subtotal: number;
}