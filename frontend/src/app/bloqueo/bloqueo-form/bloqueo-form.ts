import { Component, inject, signal } from "@angular/core";
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from "@angular/router";
import { BloqueoService } from "../bloqueo.service";
import { Persona } from "../../persona/persona";
import { BloqueoCreateRequest } from "../bloqueo-create-request";
import { CommonModule } from "@angular/common";
import { PersonaModel } from "../../persona/persona.model";
import { map } from "rxjs/operators";
import { fechaValida, noFechaFutura, noFechaPasada, rangoFechasValidas } from "../../shared/validators/date.validator/date.validator";

@Component({
  selector: 'app-bloqueo-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './bloqueo-form.html',
  styleUrl: './bloqueo-form.css'
})
export class BloqueoFormComponent{

    public errorMessage = '';

    private bloqueoService = inject(BloqueoService);

    private personaService = inject(Persona);

    router = inject(Router);

    private fb = inject(FormBuilder);

    personaEncontrada = signal<PersonaModel | null>(null);
    personaValida = signal<boolean | null>(null);
    mensajePersona = signal<string | null>(null);

    hoy = new Date().toLocaleDateString('en-CA');

    form = this.fb.group({
        personaDni: ['', [Validators.required, Validators.pattern(/^\d{7,8}$/)]],
        fechaInicio: ['', [Validators.required, noFechaFutura,fechaValida]],
        fechaFin: ['', [fechaValida]],
        motivo: ['', [ Validators.required, Validators.minLength(5), Validators.maxLength(255)]],
        },
        {
            validators: rangoFechasValidas
        }
    );

    ngOnInit() {
        this.form.valueChanges.subscribe(() => {
            this.errorMessage = '';
        });

        this.form.get('personaDni')?.valueChanges.subscribe(() => {

            this.personaEncontrada.set(null);
            this.personaValida.set(null);
            this.mensajePersona.set(null);


            const control = this.form.get('personaDni');

            if (control?.hasError('backend')) {

                const errors = { ...control.errors };

                delete errors['backend'];

                control.setErrors(
                    Object.keys(errors).length ? errors : null
                );
            }
        });
    }

    buscarPersona() {

        if (this.form.get('personaDni')?.invalid) {
            this.form.get('personaDni')?.markAsTouched();
            return;
        }

        const dni = this.form.value.personaDni;

        if (!dni) {
            return;
        }

        this.personaValida.set(null);
        this.personaEncontrada.set(null);

        this.bloqueoService.validarPersona(dni).subscribe({

            next: persona => {

                this.personaEncontrada.set(persona);
                this.personaValida.set(true);
                this.mensajePersona.set(null);

            },

            error: err => {

                this.personaEncontrada.set(null);
                this.personaValida.set(false);

                this.mensajePersona.set(
                    err.error?.message ?? 'No se encontró la persona.'
                );

            }

        });

    }

    guardar() {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        const request: BloqueoCreateRequest = {
            personaDni: this.form.value.personaDni!,
            fechaInicio: this.form.value.fechaInicio!,
            fechaFin: this.form.value.fechaFin || undefined,
            motivo: this.form.value.motivo!
        };

        if (this.personaValida() !== true) {
            return;
        }

        this.bloqueoService.crear(request).subscribe({
            next: () => this.router.navigate(['/bloqueos']),
            error: err => {this.errorMessage = this.mapError(err);}
        });
    }

    private mapError(err: any): string {
        if (err.status === 400) { return err.error?.message || 'Solicitud inválida.';}
        return err.error?.message || 'Error inesperado.';
    }

}
