import { Routes } from '@angular/router';
import { AlquilerList } from './alquiler-list/alquiler-list';
import { AlquilerForm } from './alquiler-form/alquiler-form';

export const ALQUILER_ROUTES: Routes = [
  { path: '', component: AlquilerList },
  { path: 'nuevo', component: AlquilerForm },
  { path: ':id', component: AlquilerForm },
];
