import { Component, computed, inject, signal } from '@angular/core';
import { InventarioItemService } from '../inventario-item.service';
import { VideojuegoService } from '../../videojuego/videojuego.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inventario-item-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './inventario-item-form.component.html',
  styleUrls: ['./inventario-item-form.component.css']
})
export class InventarioItemFormComponent {

  inventarioService = inject(InventarioItemService);
  videojuegosService = inject(VideojuegoService);

  route = inject(ActivatedRoute);
  router = inject(Router);

  isEdit = false;
  id: string = '';

  // Lista de videojuegos para el <select>
  videojuegos = signal<{ id: string, titulo: string }[]>([]);

  // Modelo del formulario
  item = signal({
    videojuegoId: '',
    plataforma: '',
    precioDiario: 0,
    stockTotal: 0,
    enLocal: 0
  });


  canSave = computed(() => {
    const v = this.item();
    return (
      v.videojuegoId.trim() !== '' &&
      v.plataforma.trim() !== '' &&
      v.precioDiario > 0 &&
      v.stockTotal >= 0 &&
      v.enLocal >= 0
    );
  });


  ngOnInit() {

    // Cargar videojuegos para el dropdown
    this.videojuegosService.getAll().subscribe({
      next: lista => this.videojuegos.set(
        lista.map(v => ({ id: v.id ?? '', titulo: v.titulo }))
      ),
      error: () => alert("No se pudo cargar la lista de videojuegos")
    });


    // ¿Edit?
    const routeId = this.route.snapshot.paramMap.get('id');
    if (routeId) {
      this.id = routeId;
      this.isEdit = true;

      this.inventarioService.getById(routeId).subscribe({
        next: data => this.item.set(data),
        error: () => alert("No se pudo cargar el item a editar")
      });
    }
  }

  // Métodos de update (igual que en videojuegos)
  updateVideojuego(id: string) {
    this.item.update(v => ({ ...v, videojuegoId: id }));
  }

  updatePlataforma(value: string) {
    this.item.update(v => ({ ...v, plataforma: value }));
  }

  updatePrecio(value: number) {
    this.item.update(v => ({ ...v, precioDiario: Number(value) }));
  }

  updateStockTotal(value: number) {
    this.item.update(v => ({ ...v, stockTotal: Number(value) }));
  }

  updateEnLocal(value: number) {
    this.item.update(v => ({ ...v, enLocal: Number(value) }));
  }


  guardar() {
    if (!this.canSave()) return;

    const data = this.item();

    if (this.isEdit) {
      this.inventarioService.update(this.id, data).subscribe({
        next: () => this.router.navigate(['/inventario']),
        error: () => alert("Error al actualizar el item")
      });
    } else {
      this.inventarioService.create(data).subscribe({
        next: () => this.router.navigate(['/inventario']),
        error: () => alert("Error al crear el item")
      });
    }
  }

  cancelar() {
    this.router.navigate(['/inventario']);
  }
}

