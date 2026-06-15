import { Component, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';


@Component({
  selector: 'app-alquiler-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './alquiler-list.html',
  styleUrl: './alquiler-list.css',
})
export class AlquilerList implements OnInit {
  private alquilerService = inject(Alquiler);
  private router = inject(Router);

  alquileres = computed(() => this.alquilerService.alquileres());
  cargando = computed(() => this.alquilerService.cargando());

  ngOnInit(): void {
    this.alquilerService.cargarAlquileres();
  }

  nuevo() {
    this.router.navigate(['/alquileres/nuevo']);
  }

  editar(id: number | undefined) {
    if (!id) return;

    this.router.navigate(['/alquileres', id]);
  }

  eliminar(alquilerId: number | undefined) {
    if (!alquilerId) return;

    if (!confirm('¿Seguro que querés eliminar este alquiler?')) return;

    this.alquilerService.eliminarAlquiler(alquilerId).subscribe({
      next: () => this.alquilerService.cargarAlquileres(),
      error: (err) => console.error('Error eliminando alquiler', err),
    });
  }

  getNombrePersona(alquiler: AlquilerModel): string {
    const persona = alquiler.personaResponse;

    if (!persona) return 'Desconocido';

    return `${persona.nombre} ${persona.apellido}`;
  }

  getJuegos(alquiler: AlquilerModel): string {
    if (!alquiler.carrito || alquiler.carrito.length === 0) {
      return 'Sin juegos';
    }

    return alquiler.carrito
      .map((detalle) => {
        const item =
          detalle.inventarioItemResponse ||
          detalle.inventarioItem;

        const videojuego =
          item?.videojuego ||
          item?.videojuegoResponse;

        const titulo = videojuego?.titulo || 'Juego';
        const plataforma = item?.plataforma || '';
        const cantidad = detalle.cantidad ? ` x${detalle.cantidad}` : '';

        return `${titulo}${plataforma ? ' - ' + plataforma : ''}${cantidad}`;
      })
      .join(', ');
  }
}
