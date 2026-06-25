import { Routes } from '@angular/router';
import { BloqueoListComponent } from './bloqueo-list/bloqueo-list';
import { BloqueoFormComponent } from './bloqueo-form/bloqueo-form';
import { BloqueoHistoricoComponent } from './bloqueo-historico/bloqueo-historico';
import { BloqueoMenuComponent } from './bloqueo-menu/bloqueo-menu';

export const BLOQUEO_ROUTES: Routes = [
  {
      path: '',
      component: BloqueoMenuComponent,
  },
  {
      path: 'vigentes',
      component: BloqueoListComponent,
  },
  {
      path: 'historico',
      component: BloqueoHistoricoComponent,
  },
  {
      path: 'nuevo',
      component: BloqueoFormComponent,
  },
];