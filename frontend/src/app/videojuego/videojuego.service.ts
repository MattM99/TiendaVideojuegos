import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { VideojuegoModel } from './videojuego.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class VideojuegoService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:3000/videojuegos';

  getAll(): Observable<VideojuegoModel[]> {
    return this.http.get<VideojuegoModel[]>(this.apiUrl);
  }

  getById(id: string): Observable<VideojuegoModel> {
    return this.http.get<VideojuegoModel>(`${this.apiUrl}/${id}`);
  }

  create(v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.post<VideojuegoModel>(this.apiUrl, v);
  }

  update(id: string, v: VideojuegoModel): Observable<VideojuegoModel> {
    return this.http.put<VideojuegoModel>(`${this.apiUrl}/${id}`, v);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}


