import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AlquilerModel } from './alquiler.model';
import { CrearAlquilerRequest } from './alquiler-request.model';
import { CerrarAlquilerRequest } from '../models/cerrar-alquiler-request.model';
import { Observable } from 'rxjs/internal/Observable';

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
    this.http.get<AlquilerModel[]>(this.baseUrl).subscribe({
      next: (data) => {
        this.alquileres.set(data);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error cargando alquileres', err);
        this.cargando.set(false);
      },
    });
  }

  obtenerAlquiler(id: string) {
    return this.http.get<AlquilerModel>(`${this.baseUrl}/${id}`);
  }

  crearAlquiler(request: CrearAlquilerRequest) {
    return this.http.post<void>(this.baseUrl, request);
  }


  actualizarAlquiler(id: string, alquiler: AlquilerModel) {
    return this.http.put<AlquilerModel>(`${this.baseUrl}/${id}`, alquiler);
  }

  eliminarAlquiler(id: string) {
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

