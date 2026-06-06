import { Component, inject, signal } from '@angular/core';
import { InventarioItemService } from '../inventario-item.service';
import { ActivatedRoute, Router } from '@angular/router';
import { InventarioItemModel } from '../inventario-item.model';

@Component({
  selector: 'app-inventario-item-detail.component',
  standalone: true,
  templateUrl: './inventario-item-detail.component.html',
  styleUrl: './inventario-item-detail.component.css',
})
export class InventarioItemDetailComponent {
  service = inject(InventarioItemService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  data = signal<{ item: InventarioItemModel; titulo: string }>({
    item: {
      inventarioId: 0,
      videojuego: {
        videojuegoId: 0,
        titulo: '',
        descripcion: '',
        genero: '',
        lanzamiento: 0,
        multijugador: false,
        desarrollador: '',
      },
      plataforma: '',
      precioDiario: 0,
      stockTotal: 0,
      stockDisponible: 0,
    },
    titulo: '',
  });

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    this.service.getById(id).subscribe({
      next: (item) => {
        if (!item) return;

        this.data.set({
          item,
          titulo: item.videojuego?.titulo || 'Desconocido',
        });
      },
      error: () => alert('No se pudo cargar el item del inventario'),
    });
  }

  volver() {
    this.router.navigate(['/inventario']);
  }

  editar() {
    const id = this.data().item.inventarioId;
    if (!id) return;

    this.router.navigate(['/inventario/edit', id]);
  }
}
