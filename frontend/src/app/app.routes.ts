import { Routes } from '@angular/router';
import { Admin } from './components/admin/admin';
import { AuthGuard } from './guards/auth-guard';
import { Login } from './auth/login/login';
import { FrontPage } from './components/front-page/front-page';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'home', component: FrontPage, canActivate: [AuthGuard] },
  { path: 'admin', component: Admin, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
  { path: 'videojuegos', children: VIDEOJUEGO_ROUTES},
];
