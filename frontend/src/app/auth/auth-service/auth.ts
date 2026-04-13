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

  login(user: string, pass: string) {
    return this.http.post('http://localhost:8080/api/auth/login', {
      username: user,
      password: pass
    })
  }

  setUser(user: CuentaModel) {
    this.currentUserSignal.set(user);
    localStorage.setItem('loggedUser', JSON.stringify(user));
  }

  logout() {
    this.currentUserSignal.set(null);
    localStorage.removeItem('loggedUser');
  }

  /*getCurrentUser() {
    return this.currentUserSignal();
  }*/

  getCurrentUser(nickname: string) {
    return this.http.get<CuentaModel>(
      `http://localhost:8080/api/cuenta/${nickname}`
    );
  }

  isLoggedIn(): boolean {
    return this.currentUserSignal() !== null;
  }

  hasRole(roles: string[]): boolean {
    const user = this.currentUserSignal();
    if (!user) return false;

    return roles.includes(user.rol);
  }
}
