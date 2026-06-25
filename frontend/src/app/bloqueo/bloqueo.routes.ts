import { Routes } from '@angular/router';
import { BloqueoListComponent } from './bloqueo-list/bloqueo-list';
import { BloqueoFormComponent } from './bloqueo-form/bloqueo-form';
import { BloqueoHistoricoComponent } from './bloqueo-historico/bloqueo-historico';

export const BLOQUEO_ROUTES: Routes = [
  {
    path: '',
    component: BloqueoListComponent,
  },
  {
    path:'historico',
    component: BloqueoHistoricoComponent
  },
  {
    path: 'nuevo',
    component: BloqueoFormComponent,
  }
];