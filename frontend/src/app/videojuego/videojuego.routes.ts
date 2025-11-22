import { Routes } from '@angular/router';
import { VideojuegoListComponent } from './videojuego-list.component/videojuego-list.component';
import { VideojuegoFormComponent } from './videojuego-form.component/videojuego-form.component';
import { VideojuegoDetailComponent } from './videojuego-detail.component/videojuego-detail.component';

export const VIDEOJUEGO_ROUTES: Routes = [
  { path: '', component: VideojuegoListComponent },          // /videojuegos
  { path: 'new', component: VideojuegoFormComponent },       // /videojuegos/new
  { path: ':id', component: VideojuegoDetailComponent },      // /videojuegos/123
  { path: 'edit/:id', component: VideojuegoFormComponent }    // /videojuegos/edit/123
];
