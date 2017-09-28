/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { backendBaseURL } from '../../configuration';
import { SectionData } from '../sectionData';
import { ExistService } from '../../wineryUtils/existService';
import { ModalDirective } from 'ngx-bootstrap';
import { Router } from '@angular/router';
import { EntityContainerService } from './entityContainer.service';
import { ToscaTypes } from '../../wineryInterfaces/enums';
import { WineryNotificationService } from '../../wineryNotificationModule/wineryNotification.service';

@Component({
    selector: 'winery-entity-container',
    templateUrl: './entityContainer.component.html',
    styleUrls: ['./entityContainer.component.css'],
    providers: [
        EntityContainerService
    ]
})
export class EntityContainerComponent implements OnInit {

    @Input() data: SectionData;
    @Input() toscaType: ToscaTypes;
    @Input() xsdSchemaType: string;
    @Output() deleted = new EventEmitter<string>();

    @ViewChild('confirmDeleteModal') confirmDeleteModal: ModalDirective;

    imageUrl: string;
    backendLink: string;
    editButtonToolTip = 'Edit.';
    showButtons = true;

    constructor(private existService: ExistService, private router: Router,
                private service: EntityContainerService, private notify: WineryNotificationService) {
    }

    ngOnInit(): void {
        this.backendLink = backendBaseURL + '/' + this.toscaType + '/'
            + encodeURIComponent(encodeURIComponent(this.data.namespace)) + '/' + this.data.id;

        if (this.toscaType === ToscaTypes.NodeType && this.data.id) {
            const img = this.backendLink + '/visualappearance/50x50';

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

        if (this.toscaType === ToscaTypes.ServiceTemplate) {
            this.editButtonToolTip += ' Hold CTRL to directly edit the topology template.';
        }

        this.showButtons = this.toscaType !== ToscaTypes.Imports;
    }

    onClick() {
        let url = '/' + this.toscaType + '/';
        if (this.toscaType === ToscaTypes.Imports) {
            url += encodeURIComponent(encodeURIComponent(this.xsdSchemaType))
                + '/' + encodeURIComponent(encodeURIComponent(this.data.namespace));
        } else {
            url += encodeURIComponent(encodeURIComponent(this.data.namespace));
        }
        if (this.data.id) {
            url += '/' + this.data.id;
        }
        this.router.navigateByUrl(url);
    }

    exportComponent(event: MouseEvent) {
        event.stopPropagation();
        if (event.ctrlKey) {
            window.open(this.backendLink + '?definitions', '_blank');
        } else {
            window.open(this.backendLink + '?csar', '_blank');
        }
    }

    editComponent(event: MouseEvent) {
        event.stopPropagation();
        if (this.toscaType === ToscaTypes.ServiceTemplate && event.ctrlKey) {
            const topologyModeler = backendBaseURL + '-topologymodeler/'
                + '?repositoryURL=' + encodeURIComponent(backendBaseURL)
                + '&uiURL=' + encodeURIComponent(window.location.origin)
                + '&ns=' + encodeURIComponent(this.data.namespace)
                + '&id=' + this.data.id;
            window.open(topologyModeler, '_blank');
        } else {
            this.router.navigateByUrl('/' + this.toscaType + '/' +
                encodeURIComponent(encodeURIComponent(encodeURIComponent(this.data.namespace))) + '/'
                + this.data.id);
        }
    }

    showRemoveDialog(event: MouseEvent) {
        this.confirmDeleteModal.show();
        event.stopPropagation();
    }

    deleteConfirmed() {
        this.service.deleteComponent(this.backendLink, this.data.id)
            .subscribe(
                data => this.success(),
                error => this.notify.error('Error deleting ' + this.data.id)
            );
    }

    private success() {
        this.notify.success('Successfully deleted ' + this.data.id);
        this.deleted.emit(this.data.id);
    }
}
