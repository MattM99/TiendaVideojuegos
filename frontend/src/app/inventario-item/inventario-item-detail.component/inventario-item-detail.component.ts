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

  // Un solo item + título
  data = signal<{ item: InventarioItemModel, titulo: string }>({
    item: {
      id: '',
      videojuegoId: '',
      plataforma: '',
      precioDiario: 0,
      stockTotal: 0,
      enLocal: 0
    },
    titulo: ''
  });


  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;

    // Cargar el item
    this.service.getById(id).subscribe({
      next: item => {
        if (!item) return;

        // Cargar título del juego
        this.service.getTituloJuego(item.videojuegoId).subscribe(titulo => {
          this.data.set({
            item,
            titulo: titulo || "Desconocido"
          });
        });
      },
      error: () => alert("No se pudo cargar el item del inventario")
    });
  }

  volver() {
    this.router.navigate(['/inventario']);
  }

  editar() {
    const id = this.data()?.item.id;
    if (!id) return;

    this.router.navigate(['/inventario/edit', id]);
  }
}
