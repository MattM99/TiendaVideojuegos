export interface CuentaModel {
  id: string; // modificar otras partes para poder eliminar este dato
  nombreUsuario: string;
  rol: string;
  alta: boolean;
  password: string; // eliminarlo de cuenta-form
  personaId: string; // eliminarlo de cuenta-form

}
