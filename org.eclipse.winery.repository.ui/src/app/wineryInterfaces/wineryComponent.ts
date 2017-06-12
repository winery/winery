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
export class WineryComponent {
    documentation: Array<any> = null;
    any: Array<any> = null;
    otherAttributes: Object = null;
    name = '';

    constructor(name = '') {
        this.name = name;
    }
}

export class WineryTemplateOrImplementationComponent extends WineryComponent {
    properties: any;
    propertyConstraints: any;
    id: string;
    type?: string;
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

