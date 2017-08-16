import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TargetLocationsComponent } from './target-locations.component';

describe('TargetLocationsComponent', () => {
  let component: TargetLocationsComponent;
  let fixture: ComponentFixture<TargetLocationsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TargetLocationsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TargetLocationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
