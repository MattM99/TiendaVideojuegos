import { Routes } from '@angular/router';
import { PersonaListComponent } from './personas-list/personas-list';
import { PersonasForm } from './personas-form/personas-form';

export const PERSONA_ROUTES: Routes = [
  { path: '', component: PersonaListComponent },
  { path: 'nueva', component: PersonasForm }, // primero la ruta fija
  { path: ':dni', component: PersonasForm }, // después la paramétrica
];
