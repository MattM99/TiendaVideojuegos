import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';
import { Persona } from '../../persona/persona';
import { VideojuegoService } from '../../videojuego/videojuego.service';
import { VideojuegoModel } from '../../videojuego/videojuego.model';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';
import { fechaValida, noFechaPasada } from '../../shared/validators/date.validator/date.validator';
import { CarritoItemModel } from '../../models/carritoItem.model';

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
  private alquilerService = inject(Alquiler);
  private personaService = inject(Persona);
  private videojuegoService = inject(VideojuegoService);
  private inventarioService = inject(InventarioItemService);

  personas = computed(() => this.personaService.personas());
  videojuegos = signal<VideojuegoModel[]>([]);
  inventario = signal<InventarioItemModel[]>([]);
  videojuegoMap = computed(() => {
    const map: Record<string, string> = {};
    this.videojuegos().forEach(j => {
      if (j.id) {       // ✅ solo si existe id
        map[j.id] = j.titulo;
      }
    });
    return map;
  });

  titulo = 'Nuevo alquiler';
  alquilerId?: string;

  form = this.fb.group({
    personaId: ['', [Validators.required]],
    inventarioId: ['', [Validators.required]],
    fechaInicio: ['', [Validators.required, noFechaPasada]],
    fechaFin: ['', [Validators.required, fechaValida]],
    montoFijo: [0, [Validators.required, Validators.min(0)]],
    fechaDevolucion: [''],
  });

  ngOnInit(): void {
    this.personaService.cargarPersonas();

    // Cargar videojuegos
    this.videojuegoService.getAll().subscribe({
      next: juegos => this.videojuegos.set(juegos),
      error: err => console.error(err)
    });

    // Cargar inventario disponible
    this.inventarioService.getAll().subscribe({
      next: items => this.inventario.set(items.filter(i => i.enLocal > 0)),
      error: err => console.error(err)
    });

    // Editar alquiler si viene ID
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.titulo = 'Editar alquiler';
      this.alquilerId = idParam;

      this.alquilerService.obtenerAlquiler(this.alquilerId).subscribe({
        next: alquiler => {
          this.form.patchValue({
            personaId: alquiler.personaId,
            inventarioId: alquiler.inventarioId,
            fechaInicio: alquiler.fechaInicio,
            fechaFin: alquiler.fechaFin,
            montoFijo: alquiler.montoFijo,
            fechaDevolucion: alquiler.fechaDevolucion ?? ''
          });
        },
        error: err => console.error(err)
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value;
    const item = this.inventario().find(i => i.id === value.inventarioId);

    const base: Omit<AlquilerModel, 'id'> = {
      personaId: value.personaId!,
      inventarioId: value.inventarioId!,
      videojuegoId: item!.videojuegoId,
      fechaInicio: value.fechaInicio!,
      fechaFin: value.fechaFin!,
      montoFijo: value.montoFijo!,
      fechaDevolucion: value.fechaDevolucion || undefined,
      detalles: [] as CarritoItemModel[],
      penalizaciones: []
    };

    if (this.alquilerId) {
      // Actualizar
      const actualizado: AlquilerModel = { id: this.alquilerId, ...base };
      this.alquilerService.actualizarAlquiler(this.alquilerId, actualizado).subscribe({
        next: () => this.router.navigate(['/alquileres']),
        error: err => console.error(err)
      });
    } else {
      // Crear nuevo alquiler
      this.alquilerService.crearAlquiler(base).subscribe({
        next: () => {
          // Descontar stock al crear
          this.inventarioService.descontarStock(value.inventarioId!).subscribe({
            next: () => console.log('Stock descontado'),
            error: err => console.error(err)
          });

          this.router.navigate(['/alquileres']);
        },
        error: err => console.error(err)
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}
