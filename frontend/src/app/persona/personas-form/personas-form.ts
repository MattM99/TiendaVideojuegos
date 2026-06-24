import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Persona } from '../persona';
import { PersonaModel } from '../persona.model';
import { EmailTelefono } from '../../shared/validators/email-telefono/email-telefono';
@Component({
  selector: 'app-personas-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './personas-form.html',
  styleUrl: './personas-form.css',
})
export class PersonasForm implements OnInit {
  crearCuenta: boolean = false;

  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private personaService = inject(Persona);

  public errorMessage = '';

  titulo = 'Nueva persona';
  dniPersona?: string;

  form = this.fb.group({
    nombre: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/)
      ]
    ],

    apellido: [
      '',
      [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(50),
        Validators.pattern(/^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$/)
      ]
    ],

    dni: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d{7,8}$/)
      ]
    ],

    email: [
      '',
      [
        Validators.required,
        Validators.email
      ]
    ],

    telefono: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d{7,15}$/)
      ]
    ],
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.crearCuenta = params['crearCuenta'] === 'true';
    });

    this.form.get('dni')?.valueChanges.subscribe(() => {
      this.limpiarErrorCampo('dni', 'dniDuplicado');
    });

    this.form.get('email')?.valueChanges.subscribe(() => {
      this.limpiarErrorCampo('email', 'emailDuplicado');
    });

    const dniParam = this.route.snapshot.paramMap.get('dni');

    if (dniParam) {
      this.titulo = 'Editar persona';
      this.dniPersona = dniParam;

      this.personaService.obtenerPersona(this.dniPersona).subscribe({
        next: (persona: PersonaModel) => {
          this.form.patchValue(persona);
          this.form.get('dni')?.disable();
        },
        error: err => console.error('Error obteniendo persona', err)
      });
    }
  }

  private limpiarErrorCampo(nombreCampo: string, nombreError: string): void {
    const control = this.form.get(nombreCampo);

    if (!control || !control.errors?.[nombreError]) return;

    const errores = { ...control.errors };
    delete errores[nombreError];

    control.setErrors(
      Object.keys(errores).length ? errores : null
    );
  }

  private mapError(err: any): string {
    if (err.status === 403) {
      return 'No posee permisos para modificar este usuario';
    }

    if (err.status === 404) {
      return 'Persona no encontrada';
    }

    return err.error?.message || err.error?.mensaje || err.error || 'Error inesperado';
  }

  private aplicarErroresBackend(err: any): void {
    const mensaje = this.mapError(err);
    const mensajeLower = mensaje.toLowerCase();

    if (mensajeLower.includes('dni')) {
      const dniControl = this.form.get('dni');

      dniControl?.setErrors({
        ...dniControl.errors,
        dniDuplicado: true
      });

      dniControl?.markAsTouched();
      return;
    }

    if (mensajeLower.includes('email')) {
      const emailControl = this.form.get('email');

      emailControl?.setErrors({
        ...emailControl.errors,
        emailDuplicado: true
      });

      emailControl?.markAsTouched();
      return;
    }

    this.errorMessage = mensaje;
  }

  onSubmit(): void {
    this.errorMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    if (this.dniPersona) {
      const value = {
        nombre: this.form.value.nombre ?? undefined,
        apellido: this.form.value.apellido ?? undefined,
        telefono: this.form.value.telefono ?? undefined,
        email: this.form.value.email ?? undefined
      };

      this.personaService
        .actualizarPersona(this.dniPersona, value)
        .subscribe({
          next: () => this.redirigirDespues(),
          error: err => {
            this.aplicarErroresBackend(err);
          }
        });

    } else {
      const value: PersonaModel = {
        nombre: this.form.value.nombre!,
        apellido: this.form.value.apellido!,
        dni: this.form.value.dni!,
        email: this.form.value.email ?? undefined,
        telefono: this.form.value.telefono ?? undefined
      };

      this.personaService
        .crearPersona(value)
        .subscribe({
          next: () => this.redirigirDespues(),
          error: err => {
            this.aplicarErroresBackend(err);
          }
        });
    }
  }

  private redirigirDespues() {
    if (this.crearCuenta) {
      this.router.navigate(['/cuentas/nuevo']);
    } else {
      this.router.navigate(['/personas']);
    }
  }

  cancelar(): void {
    this.router.navigate(['/personas']);
  }
}
