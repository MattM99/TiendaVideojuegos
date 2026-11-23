import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardResponse } from './dashboard.model';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class ReportesService {

  private http = inject(HttpClient);

  private apiUrl = `${environment.apiUrl}/api/reportes`;

  obtenerDashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(
      `${this.apiUrl}/dashboard`
    );
  }
}
