/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import {Component, Input, OnInit} from '@angular/core';
import {QNameWithTypeApiData} from '../../model/qNameWithTypeApiData';
import {isNullOrUndefined} from 'util';

@Component({
    selector: 'winery-referenced-definitions',
    templateUrl: 'referencedDefinitions.component.html'
})

export class ReferencedDefinitionsComponent implements OnInit {

    @Input() title: string;
    @Input() referencedDefinitions: QNameWithTypeApiData[];
    updateReferencedDefinitions: QNameWithTypeApiData[];

    constructor() {
    }

    ngOnInit() {
    }

    selectedReference(item: QNameWithTypeApiData) {
        if (isNullOrUndefined(this.updateReferencedDefinitions)) {
            this.updateReferencedDefinitions = [];
        }

        const indexInList = this.updateReferencedDefinitions.indexOf(item);

        if (indexInList >= 0) {
            this.updateReferencedDefinitions.splice(indexInList, 1);
        } else {
            this.updateReferencedDefinitions.push(item);
        }
    }
}
