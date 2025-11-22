import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login {
  nombreUsuario: string = '';
  contrasena: string = '';

  constructor(private auth: AuthService, private router: Router) {}

  login() {
  this.auth.login(this.nombreUsuario, this.contrasena).subscribe(cuentas => {
    if (cuentas.length === 1) {
      this.auth.setUser(cuentas[0]);
      this.router.navigate(['/home']);
    } else {
      alert('Usuario o contraseña incorrectos');
    }
  });
}

}
