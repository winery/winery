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

import { Component, ElementRef, EventEmitter, Input, OnChanges, OnDestroy, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { ModalDirective } from 'ngx-bootstrap';
import { TPolicy } from '../../models/policiesModalData';
import { TDeploymentArtifact } from '../../models/artifactsModalData';
import { QNameWithTypeApiData } from '../../models/generateArtifactApiData';
import { EntityTypesModel } from '../../models/entityTypesModel';
import { BackendService } from '../../services/backend.service';
import { IWineryState } from '../../redux/store/winery.store';
import { NgRedux } from '@angular-redux/store';
import { isNullOrUndefined } from 'util';
import { WineryActions } from '../../redux/actions/winery.actions';
import { ExistsService } from '../../services/exists.service';
import { ToastrService } from 'ngx-toastr';
import { DeploymentArtifactOrPolicyModalData, ModalVariant, ModalVariantAndState } from './modal-model';
import { EntitiesModalService, OpenModalEvent } from './entities-modal.service';
import { urlElement } from '../../models/enums';
import { TArtifact, TTopologyTemplate } from '../../models/ttopology-template';
import { WineryRepositoryConfigurationService } from '../../../../../tosca-management/src/app/wineryFeatureToggleModule/WineryRepositoryConfiguration.service';
import { Subscription } from 'rxjs';
import { QName } from '../../../../../shared/src/app/model/qName';

@Component({
    selector: 'winery-entities-modal',
    templateUrl: './entities-modal.component.html',
    styleUrls: ['./entities-modal.component.css']
})
export class EntitiesModalComponent implements OnInit, OnChanges, OnDestroy {

    @ViewChild('modal') public modal: ModalDirective;
    @ViewChild('fileUploader') fileUploader: ElementRef;

    @Input() modalVariantAndState: ModalVariantAndState;
    @Input() entityTypes: EntityTypesModel;
    @Input() currentNodeData: any;

    @Output() modalDataChange = new EventEmitter<ModalVariantAndState>();

    allNamespaces;
    defaultValue;

    /*    deploymentArtifactModalData: DeploymentArtifactsModalData;
        policiesModalData: PoliciesModalData;*/

    deploymentArtifactOrPolicyModalData: DeploymentArtifactOrPolicyModalData;
    modalSelectedRadioButton = '';
    artifactOrPolicyAlreadyExists: boolean;
    // artifactOrPolicy creation
    artifactOrPolicy: QNameWithTypeApiData = new QNameWithTypeApiData();
    artifactOrPolicyUrl: string;
    // needed for edit and delete tasks
    modalVariantForEditDeleteTasks = '(none)';

    // this is required for some reason
    ModalVariant = ModalVariant;
    private selectedYamlArtifactFile: File;
    private selectedYamlArtifactAllowedTypes = '';
    private currentTopologyTemplate: TTopologyTemplate;
    private subscriptions: Subscription[] = [];

    constructor(public backendService: BackendService,
                private ngRedux: NgRedux<IWineryState>,
                private actions: WineryActions,
                private existsService: ExistsService,
                private entitiesModalService: EntitiesModalService,
                private alert: ToastrService,
                private configurationService: WineryRepositoryConfigurationService) {
    }

    ngOnInit() {
        this.deploymentArtifactOrPolicyModalData = new DeploymentArtifactOrPolicyModalData();
        this.subscriptions.push(
            this.entitiesModalService.requestNamespaces()
                .subscribe(
                    data => {
                        this.allNamespaces = data;
                    },
                    error => this.alert.info((error.toString()))
                ));
        this.subscriptions.push(
            this.entitiesModalService.openModalEvent.subscribe((newEvent: OpenModalEvent) => {
                try {
                    this.deploymentArtifactOrPolicyModalData.artifactTypes = this.entityTypes.artifactTypes;
                    this.deploymentArtifactOrPolicyModalData.policyTypes = this.entityTypes.policyTypes;
                    this.deploymentArtifactOrPolicyModalData.artifactTemplates = this.entityTypes.artifactTemplates;
                    this.deploymentArtifactOrPolicyModalData.policyTemplates = this.entityTypes.policyTemplates;
                } catch (e) {
                    console.log(e);
                }
                this.deploymentArtifactOrPolicyModalData.nodeTemplateId = newEvent.currentNodeId;
                this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace = newEvent.modalTemplateNameSpace;
                this.deploymentArtifactOrPolicyModalData.modalTemplateName = newEvent.modalTemplateName;
                this.deploymentArtifactOrPolicyModalData.modalName = newEvent.modalName;
                this.deploymentArtifactOrPolicyModalData.modalType = newEvent.modalType;
                this.modalVariantForEditDeleteTasks = newEvent.modalVariant.toString();
                this.deploymentArtifactOrPolicyModalData.modalFileName = newEvent.modalFilePath;
                this.deploymentArtifactOrPolicyModalData.modalTargetLocation = newEvent.modalTargetLocation;
                this.modal.show();
            }));
        this.subscriptions.push(
            this.ngRedux.select(currentState => currentState.wineryState.currentJsonTopology)
                .subscribe(topologyTemplate => this.currentTopologyTemplate = topologyTemplate));
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    ngOnChanges(changes: SimpleChanges) {
        this.updateModal();
    }

    /**
     * Updates the modal state when needed
     */
    updateModal() {
        // Ths if statement sets the initial radio button state
        this.modalSelectedRadioButton = 'create';
        if (this.entityTypes !== undefined) {
            try {
                this.deploymentArtifactOrPolicyModalData.artifactTemplates = this.entityTypes.artifactTemplates;
                this.deploymentArtifactOrPolicyModalData.artifactTypes = this.entityTypes.artifactTypes;
                this.deploymentArtifactOrPolicyModalData.policyTemplates = this.entityTypes.policyTemplates;
                this.deploymentArtifactOrPolicyModalData.policyTypes = this.entityTypes.policyTypes;
                this.deploymentArtifactOrPolicyModalData.nodeTemplateId = this.currentNodeData.id;
            } catch (e) {
                // When artifactTemplates fail to load
                this.backendService.requestArtifactTemplates().subscribe((artifactTemplates) => {
                    this.deploymentArtifactOrPolicyModalData.artifactTemplates = artifactTemplates;
                });
            }
        }
        if (this.modalVariantAndState.modalVisible) {
            // show actual modal
            if (this.modal !== undefined) {
                this.modal.show();
            }
        }
    }

    /**
     * This method gets called when the add button is pressed inside the "Add Deployment Artifact" modal
     */
    addDeploymentArtifactOrPolicy() {
        this.artifactOrPolicy.localname = this.deploymentArtifactOrPolicyModalData.modalTemplateName;
        this.artifactOrPolicy.namespace = this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace;
        this.artifactOrPolicy.type = this.deploymentArtifactOrPolicyModalData.modalType;

        if (this.modalVariantAndState.modalVariant === ModalVariant.DeploymentArtifacts && this.configurationService.isYaml()) {
            // the name is retrieved from the open file dialog
            let yamlArtifact: TArtifact;
            if (this.deploymentArtifactOrPolicyModalData.isFileRemote) {
                yamlArtifact = new TArtifact(
                    this.deploymentArtifactOrPolicyModalData.modalName,
                    this.deploymentArtifactOrPolicyModalData.modalType,
                    this.deploymentArtifactOrPolicyModalData.selectedArtifactReference,
                    this.deploymentArtifactOrPolicyModalData.modalTargetLocation,
                    {},
                    [],
                    [],
                    {}
                );
                this.saveYamlArtifactsToModel(yamlArtifact);
                this.backendService.saveTopologyTemplate(this.currentTopologyTemplate).subscribe((res) => {
                    this.resetDeploymentArtifactOrPolicyModalData();
                    this.alert.success('Successfully Saved the Artifact!', 'Operation Successful');
                }, error => {
                    this.alert.info('<p>Something went wrong! The DA was not added to the Topology Template!<br>' + 'Error: '
                        + error + '</p>');
                });
            } else {
                this.deploymentArtifactOrPolicyModalData.modalFileName = this.selectedYamlArtifactFile.name;
                yamlArtifact = new TArtifact(
                    this.deploymentArtifactOrPolicyModalData.modalName,
                    this.deploymentArtifactOrPolicyModalData.modalType,
                    this.deploymentArtifactOrPolicyModalData.modalFileName,
                    this.deploymentArtifactOrPolicyModalData.modalTargetLocation,
                    {},
                    [],
                    [],
                    {}
                );
                this.saveYamlArtifactsToModel(yamlArtifact);
                this.backendService.saveYamlArtifact(
                    this.currentTopologyTemplate,
                    this.currentNodeData.id,
                    yamlArtifact.id,
                    this.selectedYamlArtifactFile,
                ).subscribe((res) => {
                    this.resetDeploymentArtifactOrPolicyModalData();
                    this.alert.success('Successfully Saved the Artifact!', 'Operation Successful');
                }, error => {
                    this.alert.info('<p>Something went wrong! The DA was not added to the Topology Template!<br>' + 'Error: '
                        + error + '</p>');
                });
            }
        } else if (this.modalSelectedRadioButton === 'create' && this.modalVariantAndState.modalVariant === ModalVariant.DeploymentArtifacts) {
            const deploymentArtifactToBeSavedToRedux: TDeploymentArtifact = new TDeploymentArtifact(
                [],
                [],
                {},
                this.deploymentArtifactOrPolicyModalData.modalName,
                this.deploymentArtifactOrPolicyModalData.modalType,
                this.deploymentArtifactOrPolicyModalData.modalTemplateRef
            );
            // POST to the backend
            this.backendService.createNewArtifactOrPolicy(this.artifactOrPolicy, 'artifact')
                .subscribe(res => {
                    if (res.ok === true) {
                        this.alert.success('<p>Saved the Deployment Artifact!<br>' + 'Response Status: '
                            + res.statusText + ' ' + res.status + '</p>');
                        // if saved successfully to backend, also add to topologyTemplate
                        this.saveDeploymentArtifactsToModel(deploymentArtifactToBeSavedToRedux);
                    } else {
                        this.alert.info('<p>Something went wrong! The DA was not added to the Topology Template!<br>' + 'Response Status: '
                            + res.statusText + ' ' + res.status + '</p>');
                    }
                    // get list of artifactTemplates to reflect latest change
                    this.backendService.requestArtifactTemplates().subscribe((artifactTemplates) => {
                        this.deploymentArtifactOrPolicyModalData.artifactTemplates = artifactTemplates;
                    });
                });
        } else if (this.modalSelectedRadioButton === 'create' && this.modalVariantAndState.modalVariant === ModalVariant.Policies) {
            const policyToBeSavedToRedux: TPolicy = new TPolicy(
                this.deploymentArtifactOrPolicyModalData.modalName,
                this.deploymentArtifactOrPolicyModalData.modalTemplateRef,
                this.deploymentArtifactOrPolicyModalData.modalType,
                [],
                [],
                {}
            );
            // POST to the backend
            this.backendService.createNewArtifactOrPolicy(this.artifactOrPolicy, 'policy')
                .subscribe(res => {
                    if (res.ok === true) {
                        this.alert.success('<p>Saved the Policy Template!<br>' + 'Response Status: '
                            + res.statusText + ' ' + res.status + '</p>');
                        // if saved successfully to backend, also add to topologyTemplate
                        this.savePoliciesToModel(policyToBeSavedToRedux);
                    } else {
                        this.alert.info('<p>Something went wrong! The Policy was not added to the Topology Template!<br>' + 'Response Status: '
                            + res.statusText + ' ' + res.status + '</p>');
                    }
                    // get list of policyTemplates to reflect latest change
                    this.backendService.requestPolicyTemplates().subscribe((policyTemplates) => {
                        this.deploymentArtifactOrPolicyModalData.policyTemplates = policyTemplates;
                    });
                });
        } else if (this.modalSelectedRadioButton === 'link' && this.modalVariantAndState.modalVariant === ModalVariant.DeploymentArtifacts) {
            // with artifactRef
            const deploymentArtifactToBeSavedToRedux: TDeploymentArtifact = new TDeploymentArtifact(
                [],
                [],
                {},
                this.deploymentArtifactOrPolicyModalData.modalName,
                this.deploymentArtifactOrPolicyModalData.modalType,
                this.deploymentArtifactOrPolicyModalData.modalTemplateRef
            );
            this.saveDeploymentArtifactsToModel(deploymentArtifactToBeSavedToRedux);
        } else if (this.modalSelectedRadioButton === 'skip' && this.modalVariantAndState.modalVariant === ModalVariant.DeploymentArtifacts) {
            // without artifactRef
            const deploymentArtifactToBeSavedToRedux: TDeploymentArtifact = new TDeploymentArtifact(
                [],
                [],
                {},
                this.deploymentArtifactOrPolicyModalData.modalName,
                this.deploymentArtifactOrPolicyModalData.modalType
            );
            this.saveDeploymentArtifactsToModel(deploymentArtifactToBeSavedToRedux);
        } else if (this.modalSelectedRadioButton === 'link' && this.modalVariantAndState.modalVariant === ModalVariant.Policies) {
            const policyToBeAddedToRedux: TPolicy = new TPolicy(
                this.deploymentArtifactOrPolicyModalData.modalName,
                this.deploymentArtifactOrPolicyModalData.modalTemplateRef,
                this.deploymentArtifactOrPolicyModalData.modalType,
                [],
                [],
                {});
            this.savePoliciesToModel(policyToBeAddedToRedux);
        } else if (this.modalSelectedRadioButton === 'skip' && this.modalVariantAndState.modalVariant === ModalVariant.Policies) {
            // without policyRef
            const policyToBeAddedToRedux: TPolicy = new TPolicy(
                this.deploymentArtifactOrPolicyModalData.modalName,
                null,
                this.deploymentArtifactOrPolicyModalData.modalType,
                [],
                [],
                {});
            this.savePoliciesToModel(policyToBeAddedToRedux);
        }

        this.resetModalData();
        this.modal.hide();
    }

    /**
     * Auto-completes other relevant values when a deployment-artifactOrPolicy or policy type has been selected in
     * the modal
     */
    onChangeArtifactTypeOrPolicyTypeInModal(artifactTypeOrPolicyType: any, variant: ModalVariant): void {
        let artifactTypesOrPolicyTypes: string;
        variant === ModalVariant.DeploymentArtifacts ? artifactTypesOrPolicyTypes = 'artifactTypes' : artifactTypesOrPolicyTypes = 'policyTypes';
        // change the ones affected
        this.entityTypes[artifactTypesOrPolicyTypes].some(currentlySelectedOne => {
            if (currentlySelectedOne.name === artifactTypeOrPolicyType) {
                this.deploymentArtifactOrPolicyModalData.id = currentlySelectedOne.id;
                this.deploymentArtifactOrPolicyModalData.modalName = currentlySelectedOne.id;
                this.deploymentArtifactOrPolicyModalData.modalType = artifactTypeOrPolicyType;
                return true;
            }
        });
    }

    /**
     * This is required to figure out which templateName and Ref have to be pushed to the redux state
     * @param template - either an artifactTemplate or a policyTemplate
     * @param modalVariant - describing the type of the modal
     */
    updatedTemplateToBeLinkedInModal(template, modalVariant: ModalVariant) {
        const templateObject: any = JSON.parse(template);
        const qNameWithTypeApiData = new QNameWithTypeApiData;
        qNameWithTypeApiData.localname = new QName(templateObject.qName).localName;
        qNameWithTypeApiData.namespace = new QName(templateObject.qName).nameSpace;
        this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace = templateObject.namespace;
        this.deploymentArtifactOrPolicyModalData.modalTemplateName = templateObject.name;
        this.deploymentArtifactOrPolicyModalData.modalTemplateRef = templateObject.qName;
        modalVariant === ModalVariant.DeploymentArtifacts ?
            this.backendService.requestArtifactTemplate(qNameWithTypeApiData).subscribe(
                (artifactTemplate) => {
                    this.deploymentArtifactOrPolicyModalData.modalType
                        = artifactTemplate.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].type;
                }) : console.log('Failed to GET this artifactTemplate from the backend');
        modalVariant === ModalVariant.Policies ?
            this.backendService.requestPolicyTemplate(qNameWithTypeApiData).subscribe(
                (policyTemplate) => {
                    this.deploymentArtifactOrPolicyModalData.modalType
                        = policyTemplate.serviceTemplateOrNodeTypeOrNodeTypeImplementation[0].type;
                }) : console.log('Failed to GET this policy from the backend');
        console.log(this.deploymentArtifactOrPolicyModalData);
    }

    checkIfArtifactOrPolicyAlreadyExists(event: any, changedField: string) {
        let url = '';
        if (changedField === 'templateName') {
            this.deploymentArtifactOrPolicyModalData.modalTemplateName = event.target.value;
        } else if (changedField === 'namespace') {
            this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace = event.target.value;
        }
        if (!isNullOrUndefined(this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace &&
            this.deploymentArtifactOrPolicyModalData.modalTemplateName)) {
            this.deploymentArtifactOrPolicyModalData.modalTemplateRef = '{' +
                this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace + '}' + this.deploymentArtifactOrPolicyModalData.modalTemplateName;

            if (this.modalVariantAndState.modalVariant === ModalVariant.Policies) {
                url = this.backendService.configuration.repositoryURL
                    + urlElement.PolicyTemplateURL
                    + encodeURIComponent(encodeURIComponent(this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace)) + '/'
                    + this.deploymentArtifactOrPolicyModalData.modalTemplateName + '/';

            } else {
                url = this.backendService.configuration.repositoryURL
                    + urlElement.ArtifactTemplateURL
                    + encodeURIComponent(encodeURIComponent(this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace)) + '/'
                    + this.deploymentArtifactOrPolicyModalData.modalTemplateName + '/';
            }

            this.existsService.check(url)
                .subscribe(
                    data => this.artifactOrPolicyAlreadyExists = true,
                    error => this.artifactOrPolicyAlreadyExists = false
                );
        }
    }

    resetDeploymentArtifactOrPolicyModalData(): void {
        this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace = '';
        this.deploymentArtifactOrPolicyModalData.modalTemplateName = '';
        this.deploymentArtifactOrPolicyModalData.modalName = '';
        this.deploymentArtifactOrPolicyModalData.modalType = '';
        this.deploymentArtifactOrPolicyModalData.modalFileName = '';
        this.deploymentArtifactOrPolicyModalData.modalTargetLocation = '';

        if (this.fileUploader && this.fileUploader.nativeElement) {
            this.fileUploader.nativeElement.value = '';
        }

        this.resetModalData();
        this.modal.hide();
    }

    /**
     * Saves a deployment artifacts template to the model and gets pushed into the Redux state of the application
     */
    saveDeploymentArtifactsToModel(deploymentArtifactToBeSavedToRedux: TDeploymentArtifact): void {
        const actionObject = {
            nodeId: this.currentNodeData.id,
            newDeploymentArtifact: deploymentArtifactToBeSavedToRedux
        };
        this.ngRedux.dispatch(this.actions.setDeploymentArtifact(actionObject));
        this.resetDeploymentArtifactOrPolicyModalData();
    }

    /**
     * Saves a yaml artifact to the model and gets pushed into the Redux state of the application
     */
    saveYamlArtifactsToModel(artifact: TArtifact): void {
        const actionObject = {
            nodeId: this.currentNodeData.id,
            newYamlArtifact: artifact
        };
        this.ngRedux.dispatch(this.actions.setYamlArtifact(actionObject));
    }

    /**
     * Saves a policy to the nodeTemplate model and gets pushed into the Redux state of the application
     */
    savePoliciesToModel(policyToBeSavedToRedux: TPolicy): void {
        const actionObject = {
            nodeId: this.currentNodeData.id,
            newPolicy: policyToBeSavedToRedux
        };
        if (this.deploymentArtifactOrPolicyModalData.nodeTemplateId.startsWith('con')) {
            this.ngRedux.dispatch(this.actions.setPolicyForRelationship(actionObject));
        } else {
            this.ngRedux.dispatch(this.actions.setPolicy(actionObject));
        }

        this.resetDeploymentArtifactOrPolicyModalData();
    }

    // util functions
    resetModalData() {
        // reset variant to none and hide
        this.deploymentArtifactOrPolicyModalData.modalName = undefined;
        this.deploymentArtifactOrPolicyModalData.modalTemplate = undefined;
        this.deploymentArtifactOrPolicyModalData.modalTemplateName = undefined;
        this.selectedYamlArtifactFile = undefined;
        this.selectedYamlArtifactAllowedTypes = '';
        this.modalVariantForEditDeleteTasks = '(none)';
        this.modalVariantAndState.modalVariant = ModalVariant.None;
        this.modalDataChange.emit(this.modalVariantAndState);
    }

    deleteDeploymentArtifactOrPolicy() {
        if (this.modalVariantForEditDeleteTasks === ModalVariant.Policies) {
            const actionObject = {
                nodeId: this.deploymentArtifactOrPolicyModalData.nodeTemplateId,
                deletedPolicy: this.deploymentArtifactOrPolicyModalData.modalName
            };
            this.ngRedux.dispatch(this.actions.deletePolicy(actionObject));
            this.resetDeploymentArtifactOrPolicyModalData();
        } else if (this.modalVariantForEditDeleteTasks === ModalVariant.DeploymentArtifacts) {
            const actionObject = {
                nodeId: this.deploymentArtifactOrPolicyModalData.nodeTemplateId,
                deletedDeploymentArtifact: this.deploymentArtifactOrPolicyModalData.modalName
            };
            this.ngRedux.dispatch(this.actions.deleteDeploymentArtifact(actionObject));
            this.resetDeploymentArtifactOrPolicyModalData();
        }

    }

    deleteYamlArtifact() {
        const actionObject = {
            nodeId: this.deploymentArtifactOrPolicyModalData.nodeTemplateId,
            deletedYamlArtifactId: this.deploymentArtifactOrPolicyModalData.modalName
        };
        this.ngRedux.dispatch(this.actions.deleteYamlArtifact(actionObject));
        this.resetDeploymentArtifactOrPolicyModalData();
    }

    getLocalName(qName?: string): string {
        const qNameVar = new QName(qName);
        return qNameVar.localName;
    }

    getNamespace(qName?: string): string {
        const qNameVar = new QName(qName);
        return qNameVar.nameSpace;
    }

    clickArtifactRef() {
        if (this.deploymentArtifactOrPolicyModalData.modalTemplateRef) {
            const artifactRef = this.deploymentArtifactOrPolicyModalData.modalTemplateRef;
            const url = this.backendService.configuration.repositoryURL
                + urlElement.ArtifactTemplateURL
                + encodeURIComponent(encodeURIComponent(this.getNamespace(artifactRef)))
                + '/' + this.getLocalName(artifactRef);
            window.open(url, '_blank');
        }
    }

    private makeArtifactUrl() {
        this.artifactOrPolicyUrl = this.backendService.configuration.repositoryURL
            + urlElement.ArtifactTemplateURL
            + encodeURIComponent(encodeURIComponent(this.deploymentArtifactOrPolicyModalData.modalTemplateNameSpace))
            + '/' + this.deploymentArtifactOrPolicyModalData.modalTemplateName + '/';
        // TODO: add upload ability "this.uploadUrl = this.artifactOrPolicyUrl + 'files/';"
    }

    yamlArtifactFileSelected(files: FileList) {
        if (files && files.length > 0) {
            this.selectedYamlArtifactFile = files[0];
        } else {
            this.selectedYamlArtifactFile = undefined;
        }
    }

    yamlArtifactTypeChanged() {
        this.selectedYamlArtifactAllowedTypes = '';

        if (this.deploymentArtifactOrPolicyModalData.modalType) {
            const selectedYamlArtifactType = this.entityTypes
                .artifactTypes
                .find(artiTyep => artiTyep.qName === this.deploymentArtifactOrPolicyModalData.modalType);
            if (selectedYamlArtifactType) {
                if (selectedYamlArtifactType.fileExtensions && selectedYamlArtifactType.fileExtensions.length > 0) {
                    this.selectedYamlArtifactAllowedTypes = selectedYamlArtifactType.fileExtensions.join(',');
                }
                if (selectedYamlArtifactType.mimeType) {
                    if (this.selectedYamlArtifactAllowedTypes.length > 0) {
                        this.selectedYamlArtifactAllowedTypes += ',';
                    }
                    this.selectedYamlArtifactAllowedTypes += selectedYamlArtifactType.mimeType;
                }
                // in this case we only allow referencing the artifact as URL
                if (selectedYamlArtifactType.qName === '{radon.artifacts}Repository') {
                    this.deploymentArtifactOrPolicyModalData.isRepositoryType = true;
                    this.deploymentArtifactOrPolicyModalData.isFileRemote = true;
                } else {
                    this.deploymentArtifactOrPolicyModalData.isRepositoryType = false;
                }
            }
        }
    }

    downloadYamlArtifactFile() {
        this.backendService.downloadYamlArtifactFile(this.deploymentArtifactOrPolicyModalData.nodeTemplateId,
            this.deploymentArtifactOrPolicyModalData.modalName,
            this.deploymentArtifactOrPolicyModalData.modalFileName).subscribe(data => {
            const blob = new Blob([data.body], { type: 'application/octet-stream' });
            const url = window.URL.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.download = this.deploymentArtifactOrPolicyModalData.modalFileName;
            anchor.href = url;
            anchor.click();
        });
    }
}
