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

    getById(id: number): Observable<CuentaModel> {
      return this.http.get<CuentaModel>(`${this.apiUrl}/${id}`);
    }

    create(c: CuentaModel): Observable<CuentaModel> {
      return this.http.post<CuentaModel>(this.apiUrl, c);
    }

    update(id: number, c: CuentaModel): Observable<CuentaModel> {
      return this.http.put<CuentaModel>(`${this.apiUrl}/${id}`, c);
    }

    delete(id: number): Observable<void> {
      return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }

}
