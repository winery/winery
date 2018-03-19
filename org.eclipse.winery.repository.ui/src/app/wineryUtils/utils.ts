/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
 ********************************************************************************/
import { ServiceTemplateTemplateTypes, ToscaTypes } from '../wineryInterfaces/enums';

export class Utils {

    /**
     * Generates a random alphanumeric string of the given length.
     *
     * @param length The length of the generated string. Defaults to 64.
     * @returns A random, alphanumeric string.
     */
    public static generateRandomString(length = 64): string {
        const elements = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
        let state = '';

        for (let iterator = 0; iterator < length; iterator++) {
            state += elements.charAt(Math.floor(Math.random() * elements.length));
        }

        return state;
    }

    public static getToscaTypeFromString(value: string): ToscaTypes {
        switch (value.toLowerCase()) {
            case ToscaTypes.ServiceTemplate:
            case ToscaTypes.ServiceTemplate.toString().slice(0, -1):
                return ToscaTypes.ServiceTemplate;
            case ToscaTypes.NodeType:
            case ToscaTypes.NodeType.toString().slice(0, -1):
                return ToscaTypes.NodeType;
            case ToscaTypes.RelationshipType:
            case ToscaTypes.RelationshipType.toString().slice(0, -1):
                return ToscaTypes.RelationshipType;
            case ToscaTypes.ArtifactType:
            case ToscaTypes.ArtifactType.toString().slice(0, -1):
                return ToscaTypes.ArtifactType;
            case ToscaTypes.ArtifactTemplate:
            case ToscaTypes.ArtifactTemplate.toString().slice(0, -1):
                return ToscaTypes.ArtifactTemplate;
            case ToscaTypes.RequirementType:
            case ToscaTypes.RequirementType.toString().slice(0, -1):
                return ToscaTypes.RequirementType;
            case ToscaTypes.CapabilityType:
            case ToscaTypes.CapabilityType.toString().slice(0, -1):
                return ToscaTypes.CapabilityType;
            case ToscaTypes.NodeTypeImplementation:
            case ToscaTypes.NodeTypeImplementation.toString().slice(0, -1):
                return ToscaTypes.NodeTypeImplementation;
            case ToscaTypes.RelationshipTypeImplementation:
            case ToscaTypes.RelationshipTypeImplementation.toString().slice(0, -1):
                return ToscaTypes.RelationshipTypeImplementation;
            case ToscaTypes.PolicyType:
            case ToscaTypes.PolicyType.toString().slice(0, -1):
                return ToscaTypes.PolicyType;
            case ToscaTypes.PolicyTemplate:
            case ToscaTypes.PolicyTemplate.toString().slice(0, -1):
                return ToscaTypes.PolicyTemplate;
            case ToscaTypes.Imports:
            case ToscaTypes.Imports.toString().slice(0, -1):
                return ToscaTypes.Imports;
            case ToscaTypes.ComplianceRule:
                return ToscaTypes.ComplianceRule;
            default:
                return ToscaTypes.Admin;
        }
    }

    public static getToscaTypeNameFromToscaType(value: ToscaTypes, plural = false): string {
        let type: string;

        switch (value) {
            case ToscaTypes.ServiceTemplate:
                type = 'Service Template';
                break;
            case ToscaTypes.NodeType:
                type = 'Node Type';
                break;
            case ToscaTypes.RelationshipType:
                type = 'Relationship Type';
                break;
            case ToscaTypes.ArtifactType:
                type = 'Artifact Type';
                break;
            case ToscaTypes.ArtifactTemplate:
                type = 'Artifact Template';
                break;
            case ToscaTypes.RequirementType:
                type = 'Requirement Type';
                break;
            case ToscaTypes.CapabilityType:
                type = 'Capability Type';
                break;
            case ToscaTypes.NodeTypeImplementation:
                type = 'Node Type Implementation';
                break;
            case ToscaTypes.RelationshipTypeImplementation:
                type = 'Relationship Type Implementation';
                break;
            case ToscaTypes.PolicyType:
                type = 'Policy Type';
                break;
            case ToscaTypes.PolicyTemplate:
                type = 'Policy Template';
                break;
            case ToscaTypes.Imports:
                type = 'XSD Import';
                break;
            case ToscaTypes.ComplianceRule:
                type = 'Constraint Rule';
                break;
            default:
                type = 'Admin';
        }

        if (value !== ToscaTypes.Admin && plural) {
            type += 's';
        }

        return type;
    }

    public static getTypeOfTemplateOrImplementation(value: ToscaTypes): ToscaTypes {
        switch (value) {
            case ToscaTypes.ArtifactTemplate:
                return ToscaTypes.ArtifactType;
            case ToscaTypes.NodeTypeImplementation:
                return ToscaTypes.NodeType;
            case ToscaTypes.RelationshipTypeImplementation:
                return ToscaTypes.RelationshipType;
            case ToscaTypes.PolicyTemplate:
                return ToscaTypes.PolicyType;
            default:
                return null;
        }
    }

    public static getImplementationOrTemplateOfType(value: ToscaTypes): ToscaTypes {
        switch (value) {
            case ToscaTypes.NodeType:
                return ToscaTypes.NodeTypeImplementation;
            case ToscaTypes.RelationshipType:
                return ToscaTypes.RelationshipTypeImplementation;
            case ToscaTypes.PolicyType:
                return ToscaTypes.PolicyTemplate;
            case ToscaTypes.ArtifactType:
                return ToscaTypes.ArtifactTemplate;
            default:
                return null;
        }
    }

    public static getTypeOfServiceTemplateTemplate(value: ServiceTemplateTemplateTypes): ToscaTypes {
        switch (value) {
            case ServiceTemplateTemplateTypes.CapabilityTemplate:
                return ToscaTypes.CapabilityType;
            case ServiceTemplateTemplateTypes.NodeTemplate:
                return ToscaTypes.NodeType;
            case ServiceTemplateTemplateTypes.RelationshipTemplate:
                return ToscaTypes.RelationshipType;
            case ServiceTemplateTemplateTypes.RequirementTemplate:
                return ToscaTypes.RequirementType;
            default:
                return null;
        }
    }

    public static getServiceTemplateTemplateType(value: ToscaTypes): ServiceTemplateTemplateTypes {
        switch (value) {
            case ToscaTypes.CapabilityType:
                return ServiceTemplateTemplateTypes.CapabilityTemplate;
            case ToscaTypes.NodeType:
                return ServiceTemplateTemplateTypes.NodeTemplate;
            case ToscaTypes.RelationshipType:
                return ServiceTemplateTemplateTypes.RelationshipTemplate;
            case ToscaTypes.RequirementType:
                return ServiceTemplateTemplateTypes.RequirementTemplate;
            default:
                return null;
        }
    }

    public static getServiceTemplateTemplateFromString(value: string): ServiceTemplateTemplateTypes {
        switch (value) {
            case ServiceTemplateTemplateTypes.CapabilityTemplate:
                return ServiceTemplateTemplateTypes.CapabilityTemplate;
            case ServiceTemplateTemplateTypes.NodeTemplate:
                return ServiceTemplateTemplateTypes.NodeTemplate;
            case ServiceTemplateTemplateTypes.RelationshipTemplate:
                return ServiceTemplateTemplateTypes.RelationshipTemplate;
            case ServiceTemplateTemplateTypes.RequirementTemplate:
                return ServiceTemplateTemplateTypes.RequirementTemplate;
            default:
                return null;
        }
    }

    public static getNameFromQName(qName: string) {
        return qName.split('}').pop();
    }

    public static getNamespaceAndLocalNameFromQName(qname: string): WineryComponentNameAndNamespace {
        const i = qname.indexOf('}');
        return {
            namespace: qname.substr(1, i - 1),
            localName: qname.substr(i + 1)
        };
    }
}

export interface WineryComponentNameAndNamespace {
    namespace: string;
    localName: string;
}
