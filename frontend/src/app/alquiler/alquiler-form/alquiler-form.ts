import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, Validators, ReactiveFormsModule, ValidationErrors } from '@angular/forms';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel, DisponibilidadItem, ValidarDisponibilidadResponse } from '../alquiler.model';
import { Persona } from '../../persona/persona';
import { PersonaModel } from '../../persona/persona.model';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { InventarioItemModel } from '../../inventario-item/inventario-item.model';
import { fechaValida, noFechaFutura, rangoFechasValidas } from '../../shared/validators/date.validator/date.validator';
import { CrearAlquilerRequest } from '../alquiler-request.model';
import { finalize, Subscription } from 'rxjs';
import { obtenerMensajeError } from '../../shared/error/error-response';

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
  private destroyRef = inject(DestroyRef);

  private alquilerService = inject(Alquiler);
  private personaService = inject(Persona);
  private inventarioService = inject(InventarioItemService);

  personaValida = signal<boolean | null>(null);
  personaEncontrada = signal<PersonaModel | null>(null);
  inventario = signal<InventarioItemModel[]>([]);
  esEdicion = signal(false);
  guardando = signal(false);
  alquilerOriginal = signal<AlquilerModel | null>(null);
  errorMessage = signal('');
  disponibilidad = signal<ValidarDisponibilidadResponse | null>(null);
  validandoDisponibilidad = signal(false);
  creando = signal(false);
  private consultaDisponibilidad?: Subscription;
  private busquedaPersona?: Subscription;
  hoy = new Date().toISOString().split('T')[0];

  titulo = 'Nuevo alquiler';

  alquilerId?: number;

  form = this.fb.group({
    personaDni: ['', Validators.required],
    fechaInicio: ['', [Validators.required, noFechaFutura]],
    fechaFin: ['', [Validators.required, fechaValida]],

    detalles: this.fb.array([
      this.crearDetalle()
    ], Validators.required),
  }, { validators: rangoFechasValidas });


  ngOnInit(): void {
    this.form.valueChanges.subscribe(() => {
      this.errorMessage.set('');
      if (!this.esEdicion()) this.invalidarDisponibilidad();
    });

    this.form.get('personaDni')?.valueChanges.subscribe(() => {
      this.busquedaPersona?.unsubscribe();
      this.personaEncontrada.set(null);
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
    this.errorMessage.set('');

    this.inventarioService.getAll().subscribe({
      next: (data) => this.inventario.set(data),
      error: err => {
        this.errorMessage.set(
          obtenerMensajeError(err, 'No se pudo cargar el inventario.')
        );
      }
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
    this.errorMessage.set('');

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
        alert(
          obtenerMensajeError(
            err,
            'No se pudo obtener el alquiler para editar.'
          )
        );
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

  crearDetalle() {
    return this.fb.group({
      inventarioItemId: this.fb.control<number | null>(null, Validators.required),
      cantidad: [1, [Validators.required, Validators.min(1), Validators.pattern(/^\d+$/)]]
    });
  }

  get detalles() {
    return this.form.controls.detalles;
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
    this.busquedaPersona?.unsubscribe();
    this.invalidarDisponibilidad();

    this.personaEncontrada.set(null);
    this.form.get('personaDni')?.setErrors(null);
    this.personaValida.set(null);
    this.errorMessage.set('');

    this.busquedaPersona = this.personaService.obtenerPersona(dni).pipe(
      takeUntilDestroyed(this.destroyRef)
    ).subscribe({
      next: (persona) => {
        this.personaEncontrada.set(persona);
        this.personaValida.set(true);
        this.form.get('personaDni')?.setErrors(null);
      },
      error: err => {
        this.personaValida.set(false);
        this.form.get('personaDni')?.setErrors({
          notFound: true
        });
        this.errorMessage.set(
          obtenerMensajeError(err, 'No se pudo validar la persona.')
        );

      }
    });
  }

  onSubmit(): void {

    this.errorMessage.set('');

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

    if (!this.puedeCrearAlquiler()) return;

    this.creando.set(true);
    this.alquilerService.crearAlquiler(this.obtenerSolicitudCreacion()).pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.creando.set(false))
    ).subscribe({
      next: () => this.router.navigate(['/alquileres']),
      error: err => {
        this.invalidarDisponibilidad();
        this.errorMessage.set(
          obtenerMensajeError(err, 'No se pudo crear el alquiler.')
        );
      }
    });
  }

  puedeVerificarDisponibilidad(): boolean {
    return !this.esEdicion() && this.form.valid && this.personaValida() === true
      && !this.validandoDisponibilidad() && !this.creando();
  }

  verificarDisponibilidad(): void {
    if (!this.puedeVerificarDisponibilidad()) return;

    this.invalidarDisponibilidad();
    this.errorMessage.set('');
    this.validandoDisponibilidad.set(true);

    this.consultaDisponibilidad = this.alquilerService.validarDisponibilidad(this.obtenerSolicitudCreacion()).pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.validandoDisponibilidad.set(false))
    ).subscribe({
      next: respuesta => this.disponibilidad.set(respuesta),
      error: err => this.errorMessage.set(
        obtenerMensajeError(err, 'No se pudo verificar la disponibilidad.')
      )
    });
  }

  puedeCrearAlquiler(): boolean {
    const resultado = this.disponibilidad();
    return this.puedeVerificarDisponibilidad() && resultado?.puedeCrearAlquiler === true
      && resultado.faltantes.length === 0
      && this.detalles.controls.every(detalle => {
        const { inventarioItemId, cantidad } = detalle.getRawValue();
        const item = this.obtenerDisponibilidad(inventarioItemId);
        return item !== undefined && item.motivo === null && item.cantidadSolicitada === cantidad
          && item.cantidadDisponibleParaCliente >= item.cantidadSolicitada;
      });
  }

  obtenerDisponibilidad(inventarioId: number | null): DisponibilidadItem | undefined {
    return this.disponibilidad()?.disponibilidad.find(item => item.inventarioItemId === inventarioId);
  }

  private invalidarDisponibilidad(): void {
    this.consultaDisponibilidad?.unsubscribe();
    this.disponibilidad.set(null);
  }

  private obtenerSolicitudCreacion(): CrearAlquilerRequest {
    const value = this.form.getRawValue();
    return {
      personaDni: value.personaDni!,
      fechaInicio: value.fechaInicio!,
      fechaFin: value.fechaFin!,
      detalles: value.detalles.map(detalle => ({
        inventarioItemId: detalle.inventarioItemId!,
        cantidad: detalle.cantidad!
      }))
    };
  }

  private guardarEdicion(): void {
    if (this.guardando()) return;

    const fechaFin = this.form.get('fechaFin')?.value;

    if (!this.alquilerId || !fechaFin) return;

    this.errorMessage.set('');
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
        this.errorMessage.set(
          obtenerMensajeError(
            err,
            'No se pudo actualizar la fecha de finalización del alquiler.'
          )
        );
      }
    });
  }


  cancelar(): void {
    this.router.navigate(['/alquileres']);
  }
}
