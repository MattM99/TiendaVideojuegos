import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { InventarioItemService } from '../inventario-item.service';
import { InventarioItemModel } from '../inventario-item.model';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { VideojuegoService } from '../../videojuego/videojuego.service';
import { VideojuegoModel } from '../../videojuego/videojuego.model';
import { ErrorService } from '../../shared/error/error';

@Component({
  selector: 'app-inventario-item-list.component',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './inventario-item-list.component.html',
  styleUrl: './inventario-item-list.component.css',
})
export class InventarioItemListComponent implements OnInit {
  private service = inject(InventarioItemService);
  private videojuegoService = inject(VideojuegoService);
  private router = inject(Router);
  private errorService = inject(ErrorService);

  inventarioItemsWithTitle = signal<{ item: InventarioItemModel; titulo: string }[]>([]);
  videojuegos = signal<VideojuegoModel[]>([]);

  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);

  sortBy = signal('plataforma');
  direction = signal('asc');

  tipoBusqueda = signal('');
  videojuegoIdBusqueda = signal(0);
  plataformaBusqueda = signal('');
  precioBusqueda = signal('');
  sinResultados = signal(false);

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

  ngOnInit(): void {
    this.loadVideojuegos();
    this.loadInventario();
  }

  loadVideojuegos() {
    this.videojuegoService.getPage(0, 100, 'titulo', 'asc').subscribe({
      next: (response) => this.videojuegos.set(response.content),
      error: err => this.errorService.mostrar(err)
    });
  }

  loadInventario() {
    let request;

    if (this.tipoBusqueda() === 'videojuego' && this.videojuegoIdBusqueda() > 0) {
      request = this.service.buscarPorVideojuego(
        this.videojuegoIdBusqueda(),
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction()
      );

    } else if (this.tipoBusqueda() === 'plataforma' && this.plataformaBusqueda() !== '') {
      request = this.service.buscarPorPlataforma(
        this.plataformaBusqueda(),
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction()
      );

    } else if (this.tipoBusqueda() === 'precio' && this.precioBusqueda().trim() !== '') {
      request = this.service.buscarMasBaratosQue(
        Number(this.precioBusqueda()),
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction()
      );

    } else if (
      this.tipoBusqueda() === 'plataformaPrecio' &&
      this.plataformaBusqueda() !== '' &&
      this.precioBusqueda().trim() !== ''
    ) {
      request = this.service.buscarPorPlataformaMasBaratosQue(
        this.plataformaBusqueda(),
        Number(this.precioBusqueda()),
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction()
      );

    } else {
      request = this.service.getPage(
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction()
      );
    }

    request.subscribe({
      next: (response) => {
        const combined = response.content.map((item) => ({
          item,
          titulo: item.videojuego?.titulo || 'Desconocido',
        }));

        this.inventarioItemsWithTitle.set(combined);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
        this.sinResultados.set(response.content.length === 0);
      },
      error: (err) => {
         if (err.status === 404) {
    this.inventarioItemsWithTitle.set([]);
    this.totalPages.set(0);
    this.totalElements.set(0);
    this.sinResultados.set(true);
  } else {
    console.error(err);
  }
      },
    });
  }

  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadInventario();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadInventario();
    }
  }

  changeSize(event: Event) {
    const value = Number((event.target as HTMLSelectElement).value);

    this.size.set(value);
    this.page.set(0);

    this.loadInventario();
  }

  changeSort(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.sortBy.set(value);
    this.page.set(0);

    this.loadInventario();
  }

  changeDirection(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.direction.set(value);
    this.page.set(0);

    this.loadInventario();
  }

  changeTipoBusqueda(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.tipoBusqueda.set(value);

    this.videojuegoIdBusqueda.set(0);
    this.plataformaBusqueda.set('');
    this.precioBusqueda.set('');

    this.page.set(0);
    this.loadInventario();
  }

  changeVideojuegoBusqueda(event: Event) {
    const value = Number((event.target as HTMLSelectElement).value);

    this.videojuegoIdBusqueda.set(value);
    this.page.set(0);

    this.loadInventario();
  }

  changePlataformaBusqueda(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.plataformaBusqueda.set(value);
    this.page.set(0);

    if (
      this.tipoBusqueda() === 'plataformaPrecio' &&
      (value === '' || this.precioBusqueda().trim() === '')
    ) {
      return;
    }

    this.loadInventario();
  }

  changePrecioBusqueda(event: Event) {
    const value = (event.target as HTMLInputElement).value;

    this.precioBusqueda.set(value);
    this.page.set(0);

    if (this.tipoBusqueda() === 'precio' && value.trim() === '') {
      this.loadInventario();
      return;
    }

    if (
      this.tipoBusqueda() === 'plataformaPrecio' &&
      (this.plataformaBusqueda() === '' || value.trim() === '')
    ) {
      return;
    }

    this.loadInventario();
  }

  delete(id?: number) {
    if (!id) return;

    if (!confirm('¿Seguro que querés eliminar este item del inventario?')) return;

    this.service.delete(id).subscribe({
      next: () => this.loadInventario(),
      error: err => this.errorService.mostrar(err)
    });
  }

  reservar(inventarioId?: number) {
    if (!inventarioId) return;

    this.router.navigate(['/reservas/nueva', inventarioId]);
  }

  verReservas(inventarioId?: number) {
    if (!inventarioId) return;

    this.router.navigate(['/reservas/inventario', inventarioId]);
  }
}
