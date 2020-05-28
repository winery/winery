/*******************************************************************************
 * Copyright (c) 2019-2020 Contributors to the Eclipse Foundation
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
import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { WineryRepositoryConfigurationService } from './WineryRepositoryConfiguration.service';

@Directive({
    selector: '[wineryRepositoryShowOnFeature]'
})
export class ShowOnFeatureDirective implements OnInit {

    @Input('wineryRepositoryShowOnFeature') public featuresToShow: string | string[];

    constructor(private templateRef: TemplateRef<any>, private viewContainerRef: ViewContainerRef,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    ngOnInit() {
        if (Array.isArray(this.featuresToShow)) {
            let found = false;
            for (const feature of this.featuresToShow) {
                if (this.configurationService.configuration.features[feature]) {
                    this.viewContainerRef.createEmbeddedView(this.templateRef);
                    found = true;
                }
            }
            if (!found) {
                this.viewContainerRef.clear();
            }
        } else if (typeof this.featuresToShow === 'string') {
            if (this.configurationService.configuration.features[this.featuresToShow]) {
                this.viewContainerRef.createEmbeddedView(this.templateRef);
            } else {
                this.viewContainerRef.clear();
            }
        }
    }
}
