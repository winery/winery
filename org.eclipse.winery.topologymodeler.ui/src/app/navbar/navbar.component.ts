/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Josip Ledic - initial API and implementation
 */
import {Component, OnDestroy} from '@angular/core';
import {WineryAlertService} from '../winery-alert/winery-alert.service';
import {NgRedux} from '@angular-redux/store';
import {TopologyRendererActions} from '../redux/actions/topologyRenderer.actions';
import {ButtonsStateModel} from '../models/buttonsState.model';
import {IAppState} from '../redux/store/app.store';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnDestroy {

  /**
   * Boolean variables that hold the state {pressed vs. !pressed} of the navbar buttons.
   * @type {boolean}
   */
  navbarButtonsState: ButtonsStateModel;
  navBarButtonsStateSubscription;

  constructor(private alert: WineryAlertService,
              private ngRedux: NgRedux<IAppState>,
              private actions: TopologyRendererActions) {
    this.navBarButtonsStateSubscription = ngRedux.select(state => state.topologyRendererState)
      .subscribe(newButtonsState => this.setButtonsState(newButtonsState));
  }

  setButtonsState(newButtonsState: ButtonsStateModel): void {
    this.navbarButtonsState = newButtonsState;
  }

  getStyle(buttonPressed: boolean): string {
    if (buttonPressed) {
      return 'lightgrey';
    }
  }

  /**
   * This function is called whenever a navbar button is clicked.
   * It contains a separate case for each button.
   * It toggles the `pressed` state of a button and publishes the respective
   * button {id and boolean} to the subscribers of the Observable inside
   * SharedNodeNavbarService.
   * @param event -- The click event of a button.
   */
  toggleButton(event) {
    switch (event.target.id) {
      case 'targetLocations': {
        this.ngRedux.dispatch(this.actions.toggleTargetLocations());
        break;
      }
      case 'policies': {
        this.ngRedux.dispatch(this.actions.togglePolicies());
        break;
      }
      case 'requirementsCapabilities': {
        this.ngRedux.dispatch(this.actions.toggleRequirementsCapabilities());
        break;
      }
      case 'deploymentArtifacts': {
        this.ngRedux.dispatch(this.actions.toggleDeploymentArtifacts());
        break;
      }
      case 'properties': {
        this.ngRedux.dispatch(this.actions.toggleProperties());
        break;
      }
      case 'types': {
        this.ngRedux.dispatch(this.actions.toggleTypes());
        break;
      }
      case 'ids': {
        this.ngRedux.dispatch(this.actions.toggleIds());
        break;
      }
      case 'layout': {
        this.ngRedux.dispatch(this.actions.executeLayout());
        break;
      }
      case 'alignh': {
        this.ngRedux.dispatch(this.actions.executeAlignH());
        break;
      }
      case 'alignv': {
        this.ngRedux.dispatch(this.actions.executeAlignV());
      }
    }
  }

  public showSaveAlert(): void {
    this.alert.success('Successfully saved!');
  }

  ngOnDestroy() {
    this.navBarButtonsStateSubscription.unsubscribe();
  }
}
