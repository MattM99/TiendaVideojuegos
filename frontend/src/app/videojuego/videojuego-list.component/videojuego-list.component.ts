import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { VideojuegoService } from '../videojuego.service';
import { VideojuegoModel } from '../videojuego.model';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-videojuego-list.component',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './videojuego-list.component.html',
  styleUrl: './videojuego-list.component.css',
})
export class VideojuegoListComponent implements OnInit {
  private service = inject(VideojuegoService);

  videojuegos = signal<VideojuegoModel[]>([]);

  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);

  sortBy = signal('titulo');
  direction = signal('asc');

  busqueda = signal('');
  tipoBusqueda = signal('titulo');

  generos = [
    'ACCION',
    'AVENTURA',
    'TERROR',
    'CARRERAS',
    'RPG',
    'DISPAROS',
    'ESTRATEGIA',
    'SIMULACION',
    'PELEAS',
    'PLATAFORMAS',
    'DEPORTES',
    'MUNDO_ABIERTO',
    'PUZZLE'
  ];

  ngOnInit(): void {
    this.loadVideojuegos();
  }

  loadVideojuegos() {
    const texto = this.busqueda().trim();

    let request;

    if (texto !== '') {
      if (this.tipoBusqueda() === 'titulo') {
        request = this.service.buscarPorTitulo(
          texto,
          this.page(),
          this.size(),
          this.sortBy(),
          this.direction()
        );
      } else if (this.tipoBusqueda() === 'desarrollador') {
        request = this.service.buscarPorDesarrollador(
          texto,
          this.page(),
          this.size(),
          this.sortBy(),
          this.direction()
        );
      } else if (this.tipoBusqueda() === 'genero') {
        request = this.service.buscarPorGenero(
          texto,
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
        this.videojuegos.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
      },
      error: (err) => {
        console.error(err);
        alert('Error al cargar videojuegos');
      }
    });
  }

  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadVideojuegos();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadVideojuegos();
    }
  }

  changeSize(event: Event) {
    const value = Number((event.target as HTMLSelectElement).value);

    this.size.set(value);
    this.page.set(0);

    this.loadVideojuegos();
  }

  changeSort(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.sortBy.set(value);
    this.page.set(0);

    this.loadVideojuegos();
  }

  changeDirection(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.direction.set(value);
    this.page.set(0);

    this.loadVideojuegos();
  }

  changeTipoBusqueda(event: Event) {
    const value = (event.target as HTMLSelectElement).value;

    this.tipoBusqueda.set(value);
    this.busqueda.set('');
    this.page.set(0);

    this.loadVideojuegos();
  }

  changeBusqueda(event: Event) {
    const value = (event.target as HTMLInputElement).value;

    this.busqueda.set(value);
    this.page.set(0);

    this.loadVideojuegos();
  }

  delete(id?: number) {
    if (!id) return;

    if (!confirm('¿Seguro que querés eliminar este videojuego?')) return;

    this.service.delete(id).subscribe({
      next: () => this.loadVideojuegos(),
      error: () => alert('Error al eliminar el videojuego')
    });
  }
}
