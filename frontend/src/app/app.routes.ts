import { Routes, CanActivate } from '@angular/router';
import { Admin } from './cuenta/admin/admin';
import { AuthGuard } from './auth/guards/auth-guard';
import { Login } from './auth/login/login';
import { FrontPage } from './shared/front-page/front-page';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';
import { CUENTA_ROUTES } from './cuenta/cuenta.routes';
import { PERSONA_ROUTES } from './persona/persona.routes';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },
  { path: 'admin', component: Admin, canActivate: [AuthGuard] },
  { path: 'cuentas', canActivate: [AuthGuard], children: CUENTA_ROUTES },
  { path: 'videojuegos', canActivate: [AuthGuard], children: VIDEOJUEGO_ROUTES },
  { path: 'personas', canActivate: [AuthGuard], children: PERSONA_ROUTES },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
];
