"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
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
var core_1 = require("@angular/core");
var TopologyRendererActions = TopologyRendererActions_1 = (function () {
    function TopologyRendererActions() {
    }
    TopologyRendererActions.prototype.togglePolicies = function () {
        return { type: TopologyRendererActions_1.TOGGLE_POLICIES };
    };
    TopologyRendererActions.prototype.toggleTargetLocations = function () {
        return { type: TopologyRendererActions_1.TOGGLE_TARGET_LOCATIONS };
    };
    TopologyRendererActions.prototype.toggleProperties = function () {
        return { type: TopologyRendererActions_1.TOGGLE_PROPERTIES };
    };
    TopologyRendererActions.prototype.toggleRequirementsCapabilities = function () {
        return { type: TopologyRendererActions_1.TOGGLE_REQUIREMENTS_CAPABILITIES };
    };
    TopologyRendererActions.prototype.toggleDeploymentArtifacts = function () {
        return { type: TopologyRendererActions_1.TOGGLE_DEPLOYMENT_ARTIFACTS };
    };
    TopologyRendererActions.prototype.toggleIds = function () {
        return { type: TopologyRendererActions_1.TOGGLE_IDS };
    };
    TopologyRendererActions.prototype.toggleTypes = function () {
        return { type: TopologyRendererActions_1.TOGGLE_TYPES };
    };
    return TopologyRendererActions;
}());
TopologyRendererActions.TOGGLE_POLICIES = 'TOGGLE_POLICIES';
TopologyRendererActions.TOGGLE_TARGET_LOCATIONS = 'TOGGLE_TARGET_LOCATIONS';
TopologyRendererActions.TOGGLE_PROPERTIES = 'TOGGLE_PROPERTIES';
TopologyRendererActions.TOGGLE_REQUIREMENTS_CAPABILITIES = 'TOGGLE_REQUIREMENTS_CAPABILITIES';
TopologyRendererActions.TOGGLE_DEPLOYMENT_ARTIFACTS = 'TOGGLE_DEPLOYMENT_ARTIFACTS';
TopologyRendererActions.TOGGLE_IDS = 'TOGGLE_IDS';
TopologyRendererActions.TOGGLE_TYPES = 'TOGGLE_TYPES';
TopologyRendererActions = TopologyRendererActions_1 = __decorate([
    core_1.Injectable()
], TopologyRendererActions);
exports.TopologyRendererActions = TopologyRendererActions;
var TopologyRendererActions_1;
