/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { SectionService } from './section.service';
import { SectionData } from './sectionData';

@Component({
    selector: 'winery-section-component',
    templateUrl: 'section.component.html',
    providers: [
        SectionService,
    ]
})
export class SectionComponent implements OnInit, OnDestroy {

    componentData: SectionData[];
    loading: boolean = true;
    selectedResource: string;
    routeSub: Subscription;

    constructor(private route: ActivatedRoute,
                private service: SectionService,
    ) { }

    /**
     * @override
     *
     * Subscribe to the url on initialisation in order to get the corresponding resource type.
     */
    ngOnInit(): void {
        this.routeSub = this.route
            .data
            .subscribe(
                data => this.getComponentData(data),
                error => this.handleError(error)
            );
    }

    ngOnDestroy(): void {
        this.routeSub.unsubscribe();
    }

    private getComponentData(data: any) {
        let resolved = data['resolveData'];
        this.selectedResource = resolved.section;
        this.service.getSectionData(resolved.path)
            .subscribe(
                res => this.handleData(res),
                error => this.handleError(error)
            );
    }

    private handleData(resources: SectionData[]) {
        this.componentData = resources;
        this.loading = false;
    }

    private handleError(error: any): void {
        this.loading = false;
        console.log(error);
    }
}
