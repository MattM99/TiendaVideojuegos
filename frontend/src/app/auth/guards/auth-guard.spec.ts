import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth-guard';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let routerSpy = { navigate: jasmine.createSpy('navigate') };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthGuard, { provide: Router, useValue: routerSpy }],
    });

    guard = TestBed.inject(AuthGuard);
  });

  it('should allow activation when loggedUser is set', () => {
    localStorage.setItem('loggedUser', 'true');
    expect(guard.canActivate()).toBeTrue();
    localStorage.removeItem('loggedUser');
  });

  it('should block activation when loggedUser is not set', () => {
    expect(guard.canActivate()).toBeFalse();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  });
});
