import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';


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

  alquileres = signal<AlquilerModel[]>([]);

  page = signal(0);
  size = signal(5);

  totalPages = signal(0);
  totalElements = signal(0);

  sortBy = signal('fechaInicio');
  direction = signal<'asc' | 'desc'>('desc');

  busqueda = signal('');

  mostrarFinalizados = signal(false);
 alquileresFiltrados = computed(() => {
    let lista = this.alquileres();

    if (!this.mostrarFinalizados()) {
      lista = lista.filter(a => a.estadoAlquiler !== 'FINALIZADO');
    }

    const texto = this.busqueda().toLowerCase().trim();

    return lista.filter(a =>
      a.personaResponse?.nombre?.toLowerCase().includes(texto) ||
      a.personaResponse?.apellido?.toLowerCase().includes(texto) ||
      a.estadoAlquiler?.toLowerCase().includes(texto)
    );
  });

    ngOnInit(): void {
    this.loadAlquileres();
  }

   loadAlquileres() {
    this.alquilerService.getAll(
      this.page(),
      this.size(),
      this.sortBy(),
      this.direction()
    ).subscribe(response => {
      this.alquileres.set(response.content);
      this.totalPages.set(response.totalPages);
      this.totalElements.set(response.totalElements);
    });
  }



  cargarAlquileres() {
    this.alquilerService.cargarAlquileres(
      this.page(),
      this.size(),
      this.busqueda(),
    );
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
    if (!alquilerId) return;

    if (!confirm('¿Seguro que querés eliminar este alquiler?')) return;

    this.alquilerService.eliminarAlquiler(alquilerId).subscribe({
      next: () => this.cargarAlquileres(),
      error: (err) => console.error('Error eliminando alquiler', err),
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
    this.page.set(0);
    this.loadAlquileres();
  }

  changeSort(event: Event) {
    this.sortBy.set((event.target as HTMLSelectElement).value);
    this.page.set(0);
    this.loadAlquileres();
  }

  changeDirection(event: Event) {
    this.direction.set(
      (event.target as HTMLSelectElement).value as 'asc' | 'desc'
    );
    this.page.set(0);
    this.loadAlquileres();
  }

}
