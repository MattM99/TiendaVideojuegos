import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Alquiler } from '../alquiler';
import { Persona } from '../../persona/persona';
import { VideojuegoModel } from '../../videojuego/videojuego.model';
import { VideojuegoService } from '../../videojuego/videojuego.service';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';

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
  private personaService = inject(Persona);
  private VideojuegoService = inject(VideojuegoService);
  private inventarioService = inject(InventarioItemService);

  alquileres = computed(() => this.alquilerService.alquileres());
  cargando = computed(() => this.alquilerService.cargando());
  videojuegos = signal<VideojuegoModel[]>([]);

  ngOnInit(): void {
    this.personaService.cargarPersonas();
    this.alquilerService.cargarAlquileres();
    this.VideojuegoService.getAll().subscribe({
      next: (lista) => this.videojuegos.set(lista),
      error: (err) => console.error('Error cargando videojuegos', err),
    });
  }

  nuevo() {
    this.router.navigate(['/alquileres/nuevo']);
  }

  editar(id: string | undefined) {
    if (!id) return;
    this.router.navigate(['/alquileres', id]);
  }

  eliminar(alquilerId: string | undefined, inventarioId: string | undefined) {
    if (!alquilerId) return;

    if (!confirm('¿Seguro que querés eliminar este alquiler?')) return;

    this.alquilerService.eliminarAlquiler(alquilerId).subscribe({
      next: () => {
        // Incrementar stock solo si tenemos inventarioId
        if (inventarioId) {
          this.inventarioService.incrementarStock(inventarioId).subscribe({
            next: () => console.log('Stock incrementado'),
            error: (err) => console.error('Error incrementando stock', err),
          });
        }

        // Recargar lista de alquileres
        this.alquilerService.cargarAlquileres();
      },
      error: (err) => console.error('Error eliminando alquiler', err),
    });
  }

  getNombrePersona(personaId: string): string {
    const persona = this.personaService.personas().find((p) => p.id === personaId);
    return persona ? `${persona.nombre} ${persona.apellido}` : 'Desconocido';
  }

  getTituloJuego(videojuegoId: string): string {
    const juego = this.videojuegos().find((v) => v.id === videojuegoId);
    return juego ? juego.titulo : 'Desconocido';
  }
}
