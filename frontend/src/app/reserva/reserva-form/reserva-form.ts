import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { ReservaService } from '../reserva';
import { Persona } from '../../persona/persona';
import { PersonaModel } from '../../persona/persona.model';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';
import { ReservaRequest } from '../reserva.model';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './reserva-form.html',
  styleUrl: './reserva-form.css',
})
export class ReservaForm implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  private reservaService = inject(ReservaService);
  private personaService = inject(Persona);
  private inventarioService = inject(InventarioItemService);

  inventarioId!: number;

  personas = signal<PersonaModel[]>([]);
  inventarioItem = signal<InventarioItemModel | null>(null);

  errorMessage = '';
  successMessage = '';

  form = this.fb.group({
    personaDni: ['', [Validators.required]],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('inventarioId');

    if (!idParam) {
      this.errorMessage = 'No se encontró el inventario para reservar.';
      return;
    }

    this.inventarioId = Number(idParam);

    this.cargarInventario();
    this.cargarPersonas();
  }

  cargarInventario(): void {
    this.inventarioService.getById(this.inventarioId).subscribe({
      next: (item) => {
        this.inventarioItem.set(item);
      },
      error: (err) => {
        console.error('Error cargando inventario', err);
        this.errorMessage = 'No se pudo cargar el item de inventario.';
      }
    });
  }

  cargarPersonas(): void {
    this.personaService.getAll(0, 100, 'apellido', 'asc').subscribe({
      next: (response) => {
        this.personas.set(response.content);
      },
      error: (err) => {
        console.error('Error cargando personas', err);
        this.errorMessage = 'No se pudieron cargar las personas.';
      }
    });
  }

  guardar(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const request: ReservaRequest = {
      personaDni: this.form.value.personaDni!
    };
    this.reservaService.crearReserva(this.inventarioId, request).subscribe({
      next: () => {
        this.successMessage = 'Reserva registrada correctamente.';

        setTimeout(() => {
          this.router.navigate(['/inventario']);
        }, 1000);
      },
      error: (err) => {
        console.error('Error creando reserva', err);

        this.errorMessage =
          err.error?.message ||
          err.error?.mensaje ||
          err.error ||
          'No se pudo registrar la reserva.';
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/inventario']);
  }
}
