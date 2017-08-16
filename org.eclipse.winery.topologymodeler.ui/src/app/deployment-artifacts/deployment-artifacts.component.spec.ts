import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DeploymentArtifactsComponent } from './deployment-artifacts.component';

describe('DeploymentArtifactsComponent', () => {
  let component: DeploymentArtifactsComponent;
  let fixture: ComponentFixture<DeploymentArtifactsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DeploymentArtifactsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DeploymentArtifactsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
