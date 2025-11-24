import { Routes } from '@angular/router';
import { CuentaListComponent } from './cuenta-list/cuenta-list';
import { CuentaForm } from './cuenta-form/cuenta-form';

export const CUENTA_ROUTES: Routes = [
  { path: '', component: CuentaListComponent },
  { path: 'nuevo', component: CuentaForm },
  { path: 'editar/:id', component: CuentaForm },
];
