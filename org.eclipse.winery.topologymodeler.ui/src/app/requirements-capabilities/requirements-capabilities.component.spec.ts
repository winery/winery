import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequirementsCapabilitiesComponent } from './requirements-capabilities.component';

describe('RequirementsCapabilitiesComponent', () => {
  let component: RequirementsCapabilitiesComponent;
  let fixture: ComponentFixture<RequirementsCapabilitiesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequirementsCapabilitiesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequirementsCapabilitiesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
