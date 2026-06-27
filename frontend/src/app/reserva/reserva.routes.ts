import { Routes } from '@angular/router';
import { ReservaForm } from './reserva-form/reserva-form';
import { ReservaInventarioList } from './reserva-inventario-list/reserva-inventario-list';
import { ReservaList } from './reserva-list/reserva-list';

export const RESERVA_ROUTES: Routes = [
  { path: '', component: ReservaInventarioList },
  { path: 'inventario/:inventarioId', component: ReservaList},
  { path: 'nueva/:inventarioId', component: ReservaForm }
];