import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service/auth';
import { CuentaModel } from '../../cuenta/cuenta.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  nombreUsuario: string = '';
  contrasena: string = '';
  showPassword = false;

  constructor(private auth: AuthService, private router: Router) { }

  login() {
    this.auth.login(this.nombreUsuario, this.contrasena).subscribe({
      next: (response: any) => {
        localStorage.setItem('token', response.token);

        this.auth.getCurrentUser().subscribe({
          next: (cuenta: CuentaModel) => {
            this.auth.setUser(cuenta);
            this.router.navigate(['/home']);
          },
          error: (err) => {
            localStorage.removeItem('token');
            alert('No se pudieron obtener los datos de la cuenta.');
          }
        });
      },
      error: (err) => {
        console.error('Error en login', err);
        alert('Usuario o contraseña incorrectas');
      },
    });
  }
}
