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
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const value = this.form.value as Omit<PersonaModel, 'id'>;

    if (this.personaId) {
      // Editar
      const personaActualizada: PersonaModel = {
        id: this.personaId,
        ...value
      };

      this.personaService.actualizarPersona(this.personaId, personaActualizada)
        .subscribe({
          next: () => this.router.navigate(['/personas']),
          error: err => console.error('Error actualizando persona', err)
        });

    } else {
      // Crear
      this.personaService.crearPersona(value).subscribe({
        next: () => this.router.navigate(['/personas']),
        error: err => console.error('Error creando persona', err)
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/personas']);
  }
}
