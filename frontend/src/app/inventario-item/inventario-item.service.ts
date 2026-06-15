import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventarioItemModel } from './inventario-item.model';
import { Observable, switchMap, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { VideojuegoService } from '../videojuego/videojuego.service';

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
export class InventarioItemService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/inventario';
  private videojuegoService = inject(VideojuegoService);

  // Para componentes que necesitan lista simple
  getAll(): Observable<InventarioItemModel[]> {
    return this.http
      .get<{ content: InventarioItemModel[] }>(`${this.baseUrl}/listar`)
      .pipe(map(response => response.content));
  }

  // Para listado con paginación
  getPage(
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.baseUrl}/listar?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarPorVideojuego(
    videojuegoId: number,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.baseUrl}/videojuego/${videojuegoId}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarPorPlataforma(
    plataforma: string,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.baseUrl}/plataforma/${plataforma}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarMasBaratosQue(
    valor: number,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.baseUrl}/precio/menor-a?valor=${valor}&pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
    );
  }

  buscarPorPlataformaMasBaratosQue(
    plataforma: string,
    valor: number,
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.baseUrl}/plataformaPrecio/menor-a`,
      {
        params: {
          plataforma: plataforma,
          valor: valor,
          pagina: pagina,
          tamano: tamano,
          ordenarPor: ordenarPor,
          direccion: direccion
        }
      }
    );
  }

  getById(id: string | number): Observable<InventarioItemModel> {
    return this.http.get<InventarioItemModel>(`${this.baseUrl}/${id}`);
  }

  getTituloJuego(videojuegoId: number): Observable<string> {
    return this.videojuegoService
      .getById(String(videojuegoId))
      .pipe(map(v => v.titulo));
  }

  create(item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.post<InventarioItemModel>(`${this.baseUrl}/crear`, item);
  }

  update(id: string | number, item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.put<InventarioItemModel>(`${this.baseUrl}/${id}`, item);
  }

  patch(id: string | number, item: Partial<InventarioItemModel>): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(`${this.baseUrl}/${id}`, item);
  }

  delete(id: string | number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/eliminar/${id}`);
  }

  agregarStock(id: string | number, cantidad: number): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(
      `${this.baseUrl}/${id}/agregar-stock?cantidad=${cantidad}`,
      {}
    );
  }

  darDeBaja(id: string | number): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(`${this.baseUrl}/${id}/baja`, {});
  }

  getStockTotal(id: string | number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/stock-total`);
  }

  getStockDisponible(id: string | number): Observable<number> {
    return this.http.get<number>(`${this.baseUrl}/${id}/stock-disponible`);
  }
}
