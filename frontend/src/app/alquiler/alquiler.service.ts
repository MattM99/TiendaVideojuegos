import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AlquilerModel } from './alquiler.model';

@Injectable({
  providedIn: 'root',
})
export class AlquilerService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:3000/alquileres';

  alquileres = signal<AlquilerModel[]>([]);
  cargando = signal(false);

  cargarAlquileres() {
    this.cargando.set(true);
    this.http.get<AlquilerModel[]>(this.baseUrl).subscribe({
      next: data => {
        this.alquileres.set(data);
        this.cargando.set(false);
      },
      error: err => {
        console.error('Error cargando alquileres', err);
        this.cargando.set(false);
      }
    });
  }

  obtenerAlquiler(id: string) {
    return this.http.get<AlquilerModel>(`${this.baseUrl}/${id}`);
  }

  crearAlquiler(alquiler: Omit<AlquilerModel, 'id'>) {
    return this.http.post<AlquilerModel>(this.baseUrl, alquiler);
  }

  actualizarAlquiler(id: string, alquiler: AlquilerModel) {
    return this.http.put<AlquilerModel>(`${this.baseUrl}/${id}`, alquiler);
  }

  eliminarAlquiler(id: string) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
