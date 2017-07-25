/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
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
    relationshipTemplates: RelationshipTemplates[];
}

export class NodeTemplate extends WineryTemplate {
    requirements: Object = null;
    capabilities: Object = null;
    policies: Object = null;
    deploymentArtifacts: Object = null;
    minInstances: number;
    maxInstances: string;
}

export class RelationshipTemplates extends WineryTemplate {
    relationshipConstraing: Object = null;
    sourceElement: RelationshipElement;
    targetElement: RelationshipElement;
}

export class RelationshipElement {
    ref: NodeTemplate;
}
