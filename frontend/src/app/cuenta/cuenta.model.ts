export interface CuentaModel {
  id: string; // modificar otras partes para poder eliminar este dato
  nickname: string;
  rol: string;
  estado: string;
  password: string; // eliminarlo de cuenta-form
  personaId: string; // eliminarlo de cuenta-form

}
