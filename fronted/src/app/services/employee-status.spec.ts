import { TestBed } from '@angular/core/testing';

import { EmployeeStatus } from './employee-status';

describe('EmployeeStatus', () => {
  let service: EmployeeStatus;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EmployeeStatus);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
