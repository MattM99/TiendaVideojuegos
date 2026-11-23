import { CuentaModel } from './../../cuenta/cuenta.model';
import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { jwtDecode } from 'jwt-decode';
import { RegisterRequest } from '../register-request';
import { JwtPayload } from '../jwt-payload.model';
import { environment } from '../../../environments/environment';


@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private currentUserSignal = signal<CuentaModel | null>(null);
  private apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {
    const saved = localStorage.getItem('loggedUser');

    if (saved && this.tokenVigente()) {
      try {
        this.currentUserSignal.set(JSON.parse(saved));
        return;
      } catch {
        // dato corrupto: se limpia abajo
      }
    }
    this.logout();
  }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/auth/register`, request);
  }

  currentUser = this.currentUserSignal.asReadonly();

  login(user: string, pass: string) {
    return this.http.post(`${this.apiUrl}/auth/login`, {
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
    localStorage.removeItem('token');
  }

  getCurrentUser() {
    return this.http.get<CuentaModel>(
      `${this.apiUrl}/cuenta/yo`
    );
  }

  tokenVigente(): boolean {
    const token = localStorage.getItem('token');
    if (!token) return false;

    try {
      const { exp } = jwtDecode<JwtPayload>(token);
      return !!exp && exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  isLoggedIn(): boolean {
    return this.currentUserSignal() !== null && this.tokenVigente();
  }

  hasRole(roles: string[]): boolean {
    const user = this.currentUserSignal();
    if (!user) return false;

    return roles.includes(user.rol);
  }
}
