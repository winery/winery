/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { ToscaTypes } from '../wineryInterfaces/enums';

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
        switch (value) {
            case ToscaTypes.ServiceTemplate:
                return ToscaTypes.ServiceTemplate;
            case ToscaTypes.NodeType:
                return ToscaTypes.NodeType;
            case ToscaTypes.RelationshipType:
                return ToscaTypes.RelationshipType;
            case ToscaTypes.ArtifactType:
                return ToscaTypes.ArtifactType;
            case ToscaTypes.ArtifactTemplate:
                return ToscaTypes.ArtifactTemplate;
            case ToscaTypes.RequirementType:
                return ToscaTypes.RequirementType;
            case ToscaTypes.CapabilityType:
                return ToscaTypes.CapabilityType;
            case ToscaTypes.NodeTypeImplementation:
                return ToscaTypes.NodeTypeImplementation;
            case ToscaTypes.RelationshipTypeImplementation:
                return ToscaTypes.RelationshipTypeImplementation;
            case ToscaTypes.PolicyType:
                return ToscaTypes.PolicyType;
            case ToscaTypes.PolicyTemplate:
                return ToscaTypes.PolicyTemplate;
            case ToscaTypes.Imports:
                return ToscaTypes.Imports;
            default:
                return ToscaTypes.Admin;
        }
    }

    public static getToscaTypeNameFromToscaType(value: ToscaTypes): string {
        switch (value) {
            case ToscaTypes.ServiceTemplate:
                return 'Service Template';
            case ToscaTypes.NodeType:
                return 'Node Type';
            case ToscaTypes.RelationshipType:
                return 'Relationship Type';
            case ToscaTypes.ArtifactType:
                return 'Artifact Type';
            case ToscaTypes.ArtifactTemplate:
                return 'Artifact Template';
            case ToscaTypes.RequirementType:
                return 'Requirement Type';
            case ToscaTypes.CapabilityType:
                return 'Capability Type';
            case ToscaTypes.NodeTypeImplementation:
                return 'Node Type Implementation';
            case ToscaTypes.RelationshipTypeImplementation:
                return 'Relationship Type Implementation';
            case ToscaTypes.PolicyType:
                return 'Policy Type';
            case ToscaTypes.PolicyTemplate:
                return 'Policy Template';
            case ToscaTypes.Imports:
                return 'XSD Imports';
            default:
                return 'Admin';
        }
    }

    public static getTypeOrImplementationOf(value: ToscaTypes): ToscaTypes {
        switch (value) {
            case ToscaTypes.ArtifactTemplate:
                return ToscaTypes.ArtifactType;
            case ToscaTypes.NodeTypeImplementation:
                return ToscaTypes.NodeType;
            case ToscaTypes.RelationshipTypeImplementation:
                return ToscaTypes.RelationshipType;
            case ToscaTypes.PolicyTemplate:
                return ToscaTypes.PolicyType;
        }
    }

    public static getToscaOfTypeOrImplementation(value: ToscaTypes): ToscaTypes {
        switch (value) {
            case ToscaTypes.NodeType:
                return ToscaTypes.NodeTypeImplementation;
            case ToscaTypes.RelationshipType:
                return ToscaTypes.RelationshipTypeImplementation;
            case ToscaTypes.PolicyType:
                return ToscaTypes.PolicyTemplate;
            case ToscaTypes.ArtifactType:
                return ToscaTypes.ArtifactTemplate;
        }
    }
}
