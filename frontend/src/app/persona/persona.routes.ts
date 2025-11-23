import { Routes } from "@angular/router";
import { PersonasList } from "./personas-list/personas-list";
import { PersonasForm } from './personas-form/personas-form';


export const PERSONA_ROUTES: Routes = [
  { path: '', component: PersonasList },
  { path: 'nueva', component: PersonasForm },
  { path: ':id', component: PersonasForm },
];


