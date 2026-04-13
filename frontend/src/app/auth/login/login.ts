import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service/auth';
import { jwtDecode } from 'jwt-decode';
import { CuentaService } from '../../cuenta/cuenta.service';
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

  constructor(private auth: AuthService, private CuentaService: CuentaService, private router: Router) {}

  login() {
    this.auth.login(this.nombreUsuario, this.contrasena).subscribe({
      next: (response: any) => {
        const token = response.token;
        localStorage.setItem('token', response.token); //guardar token en localStorage

        const decoded: any = jwtDecode(token); //decodificar token para obtener información del usuario
        const nickname = decoded.sub;

        this.CuentaService.getByNickname(nickname).subscribe((cuenta: CuentaModel) => {

        this.auth.setUser(cuenta);

        this.router.navigate(['/home']);
      });
      },
      error: (err) => {
        console.error('Error en login', err);
        alert('Usuario o contraseña incorrectas');
      },
    });
  }
}
