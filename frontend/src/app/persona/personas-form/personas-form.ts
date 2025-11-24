import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule, MinLengthValidator } from '@angular/forms';
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

  titulo = 'Nueva persona';
  personaId?: string;

  form = this.fb.group({
    nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
    apellido: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(15)]],
    dni: ['', [Validators.required, Validators.minLength(7), Validators.maxLength(10), Validators.pattern('^[0-9]+$')]],
    email: ['', [ Validators.email]],
    telefono: ['', [Validators.minLength(7), Validators.maxLength(15), Validators.pattern('^[0-9]+$')]],
  },
  {validators: EmailTelefono}

);

  ngOnInit(): void {
    console.log("param bruto:", this.route.snapshot.paramMap.get('id'));

    this.route.queryParams.subscribe(params => {
      this.crearCuenta = params['crearCuenta'] === 'true';
    });

    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.titulo = 'Editar persona';
      this.personaId = idParam;

      this.personaService.obtenerPersona(this.personaId).subscribe({
        next: (persona: PersonaModel) => {
          this.form.patchValue(persona);
        },
        error: err => console.error('Error obteniendo persona', err)
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value as Omit<PersonaModel, 'id'>;

    if (this.personaId) {
      const personaActualizada: PersonaModel = {
        id: this.personaId,
        ...value
      };

      this.personaService.actualizarPersona(this.personaId, personaActualizada)
        .subscribe({
          next: () => this.redirigirDespues(),
          error: err => console.error('Error actualizando persona', err)
        });

    } else {
      this.personaService.crearPersona(value).subscribe({
        next: () => this.redirigirDespues(),
        error: err => console.error('Error creando persona', err)
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
