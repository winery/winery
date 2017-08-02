/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Component, OnInit } from '@angular/core';
import { SelfServicePortalService } from './selfServicePortal.service';
import { ActivatedRoute } from '@angular/router';
import { InstanceService } from '../../instance.service';

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
