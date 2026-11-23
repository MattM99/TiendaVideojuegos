import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, Validators, ReactiveFormsModule, FormGroup, FormArray, ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';
import { Persona } from '../../persona/persona';
import { PersonaModel } from '../../persona/persona.model';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';
import { fechaValida, noFechaFutura, rangoFechasValidas } from '../../shared/validators/date.validator/date.validator';
import { CrearAlquilerRequest } from '../alquiler-request.model';
import { finalize } from 'rxjs';

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
  private route = inject(ActivatedRoute);

  private alquilerService = inject(Alquiler);
  private personaService = inject(Persona);
  private inventarioService = inject(InventarioItemService);

  personaValida = signal<boolean | null>(null);
  personaEncontrada = signal<PersonaModel | null>(null);
  inventario = signal<InventarioItemModel[]>([]);
  esEdicion = signal(false);
  guardando = signal(false);
  alquilerOriginal = signal<AlquilerModel | null>(null);
  hoy = new Date().toISOString().split('T')[0];

  titulo = 'Nuevo alquiler';

  alquilerId?: number;

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

    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam !== null) {
      const id = Number(idParam);

      if (!Number.isInteger(id) || id <= 0) {
        alert('El identificador del alquiler no es válido.');
        this.router.navigate(['/alquileres']);
        return;
      }

      this.configurarModoEdicion(id);
      return;
    }

    this.cargarDatosCreacion();
  }

  private cargarDatosCreacion(): void {
    this.inventarioService.getAll().subscribe({
      next: (data) => this.inventario.set(data),
      error: (err) => console.error('Error cargando inventario', err)
    });

    this.personaService.getAll(0, 100, 'apellido', 'asc').subscribe();
  }

  private configurarModoEdicion(id: number): void {
    this.esEdicion.set(true);
    this.alquilerId = id;
    this.titulo = 'Editar alquiler';

    this.form.get('personaDni')?.disable({ emitEvent: false });
    this.form.get('fechaInicio')?.disable({ emitEvent: false });
    this.detalles.disable({ emitEvent: false });
    this.personaValida.set(true);

    this.form.get('fechaFin')?.addValidators([
      this.fechaFinFutura,
      (control: AbstractControl): ValidationErrors | null => {
        const fechaInicio = this.alquilerOriginal()?.fechaInicio;

        if (!control.value || !fechaInicio) return null;

        return control.value > fechaInicio
          ? null
          : { fechaFinNoPosterior: true };
      }
    ]);

    this.alquilerService.obtenerAlquiler(id).subscribe({
      next: (alquiler) => {
        if (alquiler.activo === false) {
          alert('No se puede editar un alquiler dado de baja.');
          this.router.navigate(['/alquileres']);
          return;
        }

        if (alquiler.estadoAlquiler !== 'EN_CURSO') {
          alert('No se puede editar un alquiler finalizado.');
          this.router.navigate(['/alquileres']);
          return;
        }

        this.alquilerOriginal.set(alquiler);

        this.form.patchValue({
          personaDni: String(alquiler.personaResponse.dni),
          fechaInicio: alquiler.fechaInicio,
          fechaFin: alquiler.fechaFin
        });

        this.form.get('fechaFin')?.updateValueAndValidity();
        this.form.updateValueAndValidity();
      },
      error: (err) => {
        console.error('Error cargando alquiler', err);
        alert('No se pudo obtener el alquiler para editar.');
        this.router.navigate(['/alquileres']);
      }
    });
  }

  private fechaFinFutura(control: AbstractControl): ValidationErrors | null {
    if (!control.value) return null;

    const fechaFin = new Date(`${control.value}T00:00:00`);
    const hoy = new Date();
    hoy.setHours(0, 0, 0, 0);

    if (Number.isNaN(fechaFin.getTime())) return null;

    return fechaFin > hoy ? null : { fechaFinNoFutura: true };
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

  reservarDetalle(index: number): void {
    const inventarioId = this.detalles.at(index).get('inventarioItemId')?.value;

    if (!inventarioId) return;

    this.router.navigate(['/reservas/nueva', inventarioId]);
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

    if (this.esEdicion()) {
      this.guardarEdicion();
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

  private guardarEdicion(): void {
    if (this.guardando()) return;

    const fechaFin = this.form.get('fechaFin')?.value;

    if (!this.alquilerId || !fechaFin) return;

    this.guardando.set(true);

    this.alquilerService.actualizarAlquiler(
      this.alquilerId,
      { fechaFin }
    ).pipe(
      finalize(() => this.guardando.set(false))
    ).subscribe({
      next: () => this.router.navigate(['/alquileres']),
      error: (err) => {
        console.error('Error actualizando alquiler', err);
        alert('No se pudo actualizar la fecha de finalización del alquiler.');
      }
    });
  }


  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}
