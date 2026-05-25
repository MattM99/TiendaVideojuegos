import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink, Router } from '@angular/router';
import { CuentaService } from '../cuenta.service';
import { CuentaModel } from '../cuenta.model';
import { AuthService } from '../../auth/auth-service/auth';

@Component({
  selector: 'app-cuenta-detalle',
  imports: [CommonModule, RouterLink],
  templateUrl: './cuenta-detalle.html',
  styleUrl: './cuenta-detalle.css',
  standalone: true,
})
export class CuentaDetalle implements OnInit {
  private route = inject(ActivatedRoute);
  private cuentaService = inject(CuentaService);
  private router = inject(Router);
  private authService = inject(AuthService);

usuario = this.authService.currentUser;

  cuenta = signal<CuentaModel | null>(null);

  ngOnInit(): void {

    const nickname = this.route.snapshot.paramMap.get('nickname');

    this.cuentaService.getByNickname(nickname!).subscribe({
      next: (data) => {
        this.cuenta.set(data);
      }
    });

    this.recargarCuenta();

  }

  cambiarRol(nuevoRol: string) {

    const cuenta = this.cuenta();

    if (!cuenta) return;

    if (cuenta.rol === nuevoRol) {

      alert('La cuenta ya tiene ese rol');

      return;
    }

    this.cuentaService.cambiarRol(
      cuenta.nickname,
      nuevoRol
    ).subscribe({

      next: () => {

        alert('Rol actualizado');

        this.recargarCuenta();
      },

      error: (err) => {

        alert(err.error);

        console.error(err);

      }

    });

  }

  cambiarPassword() {

    const cuenta = this.cuenta();

    if (!cuenta) return;

    const nuevaContrasena = prompt(
      'Nueva contraseña:'
    );

    if (!nuevaContrasena) return;

    if (nuevaContrasena.trim().length < 4) {

      alert(
        'La contraseña debe tener al menos 4 caracteres'
      );

      return;
    }

    this.cuentaService.cambiarContrasena(
      cuenta.nickname,
      nuevaContrasena
    ).subscribe({

      next: () => alert(
        'Contraseña actualizada'
      ),

      error: (err) => console.error(err),

    });

  }
  darDeBaja() {

    const cuenta = this.cuenta();

    if (!cuenta) return;

    if (!confirm(
      `Dar de baja a ${cuenta.nickname}?`
    )) {
      return;
    }

    this.cuentaService.darDeBaja(
      cuenta.nickname
    ).subscribe({

      next: () => {

        alert('Cuenta dada de baja');

        this.recargarCuenta();
      },

      error: (err) => {

        alert(err.error);

        console.error(err);

      },

    });

  }
  darDeAlta() {

    const cuenta = this.cuenta();

    if (!cuenta) return;

    this.cuentaService.darDeAlta(
      cuenta.nickname
    ).subscribe({

      next: () => {

        alert('Cuenta reactivada');

        this.recargarCuenta();
      },

      error: (err) => console.error(err),

    });

  }
  recargarCuenta() {

    const nickname =
      this.route.snapshot.paramMap.get('nickname');

    this.cuentaService.getByNickname(nickname!).subscribe({

      next: (data) => {
        this.cuenta.set(data);
      }

    });

  }
}
