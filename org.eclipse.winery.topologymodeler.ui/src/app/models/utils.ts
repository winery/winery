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
import { NodeVisualsModel } from './nodeVisualsModel';

export class Utils {

    static createTNodeTemplateFromObject(node: TNodeTemplate, nodeVisuals: Visuals[], state?: DifferenceStates): TNodeTemplate {
        const nodeVisualsObject = this.getNodeVisualsForNodeTemplate(node.type, nodeVisuals, state);
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
            properties ? properties : {},
            node.id,
            node.type,
            node.name,
            node.minInstances,
            node.maxInstances,
            nodeVisualsObject.color,
            nodeVisualsObject.imageUrl,
            node.documentation ? node.documentation : [],
            node.any ? node.any : [],
            otherAttributes,
            node.x,
            node.y,
            node.capabilities ? node.capabilities : {},
            node.requirements ? node.requirements : {},
            node.deploymentArtifacts ? node.deploymentArtifacts : {},
            node.policies ? node.policies : {},
            state
        );
    }

    static createTRelationshipTemplateFromObject(relationship: TRelationshipTemplate, relIdCount: number, state?: DifferenceStates) {
        return new TRelationshipTemplate(
            relationship.sourceElement,
            relationship.targetElement,
            relationship.name,
            'con_' + relIdCount.toString(),
            relationship.type,
            relationship.documentation,
            relationship.any,
            relationship.otherAttributes,
            state
        );
    }

    static getNodeVisualsForNodeTemplate(nodeType: string, nodeVisuals: Visuals[], state?: DifferenceStates): NodeVisualsModel {
        let color, imageUrl: string;
        for (const visual of nodeVisuals) {
            const qName = new QName(visual.nodeTypeId);
            const localName = qName.localName;
            if (localName === new QName(nodeType).localName) {
                color = isNullOrUndefined(state) ? visual.color : VersionUtils.getElementColorByDiffState(state);
                imageUrl = visual.imageUrl;
                if (imageUrl) {
                    imageUrl = imageUrl.replace('appearance', 'visualappearance');
                }
                const nodeVisualsObject = {
                  color: color,
                  imageUrl: imageUrl,
                };
                return nodeVisualsObject;
            }
        }
    }
}
