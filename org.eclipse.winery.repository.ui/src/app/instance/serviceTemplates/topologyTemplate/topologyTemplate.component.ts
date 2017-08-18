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
import { InstanceService } from '../../instance.service';
import { backendBaseURL } from '../../../configuration';
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
        const uiURL = encodeURIComponent(window.location.origin);
        this.templateUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            backendBaseURL + this.sharedData.path + '/topologytemplate/?view&uiURL=' + uiURL
        );
        this.editorUrl = backendBaseURL + '-topologymodeler/'
            + '?repositoryURL=' + encodeURIComponent(backendBaseURL)
            + '&uiURL=' + uiURL
            + '&ns=' + encodeURIComponent(this.sharedData.toscaComponent.namespace)
            + '&id=' + this.sharedData.toscaComponent.localName;
    }
}
