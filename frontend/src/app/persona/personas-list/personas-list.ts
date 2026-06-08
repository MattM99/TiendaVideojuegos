import { PersonaModel } from './../persona.model';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../auth/auth-service/auth';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Persona } from '../persona';

@Component({
  selector: 'app-persona-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './personas-list.html',
  styleUrls: ['./personas-list.css'],
})
export class PersonaListComponent implements OnInit {
  usuario = computed(() => this.auth.currentUser());
  page = signal(0);
  size = signal(5);
  totalPages = signal(0);
  totalElements = signal(0);
  sortBy = signal('apellido');
  direction = signal('desc');
  busqueda = signal('');

  private service = inject(Persona);
  personas = signal<PersonaModel[]>([]);

  personasFiltradas = computed(() => {

    let lista = this.personas();

  // ocultar founder
  lista = lista.filter(
    persona => persona.dni !== '0000000'
  );

    const textoBusqueda = this.busqueda().toLowerCase().trim();

    return lista.filter(persona =>
      persona.dni.toLowerCase().includes(textoBusqueda) ||
      persona.nombre.toLowerCase().includes(textoBusqueda) ||
      persona.apellido.toLowerCase().includes(textoBusqueda) ||
      (persona.email && persona.email.toLowerCase().includes(textoBusqueda)) ||
      (persona.telefono && persona.telefono.toLowerCase().includes(textoBusqueda))
    );

  });

  constructor(private http: HttpClient, private auth: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.loadPersonas();
  }

  loadPersonas() {
    this.service.cargarPersonas(
      this.page(),
      this.size(),
      this.sortBy(),
      this.direction()
    ).subscribe((response) => {
      this.personas.set(response.content);
      this.totalPages.set(response.totalPages);
      this.totalElements.set(response.totalElements);
    });
  }

  crearPersona() {
    this.router.navigate(['/personas/nueva'], { queryParams: { crearCuenta: true } });
  }


  ///PAGINACION, ORDENAMIENTO Y FILTRADO
  nextPage() {
    if (this.page() < this.totalPages() - 1) {
      this.page.update(p => p + 1);
      this.loadPersonas();
    }
  }

  previousPage() {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadPersonas();
    }
  }

  changeSize(event: Event) {

    const value = Number(
      (event.target as HTMLSelectElement).value
    );

    this.size.set(value);

    this.page.set(0);

    this.loadPersonas();
  }

  changeSort(event: Event) {

    const value = (
      event.target as HTMLSelectElement
    ).value;

    this.sortBy.set(value);

    this.page.set(0);

    this.loadPersonas();
  }

  changeDirection(event: Event) {

    const value = (
      event.target as HTMLSelectElement
    ).value;

    this.direction.set(value);

    this.page.set(0);

    this.loadPersonas();
  }
}
