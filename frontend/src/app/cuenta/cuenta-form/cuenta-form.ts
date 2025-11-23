import { ActivatedRoute, Router } from '@angular/router';
import { CuentaService } from './../cuenta.service';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './cuenta-form.html',
  styleUrls: ['./cuenta-form.css'],
})
export class CuentaForm {
  service = inject(CuentaService);
  route = inject(ActivatedRoute);
  router = inject(Router);

  isEdit = false;
  id: string | null = null;

  cuenta = signal({
    nombreUsuario: '',
    password: '',
    rol: 'EMPLEADO',
    alta: true,
    personaId: '',
  });

  ngOnInit() {
    this.id = this.route.snapshot.paramMap.get('id');
    this.isEdit = !!this.id;

    if (this.isEdit && this.id) {
      this.service.getById(this.id).subscribe((c) => {
        this.cuenta.set({
          nombreUsuario: c.nombreUsuario,
          password: c.password,
          rol: c.rol,
          alta: c.alta,
          personaId: c.personaId,
        });
      });
    }
  }

  showPassword = false;

togglePassword() {
  this.showPassword = !this.showPassword;
}


  updateNombreUsuario(value: string) {
    this.cuenta.update((c) => ({ ...c, nombreUsuario: value }));
  }
  updatepassword(value: string) {
    this.cuenta.update((c) => ({ ...c, password: value }));
  }

  updateRol(value: string) {
    this.cuenta.update((c) => ({ ...c, rol: value }));
  }

  updateAlta(value: boolean) {
    this.cuenta.update((c) => ({ ...c, alta: value }));
  }

  updatePersonaId(value: string) {
  this.cuenta.update(c => ({ ...c, personaId: value }));
}


  // Guardar la cuenta (nueva o editada)
  save(): void {
    const raw = this.cuenta();
    if (!raw) return;

    const data = {
      personaId: raw.personaId,
      nombreUsuario: raw.nombreUsuario,
      password: raw.password,
      rol: raw.rol,
      alta: raw.alta,
    };

    if (this.isEdit) {
      this.service.update(String(this.id), { id: String(this.id), ...data }).subscribe({
        next: () => this.router.navigate(['/cuentas']),
        error: (err) => console.error('Error actualizando cuenta', err),
      });
    } else {
      this.service.create(data).subscribe({
        next: () => this.router.navigate(['/cuentas']),
        error: (err) => console.error('Error creando cuenta', err),
      });
    }
  }
}
