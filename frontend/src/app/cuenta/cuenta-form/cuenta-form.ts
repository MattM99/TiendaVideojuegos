import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CuentaService } from '../cuenta.service';
import { PersonaService } from '../../persona/persona.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cuenta-form.html',
  styleUrls: ['./cuenta-form.css'],
})
export class CuentaForm {

  // Servicios y router
  service = inject(CuentaService);
  personaService = inject(PersonaService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  // Signals
  personas = this.personaService.personas; // array de personas
  cuenta = signal<{
    nombreUsuario: string;
    password: string;
    rol: string;
    alta: boolean;
    personaId: string | null | undefined;
  }>({
    nombreUsuario: '',
    password: '',
    rol: 'EMPLEADO',
    alta: true,
    personaId: undefined,
  });


  isEdit = false;
  id: string | null = null;

  showPassword = false;
  togglePassword() { this.showPassword = !this.showPassword; }

  ngOnInit() {
    // Cargar personas existentes
    this.personaService.cargarPersonas();

    // Filtrar personas que ya tienen cuenta
    this.service.getAll().subscribe(cuentas => {
      const personasConCuenta = cuentas
        .filter(c => c.id !== this.id)  // si estoy editando, permito la persona actual
        .map(c => c.personaId);

      const todas = this.personas();
      const disponibles = todas.filter(
        p => !personasConCuenta.includes(String(p.id))
      );

      this.personas.set(disponibles);
    });

    // Detectar si es edición
    this.id = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!this.id;

    // Si vengo de crear persona → setear personaId automáticamente
    this.route.queryParams.subscribe(params => {
      const nuevaPersonaId = params['personaId'];
      if (nuevaPersonaId) {
        this.cuenta.update(c => ({ ...c, personaId: nuevaPersonaId }));
      }
    });

    // Si estoy editando, cargar cuenta
    if (this.isEdit && this.id) {
      this.service.getById(this.id).subscribe((c) => {
        this.cuenta.set({
          nombreUsuario: c.nombreUsuario,
          password: c.password,
          rol: c.rol,
          alta: c.alta,
          personaId: c.personaId ?? null,
        });
      });
    }
  }



  // Métodos de actualización para signals
  updateNombreUsuario(v: string) { this.cuenta.update(c => ({ ...c, nombreUsuario: v })); }
  updatePassword(v: string)      { this.cuenta.update(c => ({ ...c, password: v })); }
  updateRol(v: string)           { this.cuenta.update(c => ({ ...c, rol: v })); }
  updateAlta(v: boolean)         { this.cuenta.update(c => ({ ...c, alta: v })); }
  updatePersonaId(v: string | null) { this.cuenta.update(c => ({ ...c, personaId: v })); }

  crearNuevaPersona() {
    this.router.navigate(['/personas/nuevo'], {
      queryParams: { crearCuenta: true }
    });
  }

  // Guardar cuenta
  save() {
    const raw = this.cuenta();

    // 1) VALIDAR PERSONA
    if (raw.personaId === undefined) {
      alert('Tenés que seleccionar una persona o elegir crear una nueva.');
      return;
    }

    if (raw.personaId === null) {
      this.router.navigate(
        ['/personas/nuevo'],
        { queryParams: { crearCuenta: true } }
      );
      return;
    }

    // 2) VALIDAR USERNAME REPETIDO
    this.service.buscarPorUsuario(raw.nombreUsuario).subscribe({
      next: (encontrados) => {

        // Caso edición → si es el mismo usuario, permitimos
        if (this.isEdit &&
            encontrados.length === 1 &&
            encontrados[0].id === this.id) {
          this.guardarFinal(raw);
          return;
        }

        // Si existe otro usuario → error
        if (encontrados.length > 0) {
          alert('Ese nombre de usuario ya existe. Elegí otro.');
          return;
        }

        // Si no existe → continuar
        this.guardarFinal(raw);
      },
      error: err => {
        console.error('Error buscando usuario', err);
        alert('Error validando usuario.');
      }
    });
  }

    private guardarFinal(raw: ReturnType<typeof this.cuenta>) {
    const data = {
      personaId: raw.personaId!,
      nombreUsuario: raw.nombreUsuario,
      password: raw.password,
      rol: raw.rol,
      alta: raw.alta,
    };

    if (this.isEdit) {
      this.service.update(String(this.id), { id: String(this.id), ...data })
        .subscribe({
          next: () => this.router.navigate(['/cuentas']),
          error: err => console.error('Error actualizando cuenta', err),
        });

    } else {
      this.service.create(data).subscribe({
        next: () => this.router.navigate(['/cuentas']),
        error: err => console.error('Error creando cuenta', err),
      });
    }
  }

}



