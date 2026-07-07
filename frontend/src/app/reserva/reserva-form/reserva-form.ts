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

  personaValida = signal<boolean | null>(null);
  personaEncontrada = signal<PersonaModel | null>(null);
  inventarioItem = signal<InventarioItemModel | null>(null);

  errorMessage = signal('');
  successMessage = signal('');
  submitErrorMessage = signal('');

  form = this.fb.group({
    personaDni: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d{7,8}$/)
      ]
    ],
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('inventarioId');

    if (!idParam) {
      this.errorMessage.set('No se encontró el inventario para reservar.');
      return;
    }

    this.inventarioId = Number(idParam);

    this.cargarInventario();

    this.form.get('personaDni')?.valueChanges.subscribe(() => {
      this.personaValida.set(null);
      this.personaEncontrada.set(null);
      this.errorMessage.set('');
      this.successMessage.set('');
      this.submitErrorMessage.set('');
    });
  }

  cargarInventario(): void {
    this.inventarioService.getById(this.inventarioId).subscribe({
      next: (item) => {
        this.inventarioItem.set(item);
      },
      error: (err) => {
        console.error('Error cargando inventario', err);
        this.errorMessage.set('No se pudo cargar el item de inventario.');
      }
    });
  }

  buscarPersona(): void {
    const dniControl = this.form.get('personaDni');
    const dni = dniControl?.value;

    this.personaEncontrada.set(null);
    this.personaValida.set(null);
    this.errorMessage.set('');
    this.successMessage.set('');
    this.submitErrorMessage.set('');

    if (!dni || dniControl?.invalid) {
      dniControl?.markAsTouched();
      return;
    }

    this.personaService.obtenerPersona(dni).subscribe({
      next: (persona) => {
        this.personaEncontrada.set(persona);
        this.personaValida.set(true);
        dniControl?.setErrors(null);
      },
      error: () => {
        this.personaValida.set(false);
        dniControl?.setErrors({
          notFound: true
        });
      }
    });
  }

  private obtenerMensajeError(err: any): string {
    if (typeof err.error === 'string') {
      return err.error;
    }

    if (err.error?.message) {
      return err.error.message;
    }

    if (err.error?.mensaje) {
      return err.error.mensaje;
    }

    if (err.error?.error) {
      return err.error.error;
    }

    if (err.error?.text) {
      return err.error.text;
    }

    if (err.message) {
      return err.message;
    }

    return 'No se pudo registrar la reserva.';
  }

  guardar(): void {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.submitErrorMessage.set('');

    if (this.form.invalid || this.personaValida() !== true) {
      this.form.markAllAsTouched();
      return;
    }

    const request: ReservaRequest = {
      personaDni: this.form.value.personaDni!
    };

    this.reservaService.crearReserva(this.inventarioId, request).subscribe({
      next: () => {
        this.successMessage.set('Reserva registrada correctamente.');

        setTimeout(() => {
          this.router.navigate(['/inventario']);
        }, 1000);
      },
      error: (err) => {
        console.error('Error creando reserva', err);

        const mensaje = this.obtenerMensajeError(err);

        this.submitErrorMessage.set(mensaje);
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/inventario']);
  }
}