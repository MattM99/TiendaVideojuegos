import { Component, inject, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import { InventarioItemService } from '../inventario-item.service';
import { InventarioItemModel } from '../inventario-item.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-inventario-item-list.component',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './inventario-item-list.component.html',
  styleUrl: './inventario-item-list.component.css',
})
export class InventarioItemListComponent {
  private service = inject(InventarioItemService);

  inventarioItemsWithTitle = signal<{ item: InventarioItemModel; titulo: string }[]>([]);

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.service.getAll().subscribe({
      next: (items) => {
        console.log('ITEMS INVENTARIO:', items);

        const combined = items.map((item) => ({
          item,
          titulo: item.videojuego?.titulo || 'Desconocido',
        }));

        this.inventarioItemsWithTitle.set(combined);
      },
      error: (err) => console.error('Error cargando inventario', err),
    });
  }

  delete(id?: number) {
    if (!id) return;

    if (!confirm('¿Seguro que querés eliminar este item del inventario?')) return;

    this.service.delete(id).subscribe({
      next: () => this.loadData(),
      error: () => alert('Error al eliminar el item del inventario'),
    });
  }
}