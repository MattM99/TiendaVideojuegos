import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardResponse } from '../dashboard.model';
import { ReportesService } from '../reportes.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
   //dashboard?: DashboardResponse;
   dashboard: DashboardResponse | null = null;

  private reportesService = inject(ReportesService);
  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
     console.log('🔥 DASHBOARD COMPONENT ACTIVO');
    this.cargarDashboard();
  }

  cargarDashboard(): void {
      console.log('1 - llamando service');

    this.reportesService.obtenerDashboard()
      .subscribe({
        next: (data) => {
                    console.log('2 - respuesta recibida:', data);

                  this.dashboard = data;
        console.log('3 - asignado dashboard:', this.dashboard);
          this.cdr.detectChanges(); // 🔥 CLAVE

        },
        error: (err) => {
          console.error('Error al cargar dashboard', err);
        },
         complete: () => {
        console.log('4 - complete');
      }
      });
  }

}
