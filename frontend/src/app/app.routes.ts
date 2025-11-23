import { Routes } from '@angular/router';
import { Admin } from './cuenta/admin/admin';
import { AuthGuard } from './auth/guards/auth-guard';
import { Login } from './auth/login/login';
import { FrontPage } from './shared/front-page/front-page';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';
import { CUENTA_ROUTES } from './cuenta/cuenta.routes';
import { INVENTARIO_ITEM_ROUTES } from './inventario-item/inventario.route';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },
  { path: 'admin', component: Admin, canActivate: [AuthGuard] },
  { path: 'cuentas', canActivate: [AuthGuard], children: CUENTA_ROUTES},
  { path: 'videojuegos', canActivate: [AuthGuard], children: VIDEOJUEGO_ROUTES},
  { path: 'inventario', canActivate: [AuthGuard], children: INVENTARIO_ITEM_ROUTES },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },

];
