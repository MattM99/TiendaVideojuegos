import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonaModel } from '../persona/persona.model';
import { PageResponse } from '../models/page-response.model';

@Injectable({
  providedIn: 'root',
})
export class Persona {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/personas';


  personas = signal<PersonaModel[]>([]);
  cargarPersonas(pagina: number, tamano: number, ordenarPor: string, direccion: string, busqueda = '') {
    const termino = busqueda.trim();
    return this.http.get<PageResponse<PersonaModel>>(
      `${this.baseUrl}/administracion/obtenertodos`,
      {
        params: {
          pagina, tamano, ordenarPor, direccion,
          excluirFounder: true,
          ...(termino ? { busqueda: termino } : {})
        }
      }
    );
  }

  getAll(
  pagina: number,
  tamano: number,
  ordenarPor: string,
  direccion: string
) {

  return this.http.get<PageResponse<PersonaModel>>(
    `${this.baseUrl}/administracion/obtenertodos?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
  );
}

  obtenerPersona(dni: string) {
    return this.http.get<PersonaModel>(`${this.baseUrl}/dni/${dni}`);
  }

  crearPersona(persona: PersonaModel) {

    return this.http.post<PersonaModel>(`${this.baseUrl}/crear`, persona);
  }

actualizarPersona(
  dni: string,
  persona: {
    nombre?: string | null;
    apellido?: string | null;
    telefono?: string | null;
    email?: string | null;
  }
){
    return this.http.patch<PersonaModel>(`${this.baseUrl}/dni/${dni}`, persona);
  }

  eliminarPersona(dni: string) {
    return this.http.delete<void>(`${this.baseUrl}/dni/${dni}`);
  }
}
