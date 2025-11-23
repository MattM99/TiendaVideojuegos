import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router
} from '@angular/router';
import { AuthService } from '../auth-service/auth';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    
    const rolesPermitidos = route.data['roles'] as string[] | undefined;

    
    if (!rolesPermitidos || rolesPermitidos.length === 0) {
      return true;
    }

    
    if (!this.auth.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }

  
    if (!this.auth.hasRole(rolesPermitidos)) {
      alert('Acceso denegado: no tenés permisos para acceder a esta sección.');
      this.router.navigate(['/home']);
      return false;
    }

    return true;
  }
}