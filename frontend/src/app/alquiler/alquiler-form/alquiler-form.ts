import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormArray } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Alquiler } from '../alquiler';

import { Persona } from '../../persona/persona';

import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';

import {
  fechaValida,
  rangoFechasValidas
} from '../../shared/validators/date.validator/date.validator';
import { CrearAlquilerRequest } from '../alquiler-request.model';

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
  personaValida = signal<boolean | null>(null);
  private inventarioService = inject(InventarioItemService);

  inventario = signal<InventarioItemModel[]>([]);

  titulo = 'Nuevo alquiler';

  alquilerId?: string;

  form = this.fb.group({
    personaDni: ['', Validators.required],
    fechaInicio: ['', [Validators.required]],
    fechaFin: ['', [Validators.required, fechaValida]],

    detalles: this.fb.array([
      this.crearDetalle()
    ]),
    }, { validators: rangoFechasValidas });


  ngOnInit(): void {
    this.form.get('personaDni')?.valueChanges.subscribe(() => {
      this.personaValida.set(null);
    });


    this.inventarioService.getAll().subscribe({
      next: (data) => this.inventario.set(data),
      error: (err) => console.error('Error cargando inventario', err)
    });

    this.personaService.getAll(0, 100, 'apellido', 'asc').subscribe();
  }

  crearDetalle(): FormGroup {
    return this.fb.group({
      inventarioItemId: [null, Validators.required],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });
  }

  get detalles(): FormArray<FormGroup> {
    return this.form.get('detalles') as FormArray<FormGroup>;
  }

  agregarDetalle(): void {
    this.detalles.push(this.crearDetalle());
  }

  eliminarDetalle(index: number): void {
    this.detalles.removeAt(index);
  }

  buscarPersona(): void {
    const dni = this.form.get('personaDni')?.value;

    if (!dni || dni.length < 7) return;

    this.personaValida.set(null);

    this.personaService.obtenerPersona(dni).subscribe({
      next: () => {
        this.personaValida.set(true);
      },
      error: () => {
        this.personaValida.set(false);
      }
    });
  }

  onSubmit(): void {

    Object.keys(this.form.controls).forEach(key => {
  const control = this.form.get(key);
  console.log(key, control?.status, control?.errors);
});

    if (this.form.invalid ) { /*|| this.personaValida() !== true*/
      this.form.markAllAsTouched();
      console.log('FORM STATUS:', this.form.status);
console.log('FORM VALUE:', this.form.value);
console.log('DETALLES:', this.detalles.value);
      return;
    }

    const value = this.form.getRawValue();

    const request: CrearAlquilerRequest = {
      personaDni: Number(value.personaDni),
      fechaInicio: value.fechaInicio!,
      fechaFin: value.fechaFin!,
      detalles: value.detalles!.map((d: any) => ({
        inventarioItemId: Number(d.inventarioItemId),
        cantidad: Number(d.cantidad)
      }))
    };

console.log('ANTES DE HTTP');
    this.alquilerService.crearAlquiler(request).subscribe({
      next: () => this.router.navigate(['/alquileres']),
      error: (err) => console.error(err)
    });
  }


  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}
