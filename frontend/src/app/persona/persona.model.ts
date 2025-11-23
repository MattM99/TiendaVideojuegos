export interface PersonaModel {
    id: number; 
    dni: string;
    nombre: string;
    apellido: string;
    email?: string; //Opcional
    telefono?: string; //Opcional
}
