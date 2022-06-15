/*******************************************************************************
 * Copyright (c) 2021-2022 Contributors to the Eclipse Foundation
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
import { Component, OnInit } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { ActivatedRoute } from '@angular/router';
import { ResearchObjectService } from './researchObject.service';

@Component({
    selector: 'winery-research-object',
    templateUrl: 'researchObject.component.html',
    providers: [ResearchObjectService]
})
export class ResearchObjectComponent implements OnInit {

    urlSegment: string;

    constructor(private service: ResearchObjectService,
                private iService: InstanceService,
                private route: ActivatedRoute) {
        this.route.url.subscribe(data => this.urlSegment = data[0].path);
    }

    ngOnInit() {
        this.service.setBaseUrl(this.iService.path + '/' + this.urlSegment + '/');
    }
}
