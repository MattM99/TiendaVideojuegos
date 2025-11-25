import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CuentaModel } from './cuenta.model';

@Injectable({
  providedIn: 'root',
})
export class CuentaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:3000/cuentas';

  getAll(): Observable<CuentaModel[]> {
    return this.http.get<CuentaModel[]>(this.apiUrl);
  }

  getById(id: string): Observable<CuentaModel> {
    return this.http.get<CuentaModel>(`${this.apiUrl}/${id}`);
  }

  create(c: Omit<CuentaModel, 'id'>): Observable<CuentaModel> {
    return this.http.post<CuentaModel>(this.apiUrl, c);
  }

  update(id: string, c: CuentaModel): Observable<CuentaModel> {
    return this.http.put<CuentaModel>(`${this.apiUrl}/${id}`, c);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  buscarPorUsuario(username: string) {
    return this.http.get<CuentaModel[]>(`${this.apiUrl}?nombreUsuario=${username}`);
  }
}
