import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../auth/auth-service/auth';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = localStorage.getItem('token');
  const request = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      const esLogin = req.url.includes('/api/auth/login');

      if (error.status === 401 && !esLogin && auth.isLoggedIn()) {
        auth.logout();
        router.navigate(['/login']);
        alert('Tu sesión expiró. Volvé a iniciar sesión.');
      }

      return throwError(() => error);
    })
  );
};
