import { Component, OnInit, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AlquilerService } from '../alquiler.service';
import { AlquilerModel } from '../alquiler.model';
import { PersonaService } from '../../persona/persona.service';

@Component({
  selector: 'app-alquiler-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './alquiler-form.html',
  styleUrl: './alquiler-form.css',
})
export class AlquilerForm implements OnInit {

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private alquilerService = inject(AlquilerService);
  private personaService = inject(PersonaService);

  personas = computed(() => this.personaService.personas());

  titulo = 'Nuevo alquiler';
  alquilerId?: string;

  form = this.fb.group({
    personaId: ['', Validators.required],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
    montoFijo: [0, [Validators.required, Validators.min(0)]],
    fechaDevolucion: [''],
  });

  ngOnInit(): void {
    this.personaService.cargarPersonas();

    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.titulo = 'Editar alquiler';
      this.alquilerId = idParam;

      this.alquilerService.obtenerAlquiler(this.alquilerId).subscribe({
        next: (alquiler: AlquilerModel) => {
          this.form.patchValue({
            personaId: alquiler.personaId,
            fechaInicio: alquiler.fechaInicio,
            fechaFin: alquiler.fechaFin,
            montoFijo: alquiler.montoFijo,
            fechaDevolucion: alquiler.fechaDevolucion ?? ''
          });
        },
        error: err => console.error('Error obteniendo alquiler', err),
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value;

    const base: Omit<AlquilerModel, 'id'> = {
      personaId: value.personaId!,
      fechaInicio: value.fechaInicio!,
      fechaFin: value.fechaFin!,
      montoFijo: value.montoFijo!,
      fechaDevolucion: value.fechaDevolucion || undefined,
      detalles: [],
      penalizaciones: []
    };

    if (this.alquilerId) {
      const alquilerActualizado: AlquilerModel = {
        id: this.alquilerId,
        ...base
      };

      this.alquilerService.actualizarAlquiler(this.alquilerId, alquilerActualizado)
        .subscribe({
          next: () => this.router.navigate(['/alquileres']),
          error: err => console.error('Error actualizando alquiler', err),
        });

    } else {
      this.alquilerService.crearAlquiler(base).subscribe({
        next: () => this.router.navigate(['/alquileres']),
        error: err => console.error('Error creando alquiler', err),
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}