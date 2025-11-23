import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { InventarioItemModel } from './inventario-item.model';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class InventarioItemService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:3000/inventarioItems'; // ajusta tu endpoint

  getAll(): Observable<InventarioItemModel[]> {
    return this.http.get<InventarioItemModel[]>(this.baseUrl);
  }

  getById(id: string): Observable<InventarioItemModel> {
    return this.http.get<InventarioItemModel>(`${this.baseUrl}/${id}`);
  }

  create(item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.post<InventarioItemModel>(this.baseUrl, item);
  }

  update(id: string, item: InventarioItemModel): Observable<InventarioItemModel> {
    return this.http.put<InventarioItemModel>(`${this.baseUrl}/${id}`, item);
  }

  delete(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}

