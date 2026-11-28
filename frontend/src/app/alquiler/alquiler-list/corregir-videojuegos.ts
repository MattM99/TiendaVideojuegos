import {
  Component, computed, DestroyRef, inject, input, OnInit, output, signal,
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { InventarioItemService } from '../../inventario-item/inventario-item.service';
import { obtenerMensajeError } from '../../shared/error/error-response';
import { Alquiler } from '../alquiler';
import { AlquilerModel, DetalleAlquilerResponse } from '../alquiler.model';

interface OpcionInventario {
  id: number;
  titulo: string;
  plataforma: string;
}

@Component({
  selector: 'app-corregir-videojuegos',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './corregir-videojuegos.html',
  styleUrls: ['../alquiler-form/alquiler-form.css', './corregir-videojuegos.css'],
})
export class CorregirVideojuegos implements OnInit {
  private readonly alquilerService = inject(Alquiler);
  private readonly inventarioService = inject(InventarioItemService);
  private readonly formBuilder = inject(FormBuilder);
  private readonly destroyRef = inject(DestroyRef);
  private readonly catalogo = signal<OpcionInventario[]>([]);

  readonly alquiler = input.required<AlquilerModel>();
  readonly bloqueado = input(false);
  readonly guardado = output<AlquilerModel>();

  readonly formulario = this.formBuilder.group({
    detalleId: this.formBuilder.control<number | null>(null, Validators.required),
    destino: this.formBuilder.control<OpcionInventario | null>(null, Validators.required),
    cantidad: this.formBuilder.control<number | null>(null, [
      Validators.required, Validators.min(1), Validators.pattern(/^\d+$/),
    ]),
  });
  private readonly detalleElegido = toSignal(this.formulario.controls.detalleId.valueChanges);
  readonly detalleActual = computed(() => this.buscarDetalle(this.detalleElegido()));
  private readonly destinoElegido = toSignal(this.formulario.controls.destino.valueChanges);
  readonly opcionesDestino = computed(() => {
    const actual = this.detalleActual();
    const opciones = new Map<number, OpcionInventario>();
    const opcionesFijas = [actual && this.inventarioActual(actual), this.destinoElegido()];

    for (const opcion of [...opcionesFijas, ...this.catalogo()]) {
      if (opcion) opciones.set(opcion.id, opcion);
    }

    return [...opciones.values()];
  });

  readonly cargandoCatalogo = signal(false);
  readonly paginaCatalogo = signal(0);
  readonly totalPaginasCatalogo = signal(0);
  readonly errorCatalogo = signal('');
  readonly guardando = signal(false);
  readonly errorGuardado = signal('');
  readonly correccionGuardada = signal(false);

  ngOnInit(): void {
    this.formulario.valueChanges.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.errorGuardado.set('');
        this.correccionGuardada.set(false);
      });

    this.formulario.controls.detalleId.valueChanges.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(id => {
        const detalle = this.buscarDetalle(id);
        this.formulario.controls.destino.reset(detalle && this.inventarioActual(detalle));
        this.formulario.controls.cantidad.reset(detalle?.cantidad ?? null);
      });

    const primerDetalle = this.alquiler().carrito[0];
    if (primerDetalle) {
      this.formulario.controls.detalleId.setValue(primerDetalle.detalleAlquilerId);
      this.cargarCatalogo(0);
    }
  }

  tituloVideojuego(detalle: DetalleAlquilerResponse): string {
    return detalle.inventario.videojuego?.titulo
      ?? detalle.inventario.videojuegoResponse?.titulo
      ?? 'Videojuego sin título';
  }

  readonly mismoInventario = (a: OpcionInventario | null, b: OpcionInventario | null): boolean =>
    a?.id === b?.id;

  cargarCatalogo(pagina: number): void {
    if (this.cargandoCatalogo() || pagina < 0) return;

    this.errorCatalogo.set('');
    this.cargandoCatalogo.set(true);
    this.inventarioService.getPage(pagina, 20, 'inventarioItemId', 'asc').pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.cargandoCatalogo.set(false)),
    ).subscribe({
      next: respuesta => {
        const opciones: OpcionInventario[] = [];
        for (const inventario of respuesta.content) {
          if (!inventario.inventarioId || inventario.activo === false) continue;
          opciones.push({
            id: inventario.inventarioId,
            titulo: inventario.videojuego.titulo,
            plataforma: inventario.plataforma,
          });
        }
        this.catalogo.set(opciones);
        this.paginaCatalogo.set(pagina);
        this.totalPaginasCatalogo.set(respuesta.totalPages);
      },
      error: err => this.errorCatalogo.set(
        obtenerMensajeError(err, 'No se pudo cargar el catálogo de inventario.')
      ),
    });
  }

  guardar(): void {
    if (this.bloqueado() || this.guardando() || this.formulario.invalid) return;
    const { detalleId, destino, cantidad } = this.formulario.getRawValue();
    if (!detalleId || !destino || cantidad === null) return;

    this.errorGuardado.set('');
    this.correccionGuardada.set(false);
    this.guardando.set(true);
    this.alquilerService.corregirDetalle(this.alquiler().alquilerId, detalleId, {
      inventarioItemId: destino.id,
      cantidad,
    }).pipe(
      takeUntilDestroyed(this.destroyRef),
      finalize(() => this.guardando.set(false)),
    ).subscribe({
      next: respuesta => {
        if (respuesta.status === 200 && respuesta.body) {
          this.guardado.emit(respuesta.body);
          this.correccionGuardada.set(true);
        } else {
          this.errorGuardado.set('El servidor no confirmó la corrección del alquiler.');
        }
      },
      error: err => {
        const mensaje = obtenerMensajeError(err, 'No se pudo corregir el detalle del alquiler.');
        this.errorGuardado.set(mensaje.replace(
          /^El item: \d+ no tiene suficiente stock disponible para disminuir\. Copias disponibles: (\d+)$/,
          'El videojuego seleccionado no tiene suficiente stock disponible. Unidades disponibles: $1.'
        ));
      },
    });
  }

  private buscarDetalle(id: number | null | undefined): DetalleAlquilerResponse | null {
    return this.alquiler().carrito.find(detalle => detalle.detalleAlquilerId === id) ?? null;
  }

  private inventarioActual(detalle: DetalleAlquilerResponse): OpcionInventario | null {
    const id = detalle.inventario.inventarioId ?? detalle.inventario.inventarioItemId;
    if (!id) return null;

    return { id, titulo: this.tituloVideojuego(detalle), plataforma: detalle.inventario.plataforma };
  }
}
