import { TestBed, inject } from '@angular/core/testing';

import { JsonService } from './json.service';

describe('JsonService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [JsonService]
    });
  });

  it('should ...', inject([JsonService], (service: JsonService) => {
    expect(service).toBeTruthy();
  }));
});
