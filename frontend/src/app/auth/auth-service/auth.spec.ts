import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth-guard';
import { AuthService } from '../auth-service/auth';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  const routerSpy = { navigate: jasmine.createSpy('navigate') };
  const authSpy = {
    isLoggedIn: jasmine.createSpy('isLoggedIn'),
    logout: jasmine.createSpy('logout'),
  };

  beforeEach(() => {
    routerSpy.navigate.calls.reset();
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: routerSpy },
        { provide: AuthService, useValue: authSpy },
      ],
    });
    guard = TestBed.inject(AuthGuard);
  });

  it('permite el acceso si hay sesión vigente', () => {
    authSpy.isLoggedIn.and.returnValue(true);
    expect(guard.canActivate()).toBeTrue();
  });

  it('bloquea y redirige al login si no hay sesión vigente', () => {
    authSpy.isLoggedIn.and.returnValue(false);
    expect(guard.canActivate()).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
