import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Persona } from '../persona';
import { PersonaModel } from '../persona.model';

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
  personaId?: number;

  form = this.fb.group({
    nombre: ['', Validators.required],
    apellido: ['', Validators.required],
    dni: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    telefono: ['']
  });

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
    this.crearCuenta = params['crearCuenta'] === 'true';
  });

    const idParam = this.route.snapshot.paramMap.get('id');

    if (idParam) {
      this.titulo = 'Editar persona';
      this.personaId = Number(idParam);

      this.personaService.obtenerPersona(this.personaId).subscribe({
        next: (persona: PersonaModel) => {
          this.form.patchValue(persona);
        },
        error: err => console.error('Error obteniendo persona', err)
      });
    }
  }

  onSubmit(): void {
  // 1️⃣ Validar el formulario
  if (this.form.invalid) {
    this.form.markAllAsTouched();
    return;
  }

  // 2️⃣ Obtener los datos del formulario (sin el id)
  const value = this.form.value as Omit<PersonaModel, 'id'>;

  if (this.personaId) {
    // 3️⃣ Editar persona existente
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
    // 4️⃣ Crear nueva persona
    this.personaService.crearPersona(value).subscribe({
      next: () => this.redirigirDespues(),
      error: err => console.error('Error creando persona', err)
    });
  }
}


  private redirigirDespues() {
  if (this.crearCuenta) {
    this.router.navigate(['/cuentas/nuevo']); // ir a creación de cuenta
  } else {
    this.router.navigate(['/personas']); // ir a lista de clientes
  }
}

  cancelar(): void {
    this.router.navigate(['/personas']);
  }
}
