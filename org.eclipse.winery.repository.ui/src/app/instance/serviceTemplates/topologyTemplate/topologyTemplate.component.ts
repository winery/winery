/*******************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
import { Component, OnInit, ViewChild } from '@angular/core';
import { InstanceService } from '../../instance.service';
import { backendBaseURL, oldTopologyModelerURL, topologyModelerURL } from '../../../configuration';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { WineryVersion } from '../../../wineryInterfaces/wineryVersion';
import { ModalDirective } from 'ngx-bootstrap';
import { ToscaComponent } from '../../../wineryInterfaces/toscaComponent';
import { ToscaTypes } from '../../../wineryInterfaces/enums';
import { ActivatedRoute } from '@angular/router';

@Component({
    templateUrl: 'topologyTemplate.component.html'
})
export class TopologyTemplateComponent implements OnInit {

    loading = true;
    templateUrl: SafeResourceUrl;
    editorUrl: string;
    oldEditorUrl: string;

    selectedVersion: WineryVersion;

    @ViewChild('compareToModal') compareToModal: ModalDirective;

    constructor(private sanitizer: DomSanitizer,
                public sharedData: InstanceService,
                private activatedRoute: ActivatedRoute) {
    }

    ngOnInit() {
        const uiURL = encodeURIComponent(window.location.origin + window.location.pathname + '#/');

        this.templateUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
            backendBaseURL + this.sharedData.path + '/topologytemplate/?view&uiURL=' + uiURL
        );

        let editorConfig = '?repositoryURL=' + encodeURIComponent(backendBaseURL)
            + '&uiURL=' + uiURL
            + '&ns=' + encodeURIComponent(this.sharedData.toscaComponent.namespace)
            + '&id=' + this.sharedData.toscaComponent.localName;

        if (this.sharedData.toscaComponent.toscaType === ToscaTypes.ComplianceRule) {
            const elementPath = this.activatedRoute.snapshot.url[0].path;
            editorConfig += '&parentPath=' + ToscaTypes.ComplianceRule.toLocaleString()
                + '&elementPath=' + elementPath;
        }

        this.editorUrl = topologyModelerURL + editorConfig;
        this.oldEditorUrl = oldTopologyModelerURL + editorConfig;
    }

    versionSelected(version: WineryVersion) {
        this.selectedVersion = version;
    }

    onCompare() {
        let compareUrl = this.editorUrl + '&compareTo='
            + this.sharedData.toscaComponent.localNameWithoutVersion;

        if (this.selectedVersion.toString().length > 0) {
            compareUrl += WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR
                + this.selectedVersion.toString();
        }

        window.open(compareUrl, '_blank');
    }
}
