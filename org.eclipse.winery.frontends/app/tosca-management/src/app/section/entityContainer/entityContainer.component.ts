/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {backendBaseURL} from '../../configuration';
import {SectionData} from '../sectionData';
import {ToscaTypes} from '../../model/enums';
import {Router} from '@angular/router';
import {ExistService} from '../../wineryUtils/existService';
import {isNullOrUndefined} from 'util';
import {EntityService} from './entity.service';
import { DifferencesData } from './differencesData';

@Component({
    selector: 'winery-entity-container',
    templateUrl: './entityContainer.component.html',
    styleUrls: ['./entityContainer.component.css'],
    providers: [
        EntityService
    ]
})
export class EntityContainerComponent implements OnInit {

    @Input() data: SectionData;
    @Input() toscaType: ToscaTypes;
    @Input() xsdSchemaType: string;
    @Input() maxWidth = 500;
    @Output() deleted = new EventEmitter<string>();
    @Output() showingChildren = new EventEmitter<number>();

    imageUrl: string;
    element: SectionData;
    showVersions = false;
    treeHeight: number;

    differences: DifferencesData;

    constructor(private existService: ExistService, private router: Router, private service: EntityService) {
    }

    ngOnInit() {
        if (this.toscaType === ToscaTypes.NodeType && this.data.versionInstances) {
            const img = backendBaseURL + '/' + this.toscaType
                + '/' + encodeURIComponent(encodeURIComponent(this.data.versionInstances[0].namespace))
                + '/' + this.data.versionInstances[0].id
                + '/appearance/50x50';

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

        if (!isNullOrUndefined(this.data.versionInstances)) {
            this.element = this.data.versionInstances[0];
        } else {
            this.element = this.data;
        }

        if (this.data.versionInstances) {
            this.calculateTreeHeight();
        }
    }

    onClick(event: MouseEvent) {
        if (event.ctrlKey) {
            let url = '/' + this.toscaType + '/';
            let lastElement = this.data.versionInstances[this.data.versionInstances.length - 1];

            if (lastElement.hasChildren) {
                lastElement = lastElement.versionInstances[lastElement.versionInstances.length - 1];
            }

            if (this.toscaType === ToscaTypes.Imports) {
                url += encodeURIComponent(encodeURIComponent(this.xsdSchemaType))
                    + '/' + encodeURIComponent(encodeURIComponent(lastElement.namespace));
            } else {
                url += encodeURIComponent(encodeURIComponent(lastElement.namespace));
            }

            if (lastElement.id) {
                url += '/' + lastElement.id;
            }

            this.router.navigateByUrl(url);
        } else {
            this.showVersions = !this.showVersions;
            if (this.showVersions) {
                this.showingChildren.emit(this.data.versionInstances.length);
            } else {
                this.calculateTreeHeight();
                this.showingChildren.emit(0);
            }
        }
    }

    getContainerStyle(): string {
        if (this.showVersions) {
            if (this.maxWidth === 440) {
                return 'inlineRootContainer';
            }
            return 'rootContainer';
        }
    }

    onShowingGrandChildren($event: number) {
        this.calculateTreeHeight($event);
    }

    private calculateTreeHeight(children = 0) {
        let offset = 139;
        let childrenCount = this.data.versionInstances.length - 1;
        if (children > 0) {
            childrenCount += children - 1;
            offset = offset * 2 + 15;
        }
        if (!isNullOrUndefined(this.differences)) {
            offset += 205;
        }
        this.treeHeight += (childrenCount * 126) + offset;
    }

    isLastElementInList(item: SectionData) {
        return this.data.versionInstances.indexOf(item) < this.data.versionInstances.length - 1;
    }

    hasDifferences(item: SectionData): boolean {
        return isNullOrUndefined(this.differences) ? false : this.differences.base === item;
    }

    showOrHideDifferences(item: SectionData) {
        if (!this.hasDifferences(item)) {
            this.differences = new DifferencesData();
            this.differences.base = item;

            const successorIndex = this.data.versionInstances.indexOf(item) + 1;
            let successor = this.data.versionInstances[successorIndex];
            if (successor.hasChildren) {
                successor = successor.versionInstances[successor.versionInstances.length - 1];
            }
            if (item.hasChildren) {
                item = item.versionInstances[item.versionInstances.length - 1];
            }

            this.service.getChangeLog(this.toscaType, item, successor)
                .subscribe(
                    data => {
                        if (data.length > 0) {
                            this.differences.diff = data;
                        } else {
                            this.differences.diff = 'No differences between those versions';
                        }
                    }
                );
            this.calculateTreeHeight();
        }
    }

    closeDiffView() {
        this.differences = null;
        this.calculateTreeHeight();
    }
}
