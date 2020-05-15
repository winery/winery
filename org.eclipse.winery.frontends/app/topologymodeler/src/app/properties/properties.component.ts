/*******************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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

import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { JsPlumbService } from '../services/jsPlumb.service';
import { PropertyDefinitionType } from '../models/enums';
import { KeyValueItem } from '../../../../tosca-management/src/app/model/keyValueItem';
import { TNodeTemplate, TRelationshipTemplate } from '../models/ttopology-template';
import { isNullOrUndefined } from 'util';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';

@Component({
    selector: 'winery-properties',
    templateUrl: './properties.component.html',
    styleUrls: ['./properties.component.css']
})
export class PropertiesComponent implements OnInit, OnChanges, OnDestroy {

    @Input() readonly: boolean;
    @Input() templateId: string;
    @Input() isNode: boolean;

    private propertyDefinitionType: PropertyDefinitionType;
    private templateProperties: any = {};
    private templateType: string;

    private subscriptions: Array<Subscription> = [];
    // flag to allow skipping an update when this instance is the instigator of said update
    //  this way we avoid recreating the input form during the editing process
    private skipUpdate = false;

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private jsPlumbService: JsPlumbService,
                private repoConfiguration: WineryRepositoryConfigurationService,
                ) {
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        if (changes.isNode) {
            this.isNode = changes.isNode.currentValue;
        }
        if (changes.templateId) {
            this.clearSubscriptions();
            this.templateId = changes.templateId.currentValue;
            if (this.templateId) {
                this.subscriptions.push(this.buildSubscription());
            } else {
                this.templateProperties = {};
                this.propertyDefinitionType = PropertyDefinitionType.NONE;
            }
        }
        // repaint jsPlumb to account for height change of the accordion
        setTimeout(() => this.jsPlumbService.getJsPlumbInstance().repaintEverything(), 1);
    }

    /**
     * Angular lifecycle event.
     */
    ngOnInit() {
        if (this.templateId) {
            this.clearSubscriptions();
            this.subscriptions.push(this.buildSubscription());
        }
    }

    private loadData(template: TNodeTemplate | TRelationshipTemplate): void {
        if (this.skipUpdate) {
            this.skipUpdate = false;
            return;
        }
        const propertyData = template.properties;
        // TODO get nodeTemplate's type and grab the propertiesDefinition for the NodeType.
        //  That definition can be a List<YamlPropertiesDefinition> or an XmlPropertiesDefinition.
        //  This way we can obtain the constraints required for input form generation.

        // this.propertyDefinitionType = this.determinePropertyDefinitionType(nodeTemplate);
        this.propertyDefinitionType = propertyData.propertyType;
        this.templateType = template.type;
        // reset nodeProperties to empty object to change it's pointer for change detection to work
        this.templateProperties = {};
        try {
            if (this.propertyDefinitionType === PropertyDefinitionType.KV) {
                // need to use Object.assign here to avoid overwriting the refreshed pointer
                Object.assign(this.templateProperties, propertyData.kvproperties);
            } else if (this.propertyDefinitionType === PropertyDefinitionType.XML) {
                // since this particular value is a String, Angular correctly detects changes
                this.templateProperties = propertyData.any;
            } else if (this.propertyDefinitionType === PropertyDefinitionType.YAML) {
                // need to use Object.assign here to avoid overwriting the refreshed pointer
                Object.assign(this.templateProperties, propertyData.properties);
            }
        } catch (e) {
        }
    }

    private clearSubscriptions() {
        this.subscriptions.forEach(s => s.unsubscribe());
    }

    ngOnDestroy() {
        this.clearSubscriptions();
    }

    xmlPropertyEdit($event: string) {
        this.templateProperties = $event;
        this.dispatchRedux();
    }

    kvPropertyEdit($event: KeyValueItem) {
        this.templateProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    // TODO? rewrite to have a "PathValueItem"
    yamlPropertyEdit($event: KeyValueItem) {
        // FIXME deal with the fact that yaml properties support complex datatypes, implying nesting
        this.templateProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    private dispatchRedux(): void {
        this.skipUpdate = true;
        this.$ngRedux.dispatch(this.actions.setProperty({
            nodeProperty: {
                newProperty: this.templateProperties,
                propertyType: this.propertyDefinitionType,
                nodeId: this.templateId,
            }
        }));
    }

    private buildSubscription(): Subscription {
        if (!this.templateId) {
            return;
        }
        // we also need to display relationships here
        if (this.isNode) {
            return this.$ngRedux.select(wineryState => wineryState
                .wineryState
                .currentJsonTopology
                .nodeTemplates
                .find(nt => {
                    return nt.id === this.templateId;
                })
            ).subscribe(nodeTemplate => {
                if (nodeTemplate) {
                    this.loadData(nodeTemplate);
                }
            });
        } else {
            return this.$ngRedux.select(wineryState => wineryState
                .wineryState
                .currentJsonTopology
                .relationshipTemplates
                .find(rt => {
                    return rt.id === this.templateId;
                })
            ).subscribe(relationship => {
                if (relationship) {
                    this.loadData(relationship);
                }
            });
        }
    }
}
