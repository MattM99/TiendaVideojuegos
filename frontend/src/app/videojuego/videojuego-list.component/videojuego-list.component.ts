import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { VideojuegoService } from '../videojuego.service';
import { VideojuegoModel } from '../videojuego.model';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../auth/auth-service/auth';
import { obtenerMensajeError } from '../../shared/error/error-response';

@Component({
  selector: 'app-videojuego-list.component',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './videojuego-list.component.html',
  styleUrl: './videojuego-list.component.css',
})
export class VideojuegoListComponent implements OnInit {
  private service = inject(VideojuegoService);
  private auth = inject(AuthService);

  usuario = computed(() => this.auth.currentUser());
  puedeAdministrar = computed(() => {
    const rol = this.usuario()?.rol;
    return rol === 'FOUNDER' || rol === 'ADMINISTRADOR';
  });
  reactivando = signal(false);

  videojuegos = signal<VideojuegoModel[]>([]);

  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);

  sortBy = signal('titulo');
  direction = signal('asc');

  busqueda = signal('');
  tipoBusqueda = signal('titulo');
  mostrarBajas = signal(false);

  sinResultados = signal(false);

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

    if (this.mostrarBajas()) {
      request = this.service.getPage(
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction(),
        true
      );
    } else if (texto !== '') {
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
        this.sinResultados.set(response.content.length === 0);
      },
      error: (err) => {
        if (err.status === 404) {
          this.videojuegos.set([]);
          this.totalPages.set(0);
          this.totalElements.set(0);
          this.sinResultados.set(true);
        } else {
          console.error(err);
        }
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

  changeMostrarBajas(event: Event) {
    const value = (event.target as HTMLInputElement).checked;

    this.mostrarBajas.set(value);
    this.busqueda.set('');
    this.page.set(0);

    this.loadVideojuegos();
  }

  darDeAlta(videojuego: VideojuegoModel) {
    const id = videojuego.videojuegoId;
    if (!id || videojuego.activo !== false || !this.puedeAdministrar() || this.reactivando()) return;

    if (!confirm(`¿Querés dar de alta «${videojuego.titulo}»?`)) return;

    this.reactivando.set(true);
    this.service.darDeAlta(id).pipe(
      finalize(() => this.reactivando.set(false))
    ).subscribe({
      next: ({ status, body }) => {
        if (status !== 200 || !body) {
          alert('No se pudo confirmar el alta del videojuego.');
          return;
        }

        this.videojuegos.update(videojuegos =>
          videojuegos.map(v => v.videojuegoId === id ? body : v)
        );
        this.loadVideojuegos();
        alert('El videojuego se dio de alta correctamente.');
      },
      error: err => alert(obtenerMensajeError(err, 'Error al dar de alta el videojuego.'))
    });
  }

  delete(id?: number) {
    if (!id) return;

    if (!confirm('¿Seguro que desea eliminar este videojuego?')) return;

    this.service.delete(id).subscribe({
      next: () => this.loadVideojuegos(),
      error: () => alert('Error al eliminar el videojuego')
    });
  }
}
