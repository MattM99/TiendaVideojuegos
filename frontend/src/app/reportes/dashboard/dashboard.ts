import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardResponse } from '../dashboard.model';
import { ReportesService } from '../reportes.service';
import { ChangeDetectorRef } from '@angular/core';
import { obtenerMensajeError } from '../../shared/error/error-response';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  dashboard: DashboardResponse | null = null;
  cargando = false;
  errorMessage = '';

  private reportesService = inject(ReportesService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.cargarDashboard();
  }

  cargarDashboard(): void {
    this.cargando = true;
    this.errorMessage = '';

    this.reportesService.obtenerDashboard()
      .subscribe({
        next: (data) => {
          this.dashboard = data;
          this.cargando = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cargar dashboard', err);
          this.dashboard = null;
          this.cargando = false;
          this.errorMessage = obtenerMensajeError(
            err,
            'No se pudo cargar el dashboard de reportes.'
          );
          this.cdr.detectChanges();
        }
      });
  }

}
