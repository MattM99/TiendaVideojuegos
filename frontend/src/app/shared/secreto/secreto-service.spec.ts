import { TestBed } from '@angular/core/testing';

import { SecretoService } from './secreto-service';

describe('SecretoService', () => {
  let service: SecretoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SecretoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
