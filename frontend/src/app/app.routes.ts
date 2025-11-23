import { Routes, CanActivate } from '@angular/router';
import { Admin } from './cuenta/admin/admin';
import { AuthGuard } from './auth/guards/auth-guard';
import { Login } from './auth/login/login';
import { FrontPage } from './shared/front-page/front-page';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';
import { CUENTA_ROUTES } from './cuenta/cuenta.routes';
import { PERSONA_ROUTES } from './persona/persona.routes';
import { INVENTARIO_ITEM_ROUTES } from './inventario-item/inventario.route';
import { RoleGuard } from './auth/guards/role-guard';
import { LoginGuard } from './auth/guards/login-guard';

export const routes: Routes = [
 
   { path: 'login', component: Login, canActivate: [LoginGuard] },

  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },

  {
    path: 'admin',
    component: Admin,
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: 'cuentas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    children: CUENTA_ROUTES
  },

  {
    path: 'personas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'GERENTE', 'EMPLEADO'] },
    children: PERSONA_ROUTES
  },

  {
    path: 'videojuegos',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'EMPLEADO'] },
    children: VIDEOJUEGO_ROUTES
  },

  {
    path: 'inventario',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] }, 
    children: INVENTARIO_ITEM_ROUTES
  },

  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/home' },
];

