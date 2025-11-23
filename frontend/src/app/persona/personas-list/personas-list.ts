import { Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, RouterModule } from '@angular/router';
import { Persona } from '../persona';

@Component({
  selector: 'app-personas-list',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterModule],
  templateUrl: './personas-list.html',
  styleUrl: './personas-list.css',
})
export class PersonasList implements OnInit {

  private personaService = inject(Persona);
  constructor(
    private router: Router
  ) {}


  personas = computed(() => this.personaService.personas());
  cargando = computed(() => this.personaService.cargando());

  ngOnInit(): void {
    this.personaService.cargarPersonas();
  }

  eliminar(id: number): void {
    if (!confirm('¿Seguro que querés eliminar esta persona?')) return;

    this.personaService.eliminarPersona(id).subscribe({
      next: () => this.personaService.cargarPersonas(),
      error: err => console.error('Error eliminando persona', err),
    });
  }

  crearCliente() {
    this.router.navigate(['/persona-form'], { queryParams: { crearCuenta: false } });
  }
}
