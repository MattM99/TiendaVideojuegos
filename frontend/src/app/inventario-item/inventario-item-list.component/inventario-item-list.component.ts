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
  
  // Inyección moderna sin constructor
  private service = inject(InventarioItemService);

  // Señales para reactividad
  inventarioItemsWithTitle = signal<{ item: InventarioItemModel, titulo: string }[]>([]);

  ngOnInit() {
    this.loadData();
  }

  // Carga inventario y títulos de juegos
  loadData() {
    this.service.getAll().subscribe(items => {
      // Creamos un arreglo de observables para cada título
      const requests = items.map(i => this.service.getTituloJuego(i.videojuegoId));

      forkJoin(requests).subscribe(titulos => {
        // Combinamos items con sus títulos
        const combined = items.map((item, idx) => ({
          item,
          titulo: titulos[idx] || 'Desconocido'
        }));

        this.inventarioItemsWithTitle.set(combined);
      });
    });
  }

  delete(id?: string) {
    if (!id) return; // si undefined o vacío → no hacemos nada

    if (!confirm("¿Seguro que querés eliminar este item del inventario?")) return;

    this.service.delete(id).subscribe({
      next: () => this.loadData(),
      error: () => alert("Error al eliminar el item del inventario")
    });
  }
}
