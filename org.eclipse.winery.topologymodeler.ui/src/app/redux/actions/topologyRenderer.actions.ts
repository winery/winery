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
import { Injectable } from '@angular/core';
import { Action } from 'redux';

@Injectable()
export class TopologyRendererActions {
  static TOGGLE_POLICIES = 'TOGGLE_POLICIES';
  static TOGGLE_TARGET_LOCATIONS = 'TOGGLE_TARGET_LOCATIONS';
  static TOGGLE_PROPERTIES = 'TOGGLE_PROPERTIES';
  static TOGGLE_REQUIREMENTS_CAPABILITIES = 'TOGGLE_REQUIREMENTS_CAPABILITIES';
  static TOGGLE_DEPLOYMENT_ARTIFACTS = 'TOGGLE_DEPLOYMENT_ARTIFACTS';
  static TOGGLE_IDS = 'TOGGLE_IDS';
  static TOGGLE_TYPES = 'TOGGLE_TYPES';

  togglePolicies(): Action {
    return { type: TopologyRendererActions.TOGGLE_POLICIES };
  }
  toggleTargetLocations(): Action {
    return { type: TopologyRendererActions.TOGGLE_TARGET_LOCATIONS };
  }
  toggleProperties(): Action {
    return { type: TopologyRendererActions.TOGGLE_PROPERTIES };
  }
  toggleRequirementsCapabilities(): Action {
    return { type: TopologyRendererActions.TOGGLE_REQUIREMENTS_CAPABILITIES };
  }
  toggleDeploymentArtifacts(): Action {
    return { type: TopologyRendererActions.TOGGLE_DEPLOYMENT_ARTIFACTS };
  }
  toggleIds(): Action {
    return { type: TopologyRendererActions.TOGGLE_IDS };
  }
  toggleTypes(): Action {
    return { type: TopologyRendererActions.TOGGLE_TYPES };
  }
}
