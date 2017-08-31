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
import { ExistService } from '../wineryUtils/existService';
import { isNullOrUndefined } from 'util';
import { ModalDirective } from 'ngx-bootstrap';
import { backendBaseURL } from '../configuration';
import { ToscaTypes } from '../wineryInterfaces/enums';

/**
 * This component is for checking whether a given component already exists in the repository and displays it
 * accordingly.
 * <label>Inputs</label>
 * <ul>
 *     <li><code>generateData</code> This input of type {@link GenerateData} is mandatory and must contain all
 * necessary
 *     information for generating the url, such as the resource and resource type.
 *     </li>
 *     <li><code>modalRef</code> The modalRef is optional. You can give the reference to the modal which this component
 * is a part of. The reference is used to update the values each time the modal is shown.
 *     </li>
 * </ul>
 */
@Component({
    selector: 'winery-component-exists',
    templateUrl: './wineryComponentExists.component.html',
})
export class WineryComponentExistsComponent implements OnInit {

    @Input() generateData: GenerateData;
    @Input() modalRef: ModalDirective;

    tosca: ToscaTypes;

    constructor(private existService: ExistService) {
    }

    ngOnInit() {
        if (!isNullOrUndefined(this.modalRef)) {
            this.modalRef.onShow.subscribe(() => this.checkImplementationExists());
        }
    }

    checkImplementationExists(): void {
        if (!isNullOrUndefined(this.generateData) && !isNullOrUndefined(this.generateData.toscaType)
            && !isNullOrUndefined(this.generateData.name) && this.generateData.name.length > 0) {
            this.generateData.url = backendBaseURL + '/'
                + this.generateData.toscaType + '/'
                + encodeURIComponent(encodeURIComponent(this.generateData.namespace)) + '/'
                + this.generateData.name + '/';
        }

        if (!this.generateData.namespace.endsWith('/')) {
            this.existService.check(this.generateData.url)
                .subscribe(
                    data => this.generateData.createComponent = false,
                    error => this.generateData.createComponent = true
                );
        }
    }

}

export class GenerateData {
    toscaType: ToscaTypes;
    createComponent: boolean;
    namespace: string;
    name: string;
    url: string;
}
