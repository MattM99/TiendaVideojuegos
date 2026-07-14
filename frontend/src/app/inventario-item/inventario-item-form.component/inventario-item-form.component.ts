import { Component, computed, inject, signal } from '@angular/core';
import { InventarioItemService } from '../inventario-item.service';
import { VideojuegoService } from '../../videojuego/videojuego.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ErrorService } from '../../shared/error/error';

@Component({
  selector: 'app-inventario-item-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './inventario-item-form.component.html',
  styleUrls: ['./inventario-item-form.component.css'],
})
export class InventarioItemFormComponent {
  inventarioService = inject(InventarioItemService);
  videojuegosService = inject(VideojuegoService);
  private errorService = inject(ErrorService);

  route = inject(ActivatedRoute);
  router = inject(Router);

  isEdit = false;
  id: string = '';

  videojuegos = signal<{ id: number; titulo: string }[]>([]);

  precioError = signal('');
  stockTotalError = signal('');
  stockDisponibleError = signal('');

  plataformas = [
    'SEGA',
    'FAMILY',
    'PS1',
    'PS2',
    'PS3',
    'PS4',
    'PS5',
    'XBOX',
    'SWITCH'
  ];

  item = signal({
    videojuegoId: 0,
    plataforma: '',
    precioDiario: 0,
    stockTotal: 0,
    stockDisponible: 0,
  });

  canSave = computed(() => {
    const v = this.item();

    return (
      v.videojuegoId > 0 &&
      v.plataforma.trim() !== '' &&
      v.precioDiario > 0 &&
      v.stockTotal >= 0 &&
      v.stockDisponible >= 0 &&
      v.stockDisponible <= v.stockTotal &&
      this.precioError() === '' &&
      this.stockTotalError() === '' &&
      this.stockDisponibleError() === ''

    );
  });

  ngOnInit() {
    this.videojuegosService.getAll().subscribe({
      next: (lista) =>
        this.videojuegos.set(
          lista.map((v) => ({
            id: v.videojuegoId!,
            titulo: v.titulo,
          }))
        ),
      error: err => this.errorService.mostrar(err)
    });

    const routeId = this.route.snapshot.paramMap.get('id');

    if (routeId) {
      this.id = routeId;
      this.isEdit = true;

      this.inventarioService.getById(routeId).subscribe({
        next: (data) =>
          this.item.set({
            videojuegoId: data.videojuego.videojuegoId!,
            plataforma: data.plataforma,
            precioDiario: data.precioDiario,
            stockTotal: data.stockTotal,
            stockDisponible: data.stockDisponible,
          }),
        error: err => this.errorService.mostrar(err),
      });
    }
  }

  updateVideojuego(id: number | string) {
    this.item.update((v) => ({ ...v, videojuegoId: Number(id) }));
  }

  updatePlataforma(value: string) {
    this.item.update((v) => ({ ...v, plataforma: value }));
  }

  updatePrecio(value: number | string) {
    const numero = Number(value);
    if (isNaN(numero) || numero <= 0) {
      this.precioError.set('El precio debe ser un número positivo');
      return;
    }
    if (numero > 99999) {
      this.precioError.set('El precio no puede ser mayor a 99999');
      return;
    }
    this.precioError.set('');

    this.item.update((v) => ({ ...v, precioDiario: numero }));
  }

  updateStockTotal(value: number | string) {
    const numero = Number(value);

    if (isNaN(numero) || numero < 0) {
      this.stockTotalError.set('El stock debe ser positivo');
      return;
    }

    if (numero > 999) {
      this.stockTotalError.set('El stock no puede exceder 999');
      return;
    }

    this.stockTotalError.set('');
    this.item.update((v) => ({ ...v, stockTotal: numero }));
  }

  updateStockDisponible(value: number | string) {
    const numero = Number(value);

    if (isNaN(numero) || numero < 0) {
      this.stockDisponibleError.set('El stock debe ser positivo');
      return;
    }

    if (numero > 999) {
      this.stockDisponibleError.set('El stock no puede exceder 999');
      return;
    }

    this.stockDisponibleError.set('');
    this.item.update((v) => ({ ...v, stockDisponible: numero }));
  }

  guardar() {
    if (!this.canSave()) {
      alert('Revise los datos del inventario');
      return;
    }

    const data = this.item();

    if (this.isEdit) {
      this.inventarioService.update(this.id, data as any).subscribe({
        next: () => this.router.navigate(['/inventario']),
        error: err => this.errorService.mostrar(err)
      });
    } else {
      this.inventarioService.create(data as any).subscribe({
        next: () => this.router.navigate(['/inventario']),
        error: err => this.errorService.mostrar(err)
      });
    }
  }

  cancelar() {
    this.router.navigate(['/inventario']);
  }
}
