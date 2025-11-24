import { Component, OnInit, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Alquiler } from '../alquiler';
import { AlquilerModel } from '../alquiler.model';
import { Persona } from '../../persona/persona';

@Component({
  selector: 'app-alquiler-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './alquiler-list.html',
  styleUrl: './alquiler-list.css',
})
export class AlquilerList implements OnInit {

  private alquilerService = inject(Alquiler);
  private router = inject(Router);
  private personaService = inject(Persona);

  alquileres = computed(() => this.alquilerService.alquileres());
  cargando = computed(() => this.alquilerService.cargando());

  ngOnInit(): void {
  this.personaService.cargarPersonas();   
  this.alquilerService.cargarAlquileres();
}

  nuevo() {
    this.router.navigate(['/alquileres/nuevo']);
  }

  editar(id: string | undefined) {
    if (!id) return;
    this.router.navigate(['/alquileres', id]);
  }

  eliminar(id: string | undefined) {
    if (!id) return;

    if (!confirm('¿Seguro que querés eliminar este alquiler?')) return;

    this.alquilerService.eliminarAlquiler(id).subscribe({
      next: () => this.alquilerService.cargarAlquileres(),
      error: err => console.error('Error eliminando alquiler', err),
    });
  }

  getNombrePersona(personaId: string): string {
    const persona = this.personaService.personas().find(p => p.id === personaId);
    return persona ? `${persona.nombre} ${persona.apellido}` : 'Desconocido';
  }
}