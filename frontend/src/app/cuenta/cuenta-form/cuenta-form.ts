import { ActivatedRoute, Router } from '@angular/router';
import { CuentaService } from './../cuenta.service';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PersonasForm } from "../../persona/personas-form/personas-form";

@Component({
  selector: 'app-cuenta-form',
  standalone: true,
  imports: [FormsModule, PersonasForm],
  templateUrl: './cuenta-form.html',
  styleUrls: ['./cuenta-form.css']
})
export class CuentaForm {

service = inject(CuentaService);
route = inject(ActivatedRoute)
router = inject(Router);

isEdit = false;
id = 0;

cuenta = signal({
  nombreUsuario: '',
  password: '',
  rol: 'EMPLEADO',
  alta: true,
  persona: {
    id: 0,
    dni: '',
    nombre: '',
    apellido: '',
    email: '' as string | undefined,
    telefono: '' as string | undefined
  }
});

ngOnInit() {
  this.id = Number(this.route.snapshot.paramMap.get('id'));
  this.isEdit = !!this.id;
  if (this.isEdit) {
  this.service.getById(this.id).subscribe(c => {
    this.cuenta.set({
      ...c,
      persona: {
        id: c.persona?.id ?? 0,
        dni: c.persona?.dni ?? '',
        nombre: c.persona?.nombre ?? '',
        apellido: c.persona?.apellido ?? '',
        email: c.persona?.email ?? '',
        telefono: c.persona?.telefono ?? ''
      }
    });
  });
}
}

updateNombreUsuario(value: string) {
  this.cuenta.update(c => ({ ...c, nombreUsuario: value }));
}
updatepassword(value: string) {
  this.cuenta.update(c => ({ ...c, password: value }));
}

updateRol(value: string) {
  this.cuenta.update(c => ({ ...c, rol: value }));
}

updateAlta(value: boolean) {
  this.cuenta.update(c => ({ ...c, alta: value }));
}



// Guardar la cuenta (nueva o editada)
save(): void {
  const data = this.cuenta();
  if (!data) return; // protección contra undefined

  if (this.isEdit && this.id) {
    this.service.update(this.id, data).subscribe({
      next: () => this.router.navigate(['/cuentas']),
      error: (err: any) => console.error('Error actualizando cuenta', err)
    });
  } else {
    this.service.create(data).subscribe({
      next: () => this.router.navigate(['/cuentas']),
      error: (err: any) => console.error('Error creando cuenta', err)
    });
  }
}


}
