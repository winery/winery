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
import {Component, OnInit} from '@angular/core';
import {SelfServicePortalService} from './selfServicePortal.service';
import {ActivatedRoute} from '@angular/router';
import {InstanceService} from '../../instance.service';

@Component({
    selector: 'winery-self-service-portal',
    templateUrl: 'selfServicePortal.component.html',
    providers: [SelfServicePortalService]
})
export class SelfServicePortalComponent implements OnInit {

    urlSegment: any;

    constructor(private service: SelfServicePortalService,
                private iService: InstanceService,
                private route: ActivatedRoute) {
        this.route.url.subscribe(data => this.urlSegment = data[0].path);
    }

    ngOnInit() {
        this.service.setPath(this.iService.path + '/' + this.urlSegment + '/');
    }

}
