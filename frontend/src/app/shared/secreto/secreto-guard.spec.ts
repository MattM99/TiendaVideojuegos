import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { secretoGuard } from './secreto-guard';

describe('secretoGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => secretoGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
