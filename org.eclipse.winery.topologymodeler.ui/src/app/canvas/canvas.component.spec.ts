import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CanvasComponent } from './canvas.component';
import { NodeComponent } from '../node/node.component';
import { AccordionModule } from 'ngx-bootstrap';
import { JsPlumbService } from '../jsPlumbService';
import { JsonService } from '../jsonService/json.service';
import { MockJsonService } from '../jsonService/mock-json.service';

describe('CanvasComponent', () => {
  let component: CanvasComponent;
  let jsonService: JsonService;
  let fixture: ComponentFixture<CanvasComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CanvasComponent,
        NodeComponent],
      imports: [AccordionModule.forRoot()],
      providers: [JsPlumbService, {provide: JsonService, useClass: MockJsonService }]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CanvasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jsonService = TestBed.get(JsonService);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return true if array contains node with the given id', () => {
    component.ngOnInit();
    const trueResult = component.arrayContainsNode(['banana', 'apple', 'kiwi'], 'apple');
    expect(trueResult).toBe(true);
  });
});
