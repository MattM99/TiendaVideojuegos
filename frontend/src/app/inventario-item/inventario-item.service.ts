import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventarioItemModel } from './inventario-item.model';
import { Observable, switchMap, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { VideojuegoService } from '../videojuego/videojuego.service';

@Injectable({
  providedIn: 'root',
})
export class InventarioItemService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:3000/inventarioItems';
  private videojuegoService = inject(VideojuegoService);

  getAll(): Observable<InventarioItemModel[]> {
    return this.http.get<InventarioItemModel[]>(this.baseUrl);
  }

  getById(id: string): Observable<InventarioItemModel> {
    return this.http.get<InventarioItemModel>(`${this.baseUrl}/${id}`);
  }

  // Esto estaba antes en InventarioItemService
getTituloJuego(videojuegoId: string): Observable<string> {
  return this.videojuegoService.getById(videojuegoId).pipe(
    map(v => v.titulo)
  );
}

create(item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.post<InventarioItemModel>(this.baseUrl, item);
  }


  update(id: string, item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.put<InventarioItemModel>(`${this.baseUrl}/${id}`, item);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  // Descuenta 1 del stock
  descontarStock(id: string): Observable<InventarioItemModel> {
    return this.getById(id).pipe(
      switchMap(item =>
        this.http.patch<InventarioItemModel>(
          `${this.baseUrl}/${id}`,
          { enLocal: item.enLocal - 1 }
        )
      ),
      catchError(err => throwError(() => new Error('No se pudo descontar el stock')))
    );
  }

  // Incrementa 1 del stock
  incrementarStock(id: string): Observable<InventarioItemModel> {
    return this.getById(id).pipe(
      switchMap(item =>
        this.http.patch<InventarioItemModel>(
          `${this.baseUrl}/${id}`,
          { enLocal: item.enLocal + 1 }
        )
      ),
      catchError(err => throwError(() => new Error('No se pudo incrementar el stock')))
    );
  }
}
