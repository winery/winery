/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/
import { PlansApiData } from '../instance/serviceTemplates/plans/plansApiData';

export class WineryComponent {
    documentation: Array<any> = null;
    any: Array<any> = null;
    otherAttributes: Object = null;
    name = '';

    constructor(name = '') {
        this.name = name;
    }
}

export class WineryTemplate extends WineryComponent {
    id: string;
    type?: string;
    properties: Object = null;
    propertyConstraints: Object = null;

    setValuesFromPlan(plan: PlansApiData) {
        this.name = plan.name;
        this.id = plan.id;
    }
}

export class WineryTemplateOrImplementationComponent extends WineryTemplate {
    nodeType?: string;
    relationshipType?: string;
}

export class WineryInstance extends WineryComponent {
    types: any;
    id: string;
    targetNamespace: string;
    import: any;
    serviceTemplateOrNodeTypeOrNodeTypeImplementation: Array<WineryTemplateOrImplementationComponent>;
}

export class ArtifactApiData extends WineryComponent {
    interfaceName: string;
    operationName: string;
    artifactType: string;
    artifactRef: string;
    id: string;
    artifactTypeLocalName: string;
    artifactRefLocalName: string;
    anyText: string;
}

export class WineryTopologyTemplate extends WineryComponent {
    nodeTemplates: NodeTemplate[];
    relationshipTemplates: RelationshipTemplate[];
}

export class NodeTemplate extends WineryTemplate {
    requirements: Object = null;
    capabilities: Object = null;
    policies: Object = null;
    deploymentArtifacts: Object = null;
    minInstances: number;
    maxInstances: string;
}

export class RelationshipTemplate extends WineryTemplate {
    relationshipConstraing: Object = null;
    sourceElement: RelationshipElement;
    targetElement: RelationshipElement;
}

export class RelationshipElement {
    ref: NodeTemplate;
}
