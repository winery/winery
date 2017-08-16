import { TestBed, inject } from '@angular/core/testing';

import { PaletteService } from './palette.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('PaletteService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PaletteService],
      imports: [BrowserAnimationsModule]
    });
  });

  it('should ...', inject([PaletteService], (service: PaletteService) => {
    expect(service).toBeTruthy();
  }));
});
