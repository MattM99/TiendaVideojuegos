import { PersonaModel } from './../persona.model';
import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { finalize, of, Subscription, switchMap, timer } from 'rxjs';
import { obtenerMensajeError } from '../../shared/error/error-response';
import { Persona } from '../persona';

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './personas-list.html',
  styleUrls: ['./personas-list.css'],
})
export class PersonaListComponent implements OnInit {
  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);
  sortBy = signal('apellido');
  direction = signal('desc');
  busqueda = signal('');

  private service = inject(Persona);
  private destroyRef = inject(DestroyRef);
  private cargaListado?: Subscription;
  personas = signal<PersonaModel[]>([]);
  cargando = signal(false);
  errorMessage = signal('');

  constructor(private router: Router) { }

  ngOnInit(): void {
    this.loadPersonas();
  }

  private loadPersonas(espera = 0): void {
    this.cargaListado?.unsubscribe();
    this.cargando.set(true);
    this.errorMessage.set('');

    const inicio = espera > 0 ? timer(espera) : of(0);
    this.cargaListado = inicio.pipe(
      switchMap(() => this.service.cargarPersonas(
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction(),
        this.busqueda()
      )),
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.cargando.set(false))
    ).subscribe({
      next: response => {
        if (response.totalPages > 0 && response.number >= response.totalPages) {
          this.page.set(response.totalPages - 1);
          this.loadPersonas();
          return;
        }

        this.page.set(response.totalPages === 0 ? 0 : response.number);
        this.personas.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
      },
      error: err => {
        this.personas.set([]);
        this.totalPages.set(0);
        this.totalElements.set(0);
        this.errorMessage.set(obtenerMensajeError(err, 'No se pudieron cargar las personas.'));
      }
    });
  }

  crearPersona() {
    this.router.navigate(['/personas/nueva'], { queryParams: { crearCuenta: true } });
  }


  ///PAGINACION, ORDENAMIENTO Y FILTRADO
  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadPersonas();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadPersonas();
    }
  }

  changeSize(event: Event) {
    this.size.set(Number((event.target as HTMLSelectElement).value));
    this.reiniciarListado();
  }

  changeSort(event: Event) {
    this.sortBy.set((event.target as HTMLSelectElement).value);
    this.reiniciarListado();
  }

  changeDirection(event: Event) {
    this.direction.set((event.target as HTMLSelectElement).value);
    this.reiniciarListado();
  }

  changeBusqueda(event: Event): void {
    this.busqueda.set((event.target as HTMLInputElement).value);
    this.reiniciarListado(250);
  }

  private reiniciarListado(espera = 0): void {
    this.page.set(0);
    this.loadPersonas(espera);
  }
}
