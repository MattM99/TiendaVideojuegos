export interface PersonaModel {
    id?: string; //Lo dejo opcional para cuando se cree una nueva persona
    dni: string;
    nombre: string;
    apellido: string;
    email?: string; //Opcional
    telefono?: string; //Opcional
}
