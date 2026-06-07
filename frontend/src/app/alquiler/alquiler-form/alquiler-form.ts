import { Component, OnInit, inject, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormArray } from '@angular/forms';
import { Router } from '@angular/router';
import { Alquiler } from '../alquiler';
import { Persona } from '../../persona/persona';
import { PersonaModel } from '../../persona/persona.model';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';
import {fechaValida, noFechaFutura, rangoFechasValidas} from '../../shared/validators/date.validator/date.validator';
import { CrearAlquilerRequest } from '../alquiler-request.model';
import { stockValidator } from '../../shared/validators/stockValidator';

@Component({
  selector: 'app-alquiler-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './alquiler-form.html',
  styleUrl: './alquiler-form.css',
})
export class AlquilerForm implements OnInit {

  private fb = inject(FormBuilder);
  private router = inject(Router);

  private alquilerService = inject(Alquiler);
  private personaService = inject(Persona);
  private inventarioService = inject(InventarioItemService);

  personaValida = signal<boolean | null>(null);
  personaEncontrada = signal<PersonaModel | null>(null);
  inventario = signal<InventarioItemModel[]>([]);
  hoy = new Date().toISOString().split('T')[0];

  titulo = 'Nuevo alquiler';

  alquilerId?: string;

  form = this.fb.group({
    personaDni: ['', Validators.required],
    fechaInicio: ['', [Validators.required, noFechaFutura]],
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
    const group = this.fb.group({
      inventarioItemId: [null, Validators.required],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });

    group.get('inventarioItemId')?.valueChanges.subscribe(inventarioId => {
      const item = this.inventario().find(i => i.inventarioId === Number(inventarioId));
      const stockDisponible = item?.stockDisponible ?? 0;

      const cantidadControl = group.get('cantidad');

      cantidadControl?.setValidators([Validators.required, Validators.min(1),
        (ctrl) => ctrl.value > stockDisponible ? { stockInsuficiente: true } : null
      ]);

      cantidadControl?.updateValueAndValidity();
    });
    return group;
    }




/*
  crearDetalle(): FormGroup {
    return this.fb.group({
      inventarioItemId: [null, Validators.required],
      cantidad: [1, [Validators.required, Validators.min(1)]]
    });
  }
*/
  get detalles(): FormArray<FormGroup> {
    return this.form.get('detalles') as FormArray<FormGroup>;
  }

  agregarDetalle(): void {
    this.detalles.push(this.crearDetalle());
  }

  eliminarDetalle(index: number): void {
    this.detalles.removeAt(index);
  }

  obtenerStockDisponible(index: number): number {
  const inventarioId = this.detalles.at(index).get('inventarioItemId')?.value;

  const item = this.inventario().find(
    i => i.inventarioId == inventarioId
  );

  return item?.stockDisponible ?? 0;
}

  buscarPersona(): void {
    const dni = this.form.get('personaDni')?.value;

    if (!dni || dni.length < 7) return;

    this.personaEncontrada.set(null);
    this.form.get('personaDni')?.setErrors(null);
    this.personaValida.set(null);

    this.personaService.obtenerPersona(dni).subscribe({
      next: (persona) => {
        this.personaEncontrada.set(persona);
        this.personaValida.set(true);
        this.form.get('personaDni')?.setErrors(null);
      },
      error: () => {
        this.personaValida.set(false);
        this.form.get('personaDni')?.setErrors({
          notFound: true
        });

      }
    });
  }

  onSubmit(): void {

    console.log('FORM VALID', this.form.valid);
console.log('FORM INVALID', this.form.invalid);
console.log(this.form);

console.log(this.detalles.valid);
console.log(this.detalles.errors);

this.detalles.controls.forEach((detalle, i) => {
  console.log('Detalle', i);
  console.log('Valid:', detalle.valid);
  console.log('Errors:', detalle.errors);
  console.log('Cantidad:', detalle.get('cantidad')?.errors);
});

    Object.keys(this.form.controls).forEach(key => {
      const control = this.form.get(key);
      console.log(key, control?.status, control?.errors);
    });

    if (this.form.invalid) {
      this.form.markAllAsTouched();
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

    this.alquilerService.crearAlquiler(request).subscribe({
      next: () => this.router.navigate(['/alquileres']),
      error: (err) => console.error(err)
    });
  }


  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}
