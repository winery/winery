/********************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
import { Component, Input, OnInit } from '@angular/core';
import { WineryService } from '../../services/winery.service';
import { Node } from '../../model/workflow/node';

@Component({
    selector: 'b4t-instance-type',
    templateUrl: 'instanceType.component.html',
})
export class WmInstanceTypeComponent implements OnInit {
    @Input() public node: Node;
    types: String[];
    selectedType: String = '';

    constructor(private wineryService: WineryService) {
        this.types = ['newInstance', 'selectInstance'];
    }

    ngOnInit(): void {
        this.selectedType = this.node.instanceType;
    }

    typeChanged(type: any) {
        this.node.instanceType = type.target.value;
        this.selectedType = this.node.instanceType;
    }

}

