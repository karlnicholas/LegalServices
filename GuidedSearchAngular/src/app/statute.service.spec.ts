import { TestBed } from '@angular/core/testing';

import { StatuteService } from './statute.service';

describe('StatuteService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: StatuteService = TestBed.get(StatuteService);
    expect(service).toBeTruthy();
  });
});
