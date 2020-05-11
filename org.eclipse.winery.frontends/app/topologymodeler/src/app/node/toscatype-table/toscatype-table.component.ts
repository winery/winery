/*******************************************************************************
 * Copyright (c) 2018-2020 Contributors to the Eclipse Foundation
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

import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { QName } from '../../models/qname';
import { EntitiesModalService, OpenModalEvent } from '../../canvas/entities-modal/entities-modal.service';
import { ModalVariant } from '../../canvas/entities-modal/modal-model';
import { definitionType, TableType, urlElement } from '../../models/enums';
import { BackendService } from '../../services/backend.service';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { ReqCapRelationshipService } from '../../services/req-cap-relationship.service';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { ReqCapModalType, ShowReqCapModalEventData } from './showReqCapModalEventData';
import { RequirementModel } from '../../models/requirementModel';
import { RequirementDefinitionModel } from '../../models/requirementDefinitonModel';
import { TArtifact, VisualEntityType } from '../../models/ttopology-template';
import { TPolicy } from '../../models/policiesModalData';
import { InheritanceUtils } from '../../models/InheritanceUtils';

@Component({
    selector: 'winery-toscatype-table',
    templateUrl: './toscatype-table.component.html',
    styleUrls: ['./toscatype-table.component.css']
})
export class ToscatypeTableComponent implements OnInit, OnChanges {

    readonly editOperation = ReqCapModalType.Edit;
    readonly newOperation = ReqCapModalType.AddNew;
    readonly tableTypes = TableType;

    @Input() tableType: TableType;
    @Input() currentNodeData: any;
    @Input() toscaTypeData: any;
    @Input() entityTypes: EntityTypesModel;

    // Event emitter for showing the modal of a clicked capability or requirement id
    @Output() showClickedReqOrCapModal: EventEmitter<ShowReqCapModalEventData>;
    @Output() relationshipTemplateIdClicked: EventEmitter<string>;
    @Output() showYamlPolicyManagementModal: EventEmitter<void>;

    currentToscaTypeData;
    currentToscaType;
    latestNodeTemplate?: any = {};

    constructor(private entitiesModalService: EntitiesModalService,
                private backendService: BackendService,
                private reqCapRelationshipService: ReqCapRelationshipService,
                private configurationService: WineryRepositoryConfigurationService) {
        this.showClickedReqOrCapModal = new EventEmitter();
        this.relationshipTemplateIdClicked = new EventEmitter<string>();
        this.showYamlPolicyManagementModal = new EventEmitter<void>();
    }

    ngOnInit() {
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes['toscaTypeData']) {
            this.currentToscaTypeData = this.toscaTypeData;
        }
        if (changes['toscaType']) {
            this.currentToscaType = this.tableType;
        }
    }

    isEllipsisActive(cell): boolean {
        return (cell.offsetWidth < cell.scrollWidth);
    }

    getLocalName(qName?: string): string {
        const qNameVar = new QName(qName);
        return qNameVar.localName;
    }

    getNamespace(qName?: string): string {
        const qNameVar = new QName(qName);
        return qNameVar.nameSpace;
    }

    clickArtifactRef(artifactRef: string) {
        const url = this.backendService.configuration.uiURL
            + 'artifacttemplates/'
            + encodeURIComponent(encodeURIComponent(this.getNamespace(artifactRef)))
            + '/' + this.getLocalName(artifactRef);
        window.open(url, '_blank');
    }

    clickArtifactType(artifactType: string) {
        const url = this.backendService.configuration.uiURL
            + 'artifacttypes/'
            + encodeURIComponent(encodeURIComponent(this.getNamespace(artifactType)))
            + '/' + this.getLocalName(artifactType);
        window.open(url, '_blank');
    }

    clickPolicyRef(policyRef: string) {
        const url = this.backendService.configuration.uiURL
            + 'policytemplates/'
            + encodeURIComponent(encodeURIComponent(this.getNamespace(policyRef)))
            + '/' + this.getLocalName(policyRef);
        window.open(url, '_blank');
    }

    clickPolicyType(policyType: string) {
        const url = this.backendService.configuration.uiURL
            + 'policytypes/'
            + encodeURIComponent(encodeURIComponent(this.getNamespace(policyType)))
            + '/' + this.getLocalName(policyType);
        window.open(url, '_blank');
    }

    openPolicyModal(policy) {
        if (this.configurationService.isYaml()) {
            this.showYamlPolicyManagementModal.emit();
        } else {
            let qName;
            let namespace = '';
            let templateName = '(none)';
            try {
                qName = new QName(policy.policyRef);
                namespace = qName.nameSpace;
                templateName = qName.localName;
            } catch (e) {
                console.log(e);
            }
            const type = policy.policyType;
            const name = policy.name;
            const currentNodeId = this.currentNodeData.currentNodeId;
            // push new event onto Subject
            const eventObject: OpenModalEvent = new OpenModalEvent(currentNodeId, ModalVariant.Policies, name, templateName, namespace, type);
            this.entitiesModalService.openModalEvent.next(eventObject);
        }
    }

    openDeploymentArtifactModal(deploymentArtifact) {
        let qName;
        let namespace;
        let templateName = '(none)';
        try {
            qName = new QName(deploymentArtifact.artifactRef);
            namespace = qName.nameSpace;
            templateName = qName.localName;
        } catch (e) {
            console.log(e);
        }
        const type = deploymentArtifact.artifactType;
        const name = deploymentArtifact.name;
        const currentNodeId = this.currentNodeData.currentNodeId;
        // push new event onto Subject
        const eventObject: OpenModalEvent = new OpenModalEvent(currentNodeId, ModalVariant.DeploymentArtifacts, name, templateName, namespace, type);
        this.entitiesModalService.openModalEvent.next(eventObject);
    }

    openYamlArtifactModal(yamlArtifact: TArtifact) {
        const type = yamlArtifact.type;
        const name = yamlArtifact.id;
        const currentNodeId = this.currentNodeData.currentNodeId;
        // push new event onto Subject
        const eventObject: OpenModalEvent = new OpenModalEvent(
            currentNodeId,
            ModalVariant.DeploymentArtifacts,
            name,
            '',
            '',
            type,
            yamlArtifact.file,
            yamlArtifact.targetLocation);
        this.entitiesModalService.openModalEvent.next(eventObject);
    }

    /**
     * This modal handler gets triggered upon clicking on a capability or requirement id in the table
     * @param id - the id of the rea/cap that was clicked.
     * @param operation the type of the requested operation.
     */
    showExistingReqOrCapModal(id: string, operation: ReqCapModalType): void {
        const event = new ShowReqCapModalEventData(id, operation);
        this.showClickedReqOrCapModal.emit(event);
    }

    /**
     * Gets triggered upon clicking on a capability or requirement name in the table, links to the defined names in the
     * management UI
     * @param reqOrCapRef - the name
     */
    clickReqOrCapRef(reqOrCapRef: string) {
        let clickedDefinition;
        let url;
        if (this.tableType === this.tableTypes.Requirements) {
            clickedDefinition = definitionType.RequirementDefinitions;
        } else {
            clickedDefinition = definitionType.CapabilityDefinitions;
        }

        url = this.backendService.configuration.uiURL
            + urlElement.NodeTypeURL
            + encodeURIComponent(encodeURIComponent(this.getNamespace(this.currentNodeData.nodeTemplate.type)))
            + '/' + this.getLocalName(this.currentNodeData.nodeTemplate.type) + clickedDefinition;
        window.open(url, '_blank');
    }

    clickYamlReqRef(): void {
        const url = this.backendService.configuration.uiURL
            + urlElement.NodeTypeURL
            + this.getNamespace(this.currentNodeData.nodeTemplate.type)
            + '/' + this.getLocalName(this.currentNodeData.nodeTemplate.type)
            + '/requirementdefinitionsyaml/';

        window.open(url, '_blank');
    }

    clickYamlCapRef(): void {
        const url = this.backendService.configuration.uiURL
            + urlElement.NodeTypeURL
            + this.getNamespace(this.currentNodeData.nodeTemplate.type)
            + '/' + this.getLocalName(this.currentNodeData.nodeTemplate.type)
            + definitionType.CapabilityDefinitions;

        window.open(url, '_blank');
    }

    clickYamlRelationshipTemplateId(id: string, req: RequirementModel) {
        // the id might not belong to a relationship template if the requirement is not yet fulfilled.
        if (this.isRequirementFulfilled(req)) {
            this.relationshipTemplateIdClicked.emit(id);
        }
    }

    private getRequirementDefinition(req: RequirementModel): RequirementDefinitionModel {
        const listOfBequeathingNodeTypes = InheritanceUtils
            .getInheritanceAncestry(this.currentNodeData.nodeTemplate.type, this.entityTypes.unGroupedNodeTypes);
        for (const nodeType of listOfBequeathingNodeTypes) {
            if (nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0] &&
                nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].requirementDefinitions &&
                nodeType.full.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].requirementDefinitions.requirementDefinition) {
                const requirementDefinition = nodeType
                    .full
                    .serviceTemplateOrNodeTypeOrNodeTypeImplementation[0]
                    .requirementDefinitions
                    .requirementDefinition
                    .find((reqDef: RequirementDefinitionModel) => reqDef.name === req.name);
                if (requirementDefinition) {
                    return requirementDefinition;
                }
            }
        }
        return null;
    }

    getAllowedRelationshipTypes(req: RequirementModel): VisualEntityType[] {
        const reqDef: RequirementDefinitionModel = this.getRequirementDefinition(req);
        // if the requirement definition specifies a relationship type, then it is the only one allowed
        if (reqDef.relationship) {
            return InheritanceUtils.getDescendantsOfEntityType<VisualEntityType>(reqDef.relationship, this.entityTypes.relationshipTypes)
                .sort((a, b) => a.name.localeCompare(b.name));
        }
        // otherwise, all types are allowed
        return this.entityTypes.relationshipTypes;
    }

    isRequirementFulfilled(req: RequirementModel) {
        if (req.node) {
            // if the node value of the requirement model is a node type (rather than a node template), then it is not yet fulfilled.
            return !this.entityTypes.unGroupedNodeTypes.some(nt => nt.qName === req.node);
        }
        // this should never happen..
        return false;
    }

    /**
     * Gets triggered upon clicking on a capability or requirement type in the table, links to the defined type in the
     * management UI
     * @param reqOrCapType - the type
     */
    clickReqOrCapType(reqOrCapType: string) {
        let clickedType;
        if (this.tableType === this.tableTypes.Requirements) {
            clickedType = urlElement.RequirementTypeURL;
        } else {
            clickedType = urlElement.CapabilityTypeURL;
        }
        const url = this.backendService.configuration.uiURL
            + clickedType
            + encodeURIComponent(encodeURIComponent(this.getNamespace(reqOrCapType)))
            + '/' + this.getLocalName(reqOrCapType);
        window.open(url, '_blank');
    }

    passCurrentType($event): void {
        let currentType: string;

        try {
            currentType = $event.srcElement.innerText.replace(/\n/g, '').replace(/\s+/g, '');
        } catch (e) {
            currentType = $event.target.innerText.replace(/\n/g, '').replace(/\s+/g, '');
        }
        this.entityTypes.relationshipTypes.some(relType => {
            if (relType.qName.includes(currentType)) {
                this.reqCapRelationshipService.passCurrentType(relType);
                return true;
            }
        });
    }

    isYamlPolicyActiveForNode(policy: TPolicy): boolean {
        return policy.targets && policy.targets.some(target => target === this.currentNodeData.currentNodeId);
    }

    toggleYamlPolicy(policy: TPolicy) {
        if (policy.targets) {
            const index = policy.targets.indexOf(this.currentNodeData.currentNodeId);
            if (index >= 0) {
                policy.targets.splice(index, 1);
            } else {
                policy.targets.push(this.currentNodeData.currentNodeId);
            }
        } else {
            policy.targets = [this.currentNodeData.currentNodeId];
        }
    }

    clickArtifactFile(artifact: TArtifact) {
        if (artifact) {
            this.backendService.downloadYamlArtifactFile(this.currentNodeData.currentNodeId,
                artifact.id,
                artifact.file).subscribe(data => {
                const blob = new Blob([data.body], { type: 'application/octet-stream' });
                const url = window.URL.createObjectURL(blob);
                const anchor = document.createElement('a');
                anchor.download = artifact.file;
                anchor.href = url;
                anchor.click();
            });
        }
    }
}
