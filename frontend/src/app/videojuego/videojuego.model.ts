export interface VideojuegoModel {
  videojuegoId?: number;
  titulo: string;
  descripcion: string;
  genero: string;
  lanzamiento: number;
  multijugador: boolean | 'Sí' | 'No';
  desarrollador: string;
}
