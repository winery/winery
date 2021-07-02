/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { InterfaceOperationApiData, InterfacesApiData } from '../interfacesApiData';
import { CurrentSelectedEnum, NodeOperation, PlanOperation, RelationshipOperation } from './operations';
import { InstanceService } from '../../../instance.service';
import { WineryTemplate } from '../../../../model/wineryComponent';
import { InterfacesService } from '../interfaces.service';
import { WineryNotificationService } from '../../../../wineryNotificationModule/wineryNotification.service';
import { SelectItem } from 'ng2-select';
import { PlansService } from '../../../serviceTemplates/plans/plans.service';
import { PlansApiData } from '../../../serviceTemplates/plans/plansApiData';

/**
 * Component for setting Boundary Definitions Interfaces. Is used in {@link InterfacesComponent}.
 * It can be used by passing in a <code>operation</code> of type {@link InterfaceOperationApiData}.
 */
@Component({
    selector: 'winery-service-templates-target-interface',
    templateUrl: 'wineryTargetInterface.component.html',
    providers: [
        PlansService
    ]
})
export class WineryTargetInterfaceComponent implements OnInit, OnChanges {

    @Input() operation: InterfaceOperationApiData;

    loading = false;
    referenceData: WineryTemplate[];
    plans: WineryTemplate[];
    activeReference: WineryTemplate;
    interfaces: InterfacesApiData[];
    activeInterface: InterfacesApiData;
    activeOperation: InterfaceOperationApiData;

    currentSelected: CurrentSelectedEnum;

    constructor(private sharedData: InstanceService,
                private interfacesService: InterfacesService,
                private plansService: PlansService,
                private notify: WineryNotificationService) {
    }

    ngOnInit() {
        this.plansService.getPlansData(this.sharedData.path + '/plans/')
            .subscribe(
                data => this.handlePlansData(data)
            );
    }

    ngOnChanges(changes: SimpleChanges) {
        if (this.operation) {
            if (this.operation.relationshipOperation) {
                this.onRelationshipSelected();
            } else if (this.operation.plan) {
                this.onPlanSelected();
            } else {
                this.onNodeSelected();
            }
        }
    }

    onNodeSelected() {
        this.referenceData = this.sharedData.topologyTemplate.nodeTemplates
            ? this.sharedData.topologyTemplate.nodeTemplates
            : null;
        if (!this.operation.nodeOperation) {
            this.operation.nodeOperation = new NodeOperation();
            this.activeReference = this.referenceData ? this.referenceData[0] : null;

            if (this.activeReference) {
                this.operation.nodeOperation.nodeRef = this.activeReference.id;
            }
        } else if (this.referenceData) {
            this.activeReference = this.referenceData.find((element) => {
                return element.id === this.operation.nodeOperation.nodeRef;
            });
        }
        this.operation.relationshipOperation = null;
        this.operation.plan = null;
        this.currentSelected = CurrentSelectedEnum.nodeTemplate;
        this.getInterfaces();
    }

    onRelationshipSelected() {
        this.referenceData = this.sharedData.topologyTemplate.relationshipTemplates
            ? this.sharedData.topologyTemplate.relationshipTemplates
            : null;
        if (!this.operation.relationshipOperation) {
            this.operation.relationshipOperation = new RelationshipOperation();
            this.activeReference = this.referenceData ? this.referenceData[0] : null;

            if (this.activeReference) {
                this.operation.relationshipOperation.relationshipRef = this.activeReference.id;
            }
        } else if (this.referenceData) {
            this.activeReference = this.referenceData.find((element) => {
                return element.id === this.operation.relationshipOperation.relationshipRef;
            });
        }
        this.operation.nodeOperation = null;
        this.operation.plan = null;
        this.currentSelected = CurrentSelectedEnum.relationshipTemplate;
        this.getInterfaces();
    }

    onPlanSelected() {
        this.referenceData = this.plans ? this.plans : null;
        this.activeReference = this.plans ? this.plans[0] : null;

        if (!this.operation.plan) {
            this.operation.plan = new PlanOperation();
            this.operation.plan.planRef = this.activeReference.id;
        } else if (this.referenceData) {
            this.activeReference = this.referenceData.find((element) => {
                return element.id === this.operation.plan.planRef;
            });
        }

        this.operation.nodeOperation = null;
        this.operation.relationshipOperation = null;
        this.currentSelected = CurrentSelectedEnum.plan;
    }

    onReferenceSelected(event: SelectItem) {
        this.activeReference = this.referenceData.find((element) => {
            return element.id === event.id;
        });

        if (this.currentSelected === CurrentSelectedEnum.nodeTemplate) {
            this.operation.nodeOperation.nodeRef = this.activeReference.id;
            this.operation.nodeOperation.interfaceName = null;
            this.operation.nodeOperation.operationName = null;
        } else if (this.currentSelected === CurrentSelectedEnum.relationshipTemplate) {
            this.operation.relationshipOperation.relationshipRef = this.activeReference.id;
            this.operation.relationshipOperation.operationName = null;
            this.operation.relationshipOperation.interfaceName = null;
        } else {
            this.operation.plan.planRef = this.activeReference.id;
        }

        this.getInterfaces();
    }

    onInterfaceSelected(event: SelectItem) {
        this.activeInterface = this.interfaces.find((element) => {
            return element.name === event.id;
        });
        this.activeOperation = this.activeInterface.operation[0];
        this.setInterfaceAndOperation();
    }

    onOperationSelected(event: SelectItem) {
        this.activeOperation = this.activeInterface.operation.find((element) => {
            return element.name === event.id;
        });
        this.setInterfaceAndOperation();
    }

    private getInterfaces() {
        this.loading = true;
        this.interfaces = null;
        this.activeInterface = null;
        this.activeOperation = null;

        let relationshipInterfaces = false;

        if (this.currentSelected === CurrentSelectedEnum.nodeTemplate) {
            this.activeReference = this.sharedData.topologyTemplate.nodeTemplates.find((element, id, context) => {
                return element.id === this.operation.nodeOperation.nodeRef;
            });

            if (!this.activeReference) {
                this.activeReference = this.sharedData.topologyTemplate.nodeTemplates[0];
            }
        } else if (this.currentSelected === CurrentSelectedEnum.relationshipTemplate) {
            relationshipInterfaces = true;
            this.activeReference = this.sharedData.topologyTemplate.relationshipTemplates.find((element, id, context) => {
                return element.id === this.operation.relationshipOperation.relationshipRef;
            });

            if (!this.activeReference) {
                this.activeReference = this.sharedData.topologyTemplate.relationshipTemplates[0];
            }
        }

        if (this.activeReference) {
            const qName = this.activeReference.type.slice(1).split('}');
            const url = '/' + this.currentSelected + '/' + encodeURIComponent(encodeURIComponent(qName[0])) + '/' + qName[1];

            this.interfacesService.getInterfaces(url, relationshipInterfaces)
                .subscribe(
                    data => this.handleInterfaceData(data),
                    error => this.handleError(error)
                );
        } else {
            this.loading = false;
        }
    }

    private setInterfaceAndOperation() {
        if (this.activeInterface) {
            switch (this.currentSelected) {
                case CurrentSelectedEnum.nodeTemplate:
                    this.operation.nodeOperation.interfaceName = this.activeInterface.name;
                    if (this.activeOperation) {
                        this.operation.nodeOperation.operationName = this.activeOperation.name;
                    }
                    break;
                case CurrentSelectedEnum.relationshipTemplate:
                    this.operation.relationshipOperation.interfaceName = this.activeInterface.name;
                    if (this.activeOperation) {
                        this.operation.relationshipOperation.operationName = this.activeOperation.name;
                    }
                    break;
            }
        }
    }

    private handleInterfaceData(data: InterfacesApiData[]) {
        this.loading = false;
        this.interfaces = data;

        if (this.currentSelected === CurrentSelectedEnum.nodeTemplate) {
            const nodeInterface = this.interfaces.find((element, id, arr) => {
                return element.name === this.operation.nodeOperation.interfaceName;
            });

            this.activeInterface = nodeInterface ? nodeInterface : this.interfaces[0];

            if (this.activeInterface) {
                this.activeOperation = this.activeInterface.operation.find((element, id, arr) => {
                    return element.name === this.operation.nodeOperation.operationName;
                });
            }
        } else if (this.currentSelected === CurrentSelectedEnum.relationshipTemplate) {
            const relInterface = this.interfaces.find((element, id, arr) => {
                return element.name === this.operation.relationshipOperation.interfaceName;
            });

            this.activeInterface = relInterface ? relInterface : this.interfaces[0];

            if (this.activeInterface) {
                this.activeOperation = this.activeInterface.operation.find((element, id, arr) => {
                    return element.name === this.operation.relationshipOperation.operationName;
                });
            }
        }

        if (this.activeInterface && !this.activeOperation) {
            this.activeOperation = this.activeInterface.operation[0];
        }

        this.setInterfaceAndOperation();
    }

    private handleError(error: any) {
        this.loading = false;
        this.notify.error(error);
    }

    private handlePlansData(data: PlansApiData[]) {
        this.plans = [];

        for (const plan of data) {
            const tmp = new WineryTemplate();
            tmp.setValuesFromPlan(plan);
            this.plans.push(tmp);
        }

        if (this.currentSelected === CurrentSelectedEnum.plan) {
            this.onPlanSelected();
        }
    }

}
