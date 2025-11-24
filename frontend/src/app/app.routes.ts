import { Routes, CanActivate } from '@angular/router';
import { AuthGuard } from './auth/guards/auth-guard';
import { Login } from './auth/login/login';
import { FrontPage } from './shared/front-page/front-page';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';
import { CUENTA_ROUTES } from './cuenta/cuenta.routes';
import { PERSONA_ROUTES } from './persona/persona.routes';
import { INVENTARIO_ITEM_ROUTES } from './inventario-item/inventario.route';
import { ALQUILER_ROUTES } from './alquiler/alquiler.routes';
import { RoleGuard } from './auth/guards/role-guard';
import { LoginGuard } from './auth/guards/login-guard';
import { Wip } from './shared/wip/wip';

export const routes: Routes = [
  { path: 'login', component: Login, canActivate: [LoginGuard] },

  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },

  { path: 'wip', component: Wip },

  {
    path: 'cuentas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    children: CUENTA_ROUTES,
  },

  {
    path: 'personas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'GERENTE', 'EMPLEADO'] },
    children: PERSONA_ROUTES,
  },

  {
    path: 'videojuegos',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'EMPLEADO'] },
    children: VIDEOJUEGO_ROUTES,
  },

  {
    path: 'inventario',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    children: INVENTARIO_ITEM_ROUTES,
  },

  {
    path: 'alquileres',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN', 'EMPLEADO'] },
    children: ALQUILER_ROUTES,
  },

  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/wip' },
];
