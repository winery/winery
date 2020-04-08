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

import { ChangeDetectorRef, Component, Input, NgZone, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { NgRedux } from '@angular-redux/store';
import { IWineryState } from '../redux/store/winery.store';
import { WineryActions } from '../redux/actions/winery.actions';
import { JsPlumbService } from '../services/jsPlumb.service';
import { PropertyDefinitionType } from '../models/enums';
import { KeyValueItem } from '../../../../tosca-management/src/app/model/keyValueItem';
import { TNodeTemplate } from '../models/ttopology-template';
import { isNullOrUndefined } from 'util';
import { WineryRepositoryConfigurationService } from '../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

@Component({
    selector: 'winery-properties',
    templateUrl: './properties.component.html',
    styleUrls: ['./properties.component.css']
})
export class PropertiesComponent implements OnInit, OnChanges, OnDestroy {

    @Input() readonly: boolean;
    @Input() nodeId: string;

    propertyDefinitionType: PropertyDefinitionType;
    nodeProperties: any = {};

    private subscriptions: Array<Subscription> = [];

    constructor(private $ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private jsPlumbService: JsPlumbService,
                private repoConfiguration: WineryRepositoryConfigurationService,
                private change: ChangeDetectorRef) {
    }

    /**
     * Angular lifecycle event.
     */
    ngOnChanges(changes: SimpleChanges) {
        if (changes.nodeId) {
            this.clearSubscriptions();
            this.nodeId = changes.nodeId.currentValue;
            if (this.nodeId) {
                this.subscriptions.push(this.buildSubscription(this.nodeId));
            } else {
                this.nodeProperties = {};
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
        if (this.nodeId) {
            this.clearSubscriptions();
            this.subscriptions.push(this.buildSubscription(this.nodeId));
        }
    }

    // FIXME need to deal with losing focus on the newly generated form.
    //  Consider having some way to instead update the form only if necessary?
    private loadData(nodeTemplate: TNodeTemplate): void {
        const propertyData = nodeTemplate.properties;
        this.propertyDefinitionType = this.determinePropertyDefinitionType(nodeTemplate);
        // reset nodeProperties to empty object to change it's pointer for change detection to work
        this.nodeProperties = {};
        try {
            if (this.propertyDefinitionType === PropertyDefinitionType.KV) {
                // need to use Object.assign here to avoid overwriting the refreshed pointer
                Object.assign(this.nodeProperties, propertyData.kvproperties);
            } else if (this.propertyDefinitionType === PropertyDefinitionType.XML) {
                // FIXME this could also be using propertyData.element because XML props can be two different things!
                // since this particular value is a String, Angular correctly detects changes
                this.nodeProperties = propertyData.any;
            } else if (this.propertyDefinitionType === PropertyDefinitionType.YAML) {
                // FIXME this is not really useful, actually
                // need to use Object.assign here to avoid overwriting the refreshed pointer
                Object.assign(this.nodeProperties, propertyData.kvproperties);
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
        this.nodeProperties = $event;
        this.dispatchRedux();
    }

    kvPropertyEdit($event: KeyValueItem) {
        this.nodeProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    yamlPropertyEdit($event: KeyValueItem) {
        // FIXME deal with the fact that yaml properties support complex datatypes, implying nesting
        this.nodeProperties[$event.key] = $event.value;
        this.dispatchRedux();
    }

    private dispatchRedux(): void {
        this.$ngRedux.dispatch(this.actions.setProperty({
            nodeProperty: {
                newProperty: this.nodeProperties,
                propertyType: this.propertyDefinitionType,
                nodeId: this.nodeId,
            }
        }));
    }

    private buildSubscription(nodeId: string): Subscription {
        if (!nodeId) {
            return;
        }
        return this.$ngRedux.select(wineryState => wineryState
            .wineryState
            .currentJsonTopology
            .nodeTemplates
            .find(nt => {
                return nodeId && nt.id === nodeId;
            })
        ).subscribe(nodeTemplate => {
            if (nodeTemplate) {
                this.loadData(nodeTemplate);
            }
        });
    }

    private determinePropertyDefinitionType(nodeTemplate: TNodeTemplate): PropertyDefinitionType {
        // if PropertiesDefinition doesn't exist then it must be of type NONE
        if (isNullOrUndefined(nodeTemplate.properties)) {
            return PropertyDefinitionType.NONE;
        }
        // if no XML element inside PropertiesDefinition then it must be of type Key Value
        if (!(nodeTemplate.properties.element || nodeTemplate.properties.any)) {
            return this.repoConfiguration.isYaml() ?
                PropertyDefinitionType.YAML :
                PropertyDefinitionType.KV;
        } else {
            // else we have XML
            return PropertyDefinitionType.XML;
        }
    }
}
