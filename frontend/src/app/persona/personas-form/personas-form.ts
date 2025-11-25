import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { PersonaService } from '../persona.service';
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
  private personaService = inject(PersonaService);

  titulo = 'Nueva persona';
  personaId?: string;

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

    // Si la ruta es /personas/nuevo → MODO CREAR
    if (idParam === 'nuevo') {
      this.titulo = 'Nueva persona';
      this.personaId = undefined;
      return;
    }

    // Si existe id → MODO EDITAR
    if (idParam) {
      this.titulo = 'Editar persona';
      this.personaId = idParam;

      this.personaService.obtenerPersona(idParam).subscribe({
        next: persona => this.form.patchValue(persona),
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
          next: (personaCreada) => this.redirigirDespues(personaCreada.id),
          error: err => console.error('Error actualizando persona', err)
        });

    } else {
      this.personaService.crearPersona(value).subscribe({
        next: () => this.redirigirDespues(),
        error: err => console.error('Error creando persona', err)
      });
    }
  }

  private redirigirDespues(idPersona?: string) {
    if (this.crearCuenta) {
      this.router.navigate(['/cuentas/nuevo'], {
        queryParams: { personaId: idPersona }
      });
    } else {
      this.router.navigate(['/personas']);
    }
  }


  cancelar(): void {
    this.router.navigate(['/personas']);
  }
}
