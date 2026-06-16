import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { VideojuegoModel } from './videojuego.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

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
export class VideojuegoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/videojuegos';

  // Para componentes que solo necesitan la lista simple
  getAll(): Observable<VideojuegoModel[]> {
    return this.http
      .get<{ content: VideojuegoModel[] }>(`${this.apiUrl}/listar`)
      .pipe(map(response => response.content));
  }

  // Para el listado con paginación
  getPage(
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<VideojuegoModel>> {
    return this.http.get<PageResponse<VideojuegoModel>>(
      `${this.apiUrl}/listar?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  videojuegos(): Observable<VideojuegoModel[]> {
    return this.getAll();
  }

  buscarPorTitulo(
    titulo: string,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<VideojuegoModel>> {
    return this.http.get<PageResponse<VideojuegoModel>>(
      `${this.apiUrl}/titulo/${titulo}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarPorDesarrollador(
    desarrollador: string,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<VideojuegoModel>> {
    return this.http.get<PageResponse<VideojuegoModel>>(
      `${this.apiUrl}/desarrollador/${desarrollador}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarPorGenero(
    genero: string,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<VideojuegoModel>> {
    return this.http.get<PageResponse<VideojuegoModel>>(
      `${this.apiUrl}/genero/${genero}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  getById(id: string | number): Observable<VideojuegoModel> {
    return this.http.get<VideojuegoModel>(`${this.apiUrl}/id/${id}`);
  }

  create(v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.post<VideojuegoModel>(`${this.apiUrl}/crear`, v);
  }

  update(id: string | number, v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.put<VideojuegoModel>(`${this.apiUrl}/${id}`, v);
  }

  delete(id: string | number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/eliminar/${id}`);
  }
}

