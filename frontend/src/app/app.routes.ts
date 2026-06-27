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
import { NotFoundComponent } from './shared/not-found/not-found';
import { Dashboard } from './reportes/dashboard/dashboard';
import { Secreto } from './shared/secreto/secreto';
import { secretoGuard } from './shared/secreto/secreto-guard';

export const routes: Routes = [
  { path: 'login', component: Login, canActivate: [LoginGuard] },

  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },

  { path: '404', component: NotFoundComponent },

  {
    path: 'cuenta',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['FOUNDER', 'ADMINISTRADOR'] },
    children: CUENTA_ROUTES,
  },

  {
    path: 'personas',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['FOUNDER', 'ADMINISTRADOR', 'EMPLEADO'] },
    children: PERSONA_ROUTES,
  },

  {
    path: 'videojuegos',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['FOUNDER', 'ADMINISTRADOR', 'EMPLEADO'] },
    children: VIDEOJUEGO_ROUTES,
  },

  {
    path: 'inventario',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['FOUNDER', 'ADMINISTRADOR'] },
    children: INVENTARIO_ITEM_ROUTES,
  },

  {
    path: 'alquileres',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['FOUNDER', 'ADMINISTRADOR', 'EMPLEADO'] },
    children: ALQUILER_ROUTES,
  },

  {
    path: 'reportes',
    component: Dashboard,
   // canActivate: [AuthGuard, RoleGuard],
    //data: { roles: ['FOUNDER', 'ADMINISTRADOR'] },
  },

  {path: 'secreto', component: Secreto, canActivate: [secretoGuard] },



  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/404' },
];
