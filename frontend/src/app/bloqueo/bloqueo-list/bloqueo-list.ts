import { CommonModule } from "@angular/common";
import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { RouterModule } from "@angular/router";
import { BloqueoModel } from "../bloqueo.model";
import { BloqueoService } from "../bloqueo.service";
import { ErrorService } from "../../shared/error/error";

@Component({
  selector: 'app-bloqueo-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bloqueo-list.html',
  styleUrl: './bloqueo-list.css'
})
export class BloqueoListComponent implements OnInit{

    private service = inject(BloqueoService);

    private errorService = inject(ErrorService);

    bloqueados = signal<BloqueoModel[]>([]);

    page = signal(0);
    size = signal(5);
    totalPages = signal(0);
    totalElements = signal(0);
    sortBy = signal('fechaInicio');
    direction = signal('desc');
    busqueda = signal('');
    bloqueadosFiltrados = computed(() => {
        const texto = this.busqueda().toLowerCase().trim();
        return this.bloqueados().filter(b =>
            b.persona.nombre.toLowerCase().includes(texto)
            || b.persona.apellido.toLowerCase().includes(texto)
            || b.persona.dni.includes(texto)
            || b.motivo.toLowerCase().includes(texto)
        );
    });

    ngOnInit(){
        this.cargarBloqueados();
    }

    desbanear(dni: string) {
        if (!confirm("¿Desbanear a esta persona?"))
            return;

        this.service.desbanear(dni).subscribe({
            next: () => this.cargarBloqueados(),
            error: (err) => this.errorService.mostrar(err)
        });
    }

    cargarBloqueados() {
        this.service
            .obtenerVigentes(
                this.page(),
                this.size(),
                this.sortBy(),
                this.direction()
            )
            .subscribe({
                next: response => {
                    this.bloqueados.set(response.content);
                    this.totalPages.set(response.totalPages);
                    this.totalElements.set(response.totalElements);
                },
                error: err => this.errorService.mostrar(err)
            });
    }

    nextPage() {
        if (this.page() < this.totalPages() - 1) {
            this.page.update(p => p + 1);
            this.cargarBloqueados();
        }
    }

    previousPage() {
        if (this.page() > 0) {
            this.page.update(p => p - 1);
            this.cargarBloqueados();
        }
    }

    changeSize(event: Event) {
        this.size.set(
            Number(
                (event.target as HTMLSelectElement).value
            )
        );
        this.page.set(0);
        this.cargarBloqueados();
    }

    changeSort(event: Event) {
        this.sortBy.set(
            (event.target as HTMLSelectElement).value
        );
        this.page.set(0);
        this.cargarBloqueados();
    }

    changeDirection(event: Event) {
        this.direction.set(
            (event.target as HTMLSelectElement).value
        );
        this.page.set(0);
        this.cargarBloqueados();
    }

}
