import { Routes } from '@angular/router';

export const routes: Routes = [
    {
    path: '',
    pathMatch: 'full',
    redirectTo: 'personas'
  },
  {
    path: 'personas',
    loadComponent: () =>
      import('./pages/personas-list/personas-list')
        .then(m => m.PersonasList)
  },
  {
    path: 'personas/nueva',
    loadComponent: () =>
      import('./pages/personas-form/personas-form')
        .then(m => m.PersonasForm)
  },
  {
    path: 'personas/:id',
    loadComponent: () =>
      import('./pages/personas-form/personas-form')
        .then(m => m.PersonasForm)
  },
  { path: '**', redirectTo: 'personas' }
];
