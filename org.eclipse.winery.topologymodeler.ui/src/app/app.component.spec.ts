import { TestBed, async } from '@angular/core/testing';

import { AppComponent } from './app.component';
import { TopologyRendererModule } from './topology-renderer/topology-renderer.module';
import { PaletteComponent } from './palette/palette.component';
import { JsonService } from './jsonService/json.service';
import { JsPlumbService } from './jsPlumbService';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        PaletteComponent
      ],
      imports: [TopologyRendererModule, BrowserAnimationsModule],
      providers: [JsonService, JsPlumbService]
    }).compileComponents();
  }));

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));
});
