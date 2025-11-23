import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PersonaModel } from '../models/persona.model';


@Injectable({
  providedIn: 'root',
})
export class Persona {
     private http = inject(HttpClient);
  private baseUrl = 'http://localhost:3000/personas';

  personas = signal<PersonaModel[]>([]);
  cargando = signal(false);

  cargarPersonas() {
    this.cargando.set(true);
    this.http.get<PersonaModel[]>(this.baseUrl).subscribe({
      next: data => {
        this.personas.set(data);
        this.cargando.set(false);
      },
      error: err => {
        console.error('Error cargando personas', err);
        this.cargando.set(false);
      }
    });
  }

  obtenerPersona(id: number) {
    return this.http.get<PersonaModel>(`${this.baseUrl}/${id}`);
  }

  crearPersona(persona: Omit<PersonaModel, 'id'>) {
    return this.http.post<PersonaModel>(this.baseUrl, persona);
  }

  actualizarPersona(id: number, persona: PersonaModel) {
    return this.http.put<PersonaModel>(`${this.baseUrl}/${id}`, persona);
  }

  eliminarPersona(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
