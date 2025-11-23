import { InventarioItemModel } from "../inventario-item/inventario-item.model";

export interface CarritoModel {
  id?: string;
  inventarioItemId: string;
  unidades: number;
  subtotal: number;
}