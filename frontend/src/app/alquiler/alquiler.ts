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

  page = signal(0);
  size = signal(5);

  totalPages = signal(0);
  totalElements = signal(0);

  cargarAlquileres(page: number = 0, size: number = 5, busqueda: string = '', tipoBusqueda: string = 'persona') {
    this.cargando.set(true);

    let url = `${this.baseUrl}/listar?pagina=${page}&tamano=${size}&ordenarPor=fechaInicio&direccion=desc`;

    if (busqueda && busqueda.trim() !== '') {
      url += `&tipo=${tipoBusqueda}&valor=${busqueda}`;
    }

     return this.http.get<PageResponse<AlquilerModel>>(url);
  }

  getAll(
  page: number,
  size: number,
  sortBy: string,
  direction: string
) {
  return this.http.get<PageResponse<AlquilerModel>>(
    `${this.baseUrl}/listar`,
    {
      params: {
        pagina: page,
        tamano: size,
        ordenarPor: sortBy,
        direccion: direction
      }
    }
  );
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

