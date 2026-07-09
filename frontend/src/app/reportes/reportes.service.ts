import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardResponse } from './dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {

  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/reportes';

  obtenerDashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(
      `${this.apiUrl}/dashboard`
    );
  }
}
