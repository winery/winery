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
import {InstanceService} from '../../instance.service';
import {backendBaseURL, topologyModelerURL} from '../../../configuration';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';

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
        const uiURL = encodeURIComponent(window.location.origin + window.location.pathname);

        this.templateUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            backendBaseURL + this.sharedData.path + '/topologytemplate/?view&uiURL=' + uiURL
        );
        this.editorUrl = topologyModelerURL
            + '?repositoryURL=' + encodeURIComponent(backendBaseURL)
            + '&uiURL=' + uiURL
            + '&ns=' + encodeURIComponent(this.sharedData.toscaComponent.namespace)
            + '&id=' + this.sharedData.toscaComponent.localName;
    }
}
