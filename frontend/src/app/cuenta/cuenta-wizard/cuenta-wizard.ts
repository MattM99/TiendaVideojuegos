import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Persona } from '../../persona/persona';
import { AuthService } from '../../auth/auth-service/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cuenta-wizard',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cuenta-wizard.html',
  styleUrls: ['./cuenta-wizard.css'],
  standalone: true
})
export class CuentaWizardComponent {
  private router = inject(Router);
  private fb = inject(FormBuilder);

  step = signal<number>(1);
  modo = signal<'NUEVO' | 'EXISTENTE' | null>(null);

  private authService = inject(AuthService);

  private personaService = inject(Persona);
  personaEncontrada: any = null;

  busquedaRealizada = signal(false);

  form = this.fb.group({
    dni: ['',[Validators.required, Validators.pattern(/^\d{7,8}$/)]],
    nombre: ['',[Validators.required, Validators.minLength(2)]],
     apellido: ['',[Validators.required, Validators.minLength(2)]],
    email: ['',[Validators.required, Validators.email]],
    telefono: ['',[Validators.required, Validators.pattern(/^\d{7,15}$/)]],

    nickname: ['',[Validators.required, Validators.minLength(4),Validators.maxLength(15)]],
    password: ['',[Validators.required, Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{5,15}$/)]],
    rol: ['EMPLEADO']
  });

  seleccionarModo(tipo: 'NUEVO' | 'EXISTENTE') {
    this.modo.set(tipo);
    this.step.set(2);
  }

  buscarPersona() {
    const dni = this.form.get('dni')?.value;

    if (!dni) return;

    this.busquedaRealizada.set(false);

    this.personaService.obtenerPersona(dni).subscribe({
      next: (persona) => {
        this.personaEncontrada = persona;

        this.busquedaRealizada.set(true);

        if (persona) {
          this.form.patchValue({
            nombre: persona.nombre,
            apellido: persona.apellido,
            email: persona.email,
            telefono: persona.telefono
          });
        }
      },
      error: () => {
        this.personaEncontrada = null;
        this.busquedaRealizada.set(true);

      }
    });
  }

  guardar() {

    if (this.form.invalid) return;

    const payload = {
      dni: this.form.value.dni ?? '',
      nombre: this.form.value.nombre ?? '',
      apellido: this.form.value.apellido ?? '',
      email: this.form.value.email ?? '',
      telefono: this.form.value.telefono ?? '',
      nickname: this.form.value.nickname ?? '',
      password: this.form.value.password ?? '',
      rol: this.form.value.rol ?? 'EMPLEADO'
    };

    this.authService.register(payload).subscribe({
      next: () => {
        this.router.navigate(['/cuenta/listar']);
      },

      error: (err) => {
        console.error('Error al crear la cuenta:', err);
      }

    });
  }

}
