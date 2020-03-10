/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

import { Component, Input } from '@angular/core';
import { InstanceService } from '../../../instance.service';
import { YamlPropertyConstraint } from '../properties/yamlProperty';

@Component({
    selector: 'winery-yaml-constraints',
    templateUrl: 'yamlConstraints.component.html',
    styleUrls: [
        'yamlConstraints.component.css'
    ]
})
export class YamlConstraintsComponent {
    @Input() constraints: YamlPropertyConstraint[];

    constructor(public sharedData: InstanceService) {
    }
}
