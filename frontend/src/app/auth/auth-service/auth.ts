import { CuentaModel } from './../../cuenta/cuenta.model';
import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private currentUserSignal = signal<CuentaModel | null>(null);

  constructor(private http: HttpClient) {
    const saved = localStorage.getItem('loggedUser');
    if (saved) {
      this.currentUserSignal.set(JSON.parse(saved));
    }
  }

  currentUser = this.currentUserSignal.asReadonly();

  login(nombreUsuario: string, contraseña: string) {
    return this.http.get<CuentaModel[]>(
      `http://localhost:3000/cuentas?nombreUsuario=${nombreUsuario}&contraseña=${contraseña}`
    );
  }

  setUser(user: CuentaModel) {
    this.currentUserSignal.set(user);
    localStorage.setItem('loggedUser', JSON.stringify(user));
  }

  logout() {
    this.currentUserSignal.set(null);
    localStorage.removeItem('loggedUser');
  }

  getCurrentUser() {
    return this.currentUserSignal();
  }
}
