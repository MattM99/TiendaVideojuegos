import { Component, inject, signal } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { BloqueoService } from "../bloqueo.service";
import { Persona } from "../../persona/persona";
import { BloqueoCreateRequest } from "../bloqueo-create-request";
import { CommonModule } from "@angular/common";
import { PersonaModel } from "../../persona/persona.model";
import { map } from "rxjs/operators";

@Component({
  selector: 'app-bloqueo-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bloqueo-form.html',
  styleUrl: './bloqueo-form.css'
})
export class BloqueoFormComponent{

    private bloqueoService = inject(BloqueoService);

    private personaService = inject(Persona);

    router = inject(Router);

    personas = signal<PersonaModel[]>([]);

    bloqueo = signal<BloqueoCreateRequest>({
        personaDni: '',
        fechaInicio: '',
        fechaFin: null,
        motivo: ''
    });

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
    }

    actualizarPersona(dni: string) {
        this.bloqueo.update(b => ({
            ...b,
            personaDni: dni
        }));
    }

    actualizarFechaInicio(fecha: string) {

        this.bloqueo.update((b) => ({
            ...b,
            fechaInicio: fecha,
        }));

    }

    actualizarFechaFin(fecha: string) {

        this.bloqueo.update((b) => ({
            ...b,
            fechaFin: fecha,
        }));

    }

    actualizarMotivo(motivo: string) {

        this.bloqueo.update((b) => ({
            ...b,
            motivo: motivo,
        }));

    }

    guardar() {

        const request = this.bloqueo();

        console.log(request);

        if (!request.personaDni) {
            console.error("Debe seleccionar una persona");
            return;
        }

        this.bloqueoService.crear(request).subscribe({
            next: () => this.router.navigate(['/bloqueos']),
            error: err => console.error(err)
        });
    }

}
