/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { Component, Input } from '@angular/core';

@Component({
    selector: 'winery-entity-container',
    templateUrl: 'entityContainer.component.html',
    inputs: [
        'componentId',
        'namespace',
        'resourceType'
    ]
})
export class EntityContainerComponent {
    @Input() componentId: string;
    @Input() namespace: string;
    @Input() resourceType: string;
}
