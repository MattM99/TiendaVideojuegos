import { Routes } from '@angular/router';
import { CuentaListComponent } from './cuenta-list/cuenta-list';
import { CuentaDetalle } from './cuenta-detalle/cuenta-detalle';
import { CuentaWizardComponent } from './cuenta-wizard/cuenta-wizard';

export const CUENTA_ROUTES: Routes = [
  { path: 'listar', component: CuentaListComponent },
  { path: 'nuevo', component: CuentaWizardComponent },
  { path: ':nickname', component: CuentaDetalle },

];
