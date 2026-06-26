import { Component, OnInit, computed, inject, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";

import { BloqueoModel } from "../bloqueo.model";
import { BloqueoService } from "../bloqueo.service";

@Component({
  selector: 'app-bloqueo-historico',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './bloqueo-historico.html',
  styleUrl: '../bloqueo-list/bloqueo-list.css',
})
export class BloqueoHistoricoComponent implements OnInit {

  private service = inject(BloqueoService);

  bloqueados = signal<BloqueoModel[]>([]);

  titulo = "Histórico de bloqueos";
  mostrarDesbanear = false;

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

  ngOnInit() {
    this.cargarBloqueados();
  }

  cargarBloqueados() {
      this.service
          .obtenerHistorico(
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
              }
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