export interface PersonaModel {
  personaId?: number;
  dni: string;
  nombre: string;
  apellido: string;
  email?: string; //Opcional
  telefono?: string; //Opcional
}
