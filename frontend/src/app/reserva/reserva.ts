import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservaModel, ReservaRequest } from './reserva.model';

@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private http = inject(HttpClient);

  private baseUrl = 'http://localhost:8080/api/inventario';

  crearReserva(
    inventarioId: number,
    request: ReservaRequest
  ): Observable<ReservaModel> {
    return this.http.post<ReservaModel>(
      `${this.baseUrl}/${inventarioId}/reservas`,
      request
    );
  }

  obtenerReservasPorInventario(
    inventarioId: number
  ): Observable<ReservaModel[]> {
    return this.http.get<ReservaModel[]>(
      `${this.baseUrl}/${inventarioId}/reservas/listar`
    );
  }
}