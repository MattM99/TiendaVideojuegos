import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ReservaService } from '../reserva';
import { ReservaModel } from '../reserva.model';
import { Persona } from '../../persona/persona';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';

interface ReservaRow {
  reserva: ReservaModel;
  cliente: string;
}

@Component({
  selector: 'app-reserva-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './reserva-list.html',
  styleUrl: './reserva-list.css',
})
export class ReservaList implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private reservaService = inject(ReservaService);
  private personaService = inject(Persona);
  private inventarioService = inject(InventarioItemService);

  inventarioId!: number;

  inventarioItem = signal<InventarioItemModel | null>(null);
  reservas = signal<ReservaRow[]>([]);

  cargando = signal(false);
  errorMessage = '';

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('inventarioId');

    if (!idParam) {
      this.errorMessage = 'No se encontró el inventario.';
      return;
    }

    this.inventarioId = Number(idParam);

    this.cargarInventario();
    this.cargarReservas();
  }

  cargarInventario(): void {
    this.inventarioService.getById(this.inventarioId).subscribe({
      next: (item) => {
        this.inventarioItem.set(item);
      },
      error: (err) => {
        console.error('Error cargando inventario', err);
        this.errorMessage = 'No se pudo cargar el inventario.';
      }
    });
  }

  cargarReservas(): void {
    this.cargando.set(true);

    this.reservaService.obtenerReservasPorInventario(this.inventarioId).subscribe({
      next: (reservas) => {
        if (reservas.length === 0) {
          this.reservas.set([]);
          this.cargando.set(false);
          return;
        }

        const requests = reservas.map((reserva) =>
          this.personaService.obtenerPersona(reserva.personaDni).pipe(
            map((persona) => ({
              reserva,
              cliente: `${persona.apellido}, ${persona.nombre}`
            })),
            catchError(() =>
              of({
                reserva,
                cliente: 'Cliente no encontrado'
              })
            )
          )
        );

        forkJoin(requests).subscribe({
          next: (rows) => {
            this.reservas.set(rows);
            this.cargando.set(false);
          },
          error: (err) => {
            console.error('Error cargando datos de personas', err);
            this.errorMessage = 'No se pudieron cargar los datos de las reservas.';
            this.cargando.set(false);
          }
        });
      },
      error: (err) => {
        console.error('Error cargando reservas', err);
        this.errorMessage = 'No se pudieron cargar las reservas.';
        this.cargando.set(false);
      }
    });
  }

  volver(): void {
    this.router.navigate(['/reservas']);
  }

  formatearFecha(fecha?: string | null): string {
    if (!fecha) return '-';

    return new Date(fecha).toLocaleString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}