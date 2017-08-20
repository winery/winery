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
import { Action } from 'redux';
import { TopologyRendererActions } from '../actions/topologyRenderer.actions';

export interface TopologyRendererState {
  buttonsState: {
    targetLocationsButton?: boolean;
    policiesButton?: boolean;
    requirementsCapabilitiesButton?: boolean;
    deploymentArtifactsButton?: boolean;
    propertiesButton?: boolean;
    typesButton?: boolean;
    idsButton?: boolean;
    layoutButton?: boolean;
    alignHButton?: boolean;
    alignVButton?: boolean
  };
}

export const INITIAL_TOPOLOGY_RENDERER_STATE: TopologyRendererState = {
  buttonsState: {
    targetLocationsButton: false,
    policiesButton: false,
    requirementsCapabilitiesButton: false,
    deploymentArtifactsButton: false,
    propertiesButton: false,
    typesButton: true,
    idsButton: true,
    layoutButton: false,
    alignHButton: false,
    alignVButton: false
  }
};

export const TopologyRendererReducer =
  function (lastState: TopologyRendererState = INITIAL_TOPOLOGY_RENDERER_STATE, action: Action): TopologyRendererState {
    switch (action.type) {
      case TopologyRendererActions.TOGGLE_POLICIES:
        // console.log({...lastState, buttonsState: { ...lastState.buttonsState, policiesButton: !lastState.buttonsState.policiesButton}});
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            policiesButton: !lastState.buttonsState.policiesButton}
        };
      case TopologyRendererActions.TOGGLE_TARGET_LOCATIONS:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            targetLocationsButton: !lastState.buttonsState.targetLocationsButton}
        };
      case TopologyRendererActions.TOGGLE_PROPERTIES:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            propertiesButton: !lastState.buttonsState.propertiesButton}
        };
      case TopologyRendererActions.TOGGLE_REQUIREMENTS_CAPABILITIES:
        return {
          ...lastState,
          buttonsState: {
            ...lastState.buttonsState,
            requirementsCapabilitiesButton: !lastState.buttonsState.requirementsCapabilitiesButton
          }
        };
      case TopologyRendererActions.TOGGLE_DEPLOYMENT_ARTIFACTS:
        return {
          ...lastState,
          buttonsState: {
            ...lastState.buttonsState,
            deploymentArtifactsButton: !lastState.buttonsState.deploymentArtifactsButton
          }
        };
      case TopologyRendererActions.TOGGLE_IDS:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            idsButton: !lastState.buttonsState.idsButton}};
      case TopologyRendererActions.TOGGLE_TYPES:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            typesButton: !lastState.buttonsState.typesButton}
        };
      case TopologyRendererActions.EXECUTE_LAYOUT:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            layoutButton: !lastState.buttonsState.layoutButton}
        };
      case TopologyRendererActions.EXECUTE_ALIGN_H:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            alignHButton: !lastState.buttonsState.alignHButton}
        };
      case TopologyRendererActions.EXECUTE_ALIGN_V:
        return {
          ...lastState,
          buttonsState: {...lastState.buttonsState,
            alignVButton: !lastState.buttonsState.alignVButton}
        };
    }
    return lastState;
  };
