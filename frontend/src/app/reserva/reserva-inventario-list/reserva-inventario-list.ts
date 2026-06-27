import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';

@Component({
  selector: 'app-reserva-inventario-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './reserva-inventario-list.html',
  styleUrl: './reserva-inventario-list.css',
})
export class ReservaInventarioList implements OnInit {
  private inventarioService = inject(InventarioItemService);
  private router = inject(Router);

  inventarios = signal<InventarioItemModel[]>([]);
  cargando = signal(false);

  ngOnInit(): void {
    this.cargarInventarios();
  }

  cargarInventarios(): void {
    this.cargando.set(true);

    this.inventarioService.getPage(0, 100, 'plataforma', 'asc').subscribe({
      next: (response) => {
        this.inventarios.set(response.content);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error cargando inventarios', err);
        this.cargando.set(false);
        alert('Error al cargar los inventarios');
      }
    });
  }

  verReservas(inventarioId?: number): void {
    if (!inventarioId) return;

    this.router.navigate(['/reservas/inventario', inventarioId]);
  }
}
