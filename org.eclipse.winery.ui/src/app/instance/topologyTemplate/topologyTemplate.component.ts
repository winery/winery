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
import { Component, OnInit } from '@angular/core';
import { InstanceService } from '../instance.service';
import { backendBaseUri } from '../../configuration';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
    templateUrl: 'topologyTemplate.component.html'
})
export class TopologyTemplateComponent implements OnInit {

    loading = true;
    templateUrl: SafeResourceUrl;
    editorUrl: string;

    constructor(private sanitizer: DomSanitizer, private sharedData: InstanceService) {
    }

    ngOnInit() {
        this.templateUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            backendBaseUri + this.sharedData.path + '/topologytemplate/?view'
        );
        this.editorUrl = backendBaseUri + '-topologymodeler/'
            + '?repositoryURL=' + encodeURIComponent(backendBaseUri)
            + '&ns=' + encodeURIComponent(this.sharedData.selectedNamespace)
            + '&id=' + this.sharedData.selectedComponentId;
    }
}
