import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PaletteComponent } from './palette.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('PaletteComponent', () => {
  let component: PaletteComponent;
  let fixture: ComponentFixture<PaletteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PaletteComponent],
      imports: [BrowserAnimationsModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PaletteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
