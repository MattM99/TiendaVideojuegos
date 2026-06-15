import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AlquilerModel } from './alquiler.model';
import { CrearAlquilerRequest } from './alquiler-request.model';
import { CerrarAlquilerRequest } from '../models/cerrar-alquiler-request.model';
import { Observable } from 'rxjs/internal/Observable';

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

@Injectable({
  providedIn: 'root',
})
export class Alquiler {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/alquileres';

  alquileres = signal<AlquilerModel[]>([]);
  cargando = signal(false);

  cargarAlquileres() {
    this.cargando.set(true);

    this.http.get<PageResponse<AlquilerModel>>(
      `${this.baseUrl}/listar?pagina=0&tamano=20&ordenarPor=fechaInicio&direccion=desc`
    ).subscribe({
      next: (response) => {
        console.log('ALQUILERES RECIBIDOS:', response);
        this.alquileres.set(response.content);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error cargando alquileres', err);
        this.cargando.set(false);
      },
    });
  }

  obtenerAlquiler(id: string | number) {
    return this.http.get<AlquilerModel>(`${this.baseUrl}/${id}`);
  }

  crearAlquiler(request: CrearAlquilerRequest) {
    return this.http.post<AlquilerModel>(this.baseUrl, request);
  }

  actualizarAlquiler(id: string | number, alquiler: AlquilerModel) {
    return this.http.put<AlquilerModel>(`${this.baseUrl}/${id}`, alquiler);
  }

  eliminarAlquiler(id: string | number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  cerrarAlquiler(
    alquilerId: number,
    request: CerrarAlquilerRequest
  ): Observable<AlquilerModel> {
    return this.http.post<AlquilerModel>(
      `${this.baseUrl}/${alquilerId}/finalizar`,
      request
    );
  }
}

