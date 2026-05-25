import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CuentaModel } from './cuenta.model';
import { PageResponse } from '../models/page-response.model';

@Injectable({
  providedIn: 'root',
})
export class CuentaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/cuenta';


  getAll(
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string) {
    return this.http.get<PageResponse<CuentaModel>>(
      `${this.apiUrl}/listar?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  getById(id: string): Observable<CuentaModel> {
    return this.http.get<CuentaModel>(`${this.apiUrl}/${id}`);
  }

  getByNickname(nickname: string): Observable<CuentaModel> {
    return this.http.get<CuentaModel>(
      `http://localhost:8080/api/cuenta/${nickname}`
    );
  }

  create(c: Omit<CuentaModel, 'id'>): Observable<CuentaModel> {
    return this.http.post<CuentaModel>(this.apiUrl, c);
  }

  cambiarRol(
    nickname: string,
    nuevoRol: string
  ) {

    return this.http.put<CuentaModel>(
      `${this.apiUrl}/${nickname}/rol`,
      {
        nuevoRol: nuevoRol
      }
    );
  }

  cambiarContrasena(
    nickname: string,
    nuevaContrasena: string
  ) {

    return this.http.put<CuentaModel>(
      `${this.apiUrl}/${nickname}/contrasena`,
      {
        nuevaContrasena: nuevaContrasena
      }
    );
  }

  cambiarMiContrasena(
    nuevaContrasena: string
  ) {

    return this.http.put<CuentaModel>(
      `${this.apiUrl}/mi-cuenta/contrasena`,
      {
        nuevaContrasena: nuevaContrasena
      }
    );
  }

  darDeBaja(
    nickname: string
  ) {

    return this.http.put<CuentaModel>(
      `${this.apiUrl}/${nickname}/baja`,
      {}
    );
  }

  darDeAlta(
    nickname: string
  ) {

    return this.http.put(
      `${this.apiUrl}/${nickname}/alta`,
      {}
    );
  }

  delete(nickname: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${nickname}`);
  }
}
