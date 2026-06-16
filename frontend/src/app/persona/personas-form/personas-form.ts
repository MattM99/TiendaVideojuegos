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
  private mapError(err: any): string {
  if (err.status === 403) {
    return 'No posee permisos para modificar este usuario';
  }

  if (err.status === 404) {
    return 'Persona no encontrada';
  }

  return err.error?.message || err.error || 'Error inesperado';
}




  titulo = 'Nueva persona';
  dniPersona?: string;

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
    apellido: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
    dni: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(10), Validators.pattern('^[0-9]+$')]],
    email: ['', [Validators.email]],
    telefono: ['', [Validators.minLength(7), Validators.maxLength(15), Validators.pattern('^[0-9]+$')]],
  },
    { validators: EmailTelefono }

  );

  ngOnInit(): void {

    this.route.queryParams.subscribe(params => {
      this.crearCuenta = params['crearCuenta'] === 'true';
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
            this.errorMessage = '';
                  this.errorMessage = this.mapError(err);

          }

        });

    }

    else {

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
            this.errorMessage = this.mapError(err);
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
