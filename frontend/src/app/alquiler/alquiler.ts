import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AlquilerModel, ValidarDisponibilidadResponse } from './alquiler.model';
import { AlquilerUpdateRequest, CrearAlquilerRequest } from './alquiler-request.model';
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

    let url = `${this.baseUrl}/listaractivos?pagina=${page}&tamano=${size}&ordenarPor=fechaInicio&direccion=desc`;

    if (busqueda && busqueda.trim() !== '') {
      url += `&tipo=${tipoBusqueda}&valor=${busqueda}`;
    }

     return this.http.get<PageResponse<AlquilerModel>>(url);
  }

  getAll(
    page: number,
    size: number,
    sortBy: string,
    direction: string,
    incluirInactivos = false,
    estado?: 'EN_CURSO' | 'FINALIZADO',
    persona = ''
  ) {
    const endpoint = incluirInactivos
      ? 'administracion/obtenertodos'
      : 'listaractivos';

    return this.http.get<PageResponse<AlquilerModel>>(
      `${this.baseUrl}/${endpoint}`,
      {
        params: {
          pagina: page,
          tamano: size,
          ordenarPor: sortBy,
          direccion: direction,
          ...(estado ? { estado } : {}),
          ...(!incluirInactivos && persona.trim() ? { persona: persona.trim() } : {})
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

  validarDisponibilidad(request: CrearAlquilerRequest): Observable<ValidarDisponibilidadResponse> {
    return this.http.post<ValidarDisponibilidadResponse>(`${this.baseUrl}/validar`, request);
  }

  actualizarAlquiler(
    id: string | number,
    request: AlquilerUpdateRequest
  ): Observable<AlquilerModel> {
    return this.http.patch<AlquilerModel>(`${this.baseUrl}/${id}`, request);
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

