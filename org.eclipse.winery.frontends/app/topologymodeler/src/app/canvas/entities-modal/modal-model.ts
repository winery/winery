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
import { Entity, EntityType } from '../../models/ttopology-template';

export class ModalVariantAndState {
    modalVariant: ModalVariant;
    modalVisible: boolean;
    modalTitle: string;
}

export enum ModalVariant {
    Policies = 'policies',
    DeploymentArtifacts = 'deployment_artifacts',
    // Other as placeholder for other modals like the Requirement and Capability Modals
    Other = 'other',
    None = 'none'
}

/**
 * Encompasses the variety of values that are displayed inside and entered into the modal.
 */
export class DeploymentArtifactOrPolicyModalData {
    constructor(
        // id of the nodeTemplate the actions are performed on
        public nodeTemplateId?: string,
        // the id of
        public id?: string,
        // value of the name text field in the modal
        public modalName?: string,
        // value of the type dropdown in the modal
        public modalType?: string,
        // all DA types
        public artifactTypes?: EntityType[],
        // all policyTypes
        public policyTypes?: EntityType[],
        // the selected artifactTemplate or policyTemplate
        public modalTemplate?: any,
        // all artifactTemplates
        public artifactTemplates?: Entity[],
        // all policyTemplates
        public policyTemplates?: Entity[],
        // name of the selected artifactTemplate or policyTemplate
        public modalTemplateName?: any,
        // ref of the selected artifactTemplate or policyTemplate
        public modalTemplateRef?: any,
        // the selected namespace inside the modal
        public modalTemplateNameSpace?: any,
        // all namespaces
        public namespaces?: string[],
        // the file path of a yaml artifact
        public modalFileName?: string,
        // the deployment path of a yaml artifact
        public modalTargetLocation?: string,
        // yaml artifact provided as a reference
        public isFileRemote?: boolean,
        // file reference
        public selectedArtifactReference?: string
    ) {
    }
}
