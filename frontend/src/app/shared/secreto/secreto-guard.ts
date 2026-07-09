import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { SecretoService } from './secreto-service';

export const secretoGuard: CanActivateFn = () => {
  const secreto = inject(SecretoService);
  const router = inject(Router);

    console.log('Desbloqueado:', secreto.desbloqueado());


  if (secreto.desbloqueado()) {
    return true;
  }

  return router.createUrlTree(['/']);
};
