import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { VideojuegoModel } from './videojuego.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class VideojuegoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/videojuegos';

  getAll(): Observable<VideojuegoModel[]> {
    return this.http
      .get<{ content: VideojuegoModel[] }>(`${this.apiUrl}/listar`)
      .pipe(map(response => response.content));
  }

  videojuegos(): Observable<VideojuegoModel[]> {
    return this.getAll();
  }

  getById(id: string): Observable<VideojuegoModel> {
    return this.http.get<VideojuegoModel>(`${this.apiUrl}/id/${id}`);
  }

  create(v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.post<VideojuegoModel>(`${this.apiUrl}/crear`, v);
  }

  update(id: string, v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.put<VideojuegoModel>(`${this.apiUrl}/${id}`, v);
  }

  delete(id: string | number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/eliminar/${id}`);
  }
}


