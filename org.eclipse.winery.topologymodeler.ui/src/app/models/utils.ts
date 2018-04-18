/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
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
import { TNodeTemplate, TRelationshipTemplate, Visuals } from './ttopology-template';
import { QName } from './qname';
import { isNullOrUndefined } from 'util';
import { DifferenceStates, VersionUtils } from './ToscaDiff';

export class Utils {

    static createTNodeTemplateFromObject(node: TNodeTemplate, nodeVisuals: Visuals[], state?: DifferenceStates): TNodeTemplate {
        let color;
        let imageUrl;
        for (const visual of nodeVisuals) {
            const qName = new QName(visual.nodeTypeId);
            const localName = qName.localName;
            if (localName === new QName(node.type).localName) {
                color = isNullOrUndefined(state) ? visual.color : VersionUtils.getElementColorByDiffState(state);
                imageUrl = visual.imageUrl;
                if (imageUrl) {
                    imageUrl = imageUrl.replace('appearance', 'visualappearance');
                }
                break;
            }
        }
        let properties;
        if (node.properties) {
            properties = node.properties;
        }

        let nameSpace: string;
        let targetLocationKey: string;
        let otherAttributes;
        for (const key in node.otherAttributes) {
            if (node.otherAttributes.hasOwnProperty(key)) {
                nameSpace = key.substring(key.indexOf('{'), key.indexOf('}') + 1);
                if (nameSpace) {
                    if (key.substring(key.indexOf('}') + 1) === 'location') {
                        targetLocationKey = key;
                    }
                    otherAttributes = {
                        [nameSpace + 'location']: node.otherAttributes[targetLocationKey],
                        [nameSpace + 'x']: node.x,
                        [nameSpace + 'y']: node.y
                    };
                    break;
                } else if (key === 'location') {
                    targetLocationKey = 'location';
                }
            }
        }

        return new TNodeTemplate(
            properties,
            node.id,
            node.type,
            node.name,
            node.minInstances,
            node.maxInstances,
            color,
            imageUrl,
            node.documentation,
            node.any,
            otherAttributes,
            node.x,
            node.y,
            node.capabilities,
            node.requirements,
            node.deploymentArtifacts,
            node.policies,
            state
        );
    }

    static createTRelationshipTemplateFromObject(relationship: TRelationshipTemplate, state?: DifferenceStates) {
        return new TRelationshipTemplate(
            relationship.sourceElement,
            relationship.targetElement,
            relationship.name,
            `${relationship.sourceElement.ref}_${relationship.type.substring(relationship.type.indexOf('}') + 1)}_${relationship.targetElement.ref}`,
            relationship.type,
            relationship.documentation,
            relationship.any,
            relationship.otherAttributes,
            state
        );
    }
}
