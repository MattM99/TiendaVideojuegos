import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservaModel, ReservaRequest } from './reserva.model';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class ReservaService {
  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/api/inventario`;

  crearReserva(
    inventarioId: number,
    request: ReservaRequest
  ): Observable<ReservaModel> {
    return this.http.post<ReservaModel>(
      `${this.apiUrl}/${inventarioId}/reservas`,
      request
    );
  }

  obtenerReservasPorInventario(
    inventarioId: number
  ): Observable<ReservaModel[]> {
    return this.http.get<ReservaModel[]>(
      `${this.apiUrl}/${inventarioId}/reservas/listar`
    );
  }
}
