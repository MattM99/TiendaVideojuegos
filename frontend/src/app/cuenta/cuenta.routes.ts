import { Routes } from '@angular/router';
import { CuentaListComponent } from './cuenta-list/cuenta-list';
import { CuentaDetalle } from './cuenta-detalle/cuenta-detalle';

export const CUENTA_ROUTES: Routes = [
  { path: 'listar', component: CuentaListComponent },
  { path: ':nickname', component: CuentaDetalle },

];
