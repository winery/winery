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
import { Component, Input, OnInit } from '@angular/core';
import { backendBaseURL } from '../../configuration';
import { SectionData } from '../sectionData';
import { ExistService } from '../../wineryUtils/existService';

@Component({
    selector: 'winery-entity-container',
    templateUrl: './entityContainer.component.html',
    styleUrls: ['./entityContainer.component.css']
})
export class EntityContainerComponent implements OnInit {
    @Input() data: SectionData;
    @Input() resourceType: string;

    imageUrl: string;

    constructor(private existService: ExistService) {
    }

    ngOnInit(): void {
        if (this.resourceType === 'nodeType' && this.data.id) {
            const img = backendBaseURL + '/'
                + this.resourceType.toLowerCase() + 's/'
                + encodeURIComponent(encodeURIComponent(this.data.namespace)) + '/'
                + this.data.id
                + '/visualappearance/50x50';

            this.existService.check(img)
                .subscribe(
                    () => {
                        this.imageUrl = img;
                    },
                    () => {
                        this.imageUrl = null;
                    },
                );
        }
    }
}
