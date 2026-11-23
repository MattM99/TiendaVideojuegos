import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventarioItemModel } from './inventario-item.model';
import { Observable, switchMap, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { VideojuegoService } from '../videojuego/videojuego.service';
import { environment } from '../../environments/environment';


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
  private apiUrl = `${environment.apiUrl}/api/inventario`;
  private videojuegoService = inject(VideojuegoService);

  getAll(): Observable<InventarioItemModel[]> {
    return this.http
      .get<{ content: InventarioItemModel[] }>(`${this.apiUrl}/listar`)
      .pipe(map(response => response.content));
  }

  getPage(
    pagina: number,
    tamano: number,
    ordenarPor: string,
    direccion: string
  ): Observable<PageResponse<InventarioItemModel>> {
    return this.http.get<PageResponse<InventarioItemModel>>(
      `${this.apiUrl}/listar?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
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
      `${this.apiUrl}/videojuego/${videojuegoId}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
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
      `${this.apiUrl}/plataforma/${plataforma}?pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
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
      `${this.apiUrl}/precio/menor-a?valor=${valor}&pagina=${pagina}&tamano=${tamano}&ordenarPor=${ordenarPor}&direccion=${direccion}`
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
      `${this.apiUrl}/plataformaPrecio/menor-a`,
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
    return this.http.get<InventarioItemModel>(`${this.apiUrl}/${id}`);
  }

  getTituloJuego(videojuegoId: number): Observable<string> {
    return this.videojuegoService
      .getById(String(videojuegoId))
      .pipe(map(v => v.titulo));
  }

  create(item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.post<InventarioItemModel>(`${this.apiUrl}/crear`, item);
  }

  update(id: string | number, item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.put<InventarioItemModel>(`${this.apiUrl}/${id}`, item);
  }

  patch(id: string | number, item: Partial<InventarioItemModel>): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(`${this.apiUrl}/${id}`, item);
  }

  delete(id: string | number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/eliminar/${id}`);
  }

  agregarStock(id: string | number, cantidad: number): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(
      `${this.apiUrl}/${id}/agregar-stock?cantidad=${cantidad}`,
      {}
    );
  }

  darDeBaja(id: string | number): Observable<InventarioItemModel> {
    return this.http.patch<InventarioItemModel>(`${this.apiUrl}/${id}/baja`, {});
  }

  getStockTotal(id: string | number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${id}/stock-total`);
  }

  getStockDisponible(id: string | number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${id}/stock-disponible`);
  }
}
