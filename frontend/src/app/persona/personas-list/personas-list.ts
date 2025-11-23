import { Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterModule } from '@angular/router';
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
  private router = inject(Router);

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

<<<<<<< HEAD
  crearCliente() {
    this.router.navigate(['/persona-form'], { queryParams: { crearCuenta: false } });
=======
  editar(id: number): void {
    this.router.navigate(['/personas', id]);
>>>>>>> origin/nico-branch
  }
}
