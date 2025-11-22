import { Routes } from '@angular/router';
import { VIDEOJUEGO_ROUTES } from './videojuego/videojuego.routes';

export const routes: Routes = [
    {
        path: 'videojuegos',
        children: VIDEOJUEGO_ROUTES 
    },
    {
        path: '',
        redirectTo: 'videojuegos',
        pathMatch: 'full'
    }
];
