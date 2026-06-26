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

    personas = signal<PersonaModel[]>([]);

    hoy = new Date().toLocaleDateString('en-CA');

    form = this.fb.group({
        personaDni: ['', Validators.required],
        fechaInicio: ['', [Validators.required, noFechaFutura,fechaValida]],
        fechaFin: ['', [fechaValida]],
        motivo: ['', [ Validators.required, Validators.minLength(5), Validators.maxLength(255)]],
        },
        {
            validators: rangoFechasValidas
        }
    );

    ngOnInit() {
        this.personaService
            .getAll(0, 1000, 'apellido', 'asc')
            .pipe(
                map(response => response.content)
            )
            .subscribe({
                next: (lista) => this.personas.set(lista),
                error: (err) => console.error(err)
            });
        this.form.valueChanges.subscribe(() => {
            this.errorMessage = '';
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
