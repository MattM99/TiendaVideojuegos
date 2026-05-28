export interface InventarioItemModel {
  inventarioItemId?: number;
  videojuegoId: number;
  plataforma: string;
  precioDiario: number;
  stockTotal: number;
  stockDisponible: number;
}
