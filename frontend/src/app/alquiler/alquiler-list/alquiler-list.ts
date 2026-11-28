import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { finalize, of, Subscription, switchMap, timer } from 'rxjs';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';
import { AuthService } from '../../auth/auth-service/auth';
import { obtenerMensajeError } from '../../shared/error/error-response';


@Component({
  selector: 'app-alquiler-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './alquiler-list.html',
  styleUrl: './alquiler-list.css',
})
export class AlquilerList implements OnInit {
  private alquilerService = inject(Alquiler);
  private router = inject(Router);
  private auth = inject(AuthService);
  private destroyRef = inject(DestroyRef);
  private cargaListado?: Subscription;

  usuario = computed(() => this.auth.currentUser());

  alquileres = signal<AlquilerModel[]>([]);
  cargando = signal(false);
  errorMessage = signal('');

  page = signal(0);
  size = signal(5);

  totalPages = signal(0);
  totalElements = signal(0);

  sortBy = signal('fechaInicio');
  direction = signal<'asc' | 'desc'>('desc');

  busqueda = signal('');

  mostrarFinalizados = signal(false);
  mostrarBajas = signal(false);

  ngOnInit(): void {
    this.loadAlquileres();
  }

  private loadAlquileres(espera = 0): void {
    this.cargaListado?.unsubscribe();
    this.errorMessage.set('');
    this.cargando.set(true);

    const inicio = espera > 0 ? timer(espera) : of(0);
    this.cargaListado = inicio.pipe(
      switchMap(() => this.alquilerService.getAll(
        this.page(),
        this.size(),
        this.sortBy(),
        this.direction(),
        this.mostrarBajas(),
        this.mostrarFinalizados() ? undefined : 'EN_CURSO',
        this.busqueda()
      )),
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.cargando.set(false))
    ).subscribe({
      next: response => {
        if (response.totalPages > 0 && this.page() >= response.totalPages) {
          this.page.set(response.totalPages - 1);
          this.loadAlquileres();
          return;
        }

        if (response.totalPages === 0) this.page.set(0);
        this.alquileres.set(response.content);
        this.totalPages.set(response.totalPages);
        this.totalElements.set(response.totalElements);
      },
      error: err => {
        this.alquileres.set([]);
        this.totalPages.set(0);
        this.totalElements.set(0);
        this.errorMessage.set(
          obtenerMensajeError(err, 'No se pudieron cargar los alquileres.')
        );
      }
    });
  }

  nuevo() {
    this.router.navigate(['/alquileres/nuevo']);
  }

  editar(id: number | undefined) {
    if (!id) return;

    this.router.navigate(['/alquileres', id]);
  }

  cerrar(id: number | undefined) {
    if (!id) return;

    this.router.navigate(['/alquileres', id, 'cerrar']);
  }

  eliminar(alquilerId: number | undefined) {
    this.darDeBaja(alquilerId);
  }

  darDeBaja(alquilerId: number | undefined) {
    if (!alquilerId) return;

    if (!confirm('¿Querés dar de baja este alquiler finalizado?')) return;

    this.errorMessage.set('');

    this.alquilerService.eliminarAlquiler(alquilerId).subscribe({
      next: () => this.loadAlquileres(),
      error: err => {
        this.errorMessage.set(
          obtenerMensajeError(err, 'No se pudo dar de baja el alquiler.')
        );
      },
    });
  }

  getNombrePersona(alquiler: AlquilerModel): string {
    const persona = alquiler.personaResponse;

    if (!persona) return 'Desconocido';

    return `${persona.nombre} ${persona.apellido}`;
  }

  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadAlquileres();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadAlquileres();
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
    this.direction.set(
      (event.target as HTMLSelectElement).value as 'asc' | 'desc'
    );
    this.reiniciarListado();
  }

  changeBusqueda(event: Event) {
    if (this.mostrarBajas()) return;
    this.busqueda.set((event.target as HTMLInputElement).value);
    this.reiniciarListado(250);
  }

  changeMostrarFinalizados(event: Event) {
    this.mostrarFinalizados.set((event.target as HTMLInputElement).checked);
    this.reiniciarListado();
  }

  changeMostrarBajas(event: Event) {
    this.mostrarBajas.set((event.target as HTMLInputElement).checked);
    this.busqueda.set('');
    this.reiniciarListado();
  }

  private reiniciarListado(espera = 0): void {
    this.page.set(0);
    this.loadAlquileres(espera);
  }

}
