/*******************************************************************************
 * Copyright (c) 2017-2021 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { backendBaseURL } from '../../configuration';
import { SectionData } from '../sectionData';
import { ToscaTypes } from '../../model/enums';
import { Router } from '@angular/router';
import { ExistService } from '../../wineryUtils/existService';
import { EntityService } from './entity.service';
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
    @Output() showingChildren = new EventEmitter<ShowingSubChildren>();
    @Output() showsLastElement = new EventEmitter<boolean>();

    imageUrl: string;
    element: SectionData;
    showVersions = false;
    treeHeight: number;
    openChildren: Map<string, ShowingSubChildren> = new Map<string, ShowingSubChildren>();

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

        if (this.data.versionInstances) {
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
            this.emitContainerChange();
        }
    }

    getContainerStyle(): string {
        if (this.showVersions) {
            return 'inlineRootContainer';
        }
    }

    onShowingGrandChildren(event: ShowingSubChildren) {
        this.openChildren.set(event.definitionsId, event);
        this.calculateTreeHeight();
    }

    isLastElementInList(item: SectionData) {
        return this.data.versionInstances.indexOf(item) < this.data.versionInstances.length - 1;
    }

    hasDifferences(item: SectionData): boolean {
        return this.differences ? this.differences.base === item : false;
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
            this.emitContainerChange();
        }
    }

    closeDiffView() {
        this.differences = null;
        this.emitContainerChange();
        this.calculateTreeHeight();
    }

    private calculateTreeHeight() {
        //  86px for the container
        // +10px margin to the next container
        // +43px to the center of the first container
        let offset = 139;
        // Thus, we need to subtract 1 from all version instances.
        let childrenCount = this.data.versionInstances.length - 1;

        // If we show the differences dialog, we add the size of the dialog.
        let children = 0;
        let directChildrenShowingTheirContent = 0;
        let containersShowingDiff = 0;
        this.openChildren.forEach((child: ShowingSubChildren) => {
            if (child.showingDifferences) {
                containersShowingDiff++;
            }
            if (child.childrenCount > 0) {
                children += child.childrenCount;
                directChildrenShowingTheirContent++;
            }
        });

        const lastElement = this.data.versionInstances[this.data.versionInstances.length - 1];
        const lastElementOpen = this.openChildren.get(lastElement.id);
        if (lastElementOpen && lastElementOpen.childrenCount > 0) {
            // Somehow, the height of a half container is missing if the last container is expanded.
            // Thus, add 43px plus some extra boundary.
            offset -= 126 * lastElementOpen.childrenCount;
            directChildrenShowingTheirContent--;
            this.showsLastElement.emit(true);
        } else {
            this.showsLastElement.emit(false);
        }

        if (children > 0) {
            childrenCount += children;
            // Because there is no Differences button between the container and the first version instance,
            // we must subtract 30px.
            offset -= 30 * directChildrenShowingTheirContent;
        }

        if (this.differences) {
            containersShowingDiff++;
        }
        offset += 205 * containersShowingDiff;

        // Between the center of two containers, there are 126px:
        //  2x 43px for each container center
        // +   10px margin to the next container
        // +   30px height of the Differences Button
        this.treeHeight = (childrenCount * 126) + offset;
    }

    private emitContainerChange() {
        this.showingChildren.emit({
            definitionsId: this.data.id,
            childrenCount: this.showVersions ? this.data.versionInstances.length : 0,
            showingDifferences: !!this.differences
        });
    }
}

interface ShowingSubChildren {
    childrenCount: number;
    definitionsId: string;
    showingDifferences: boolean;
}
