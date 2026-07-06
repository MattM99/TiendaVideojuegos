import { Routes } from '@angular/router';
import { ReservaForm } from './reserva-form/reserva-form';
import { ReservaList } from './reserva-list/reserva-list';

export const RESERVA_ROUTES: Routes = [
  { path: 'inventario/:inventarioId', component: ReservaList},
  { path: 'nueva/:inventarioId', component: ReservaForm }
];