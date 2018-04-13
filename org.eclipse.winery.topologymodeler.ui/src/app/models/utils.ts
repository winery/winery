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
import { TNodeTemplate, Visuals } from './ttopology-template';
import { QName } from './qname';
import { isNullOrUndefined } from 'util';

export class Utils {

    public static createTNodeTemplateFromObject(node: TNodeTemplate, nodeVisuals: Visuals[], givenColor?: string): TNodeTemplate {
        let color;
        let imageUrl;
        for (const visual of nodeVisuals) {
            const qName = new QName(visual.nodeTypeId);
            const localName = qName.localName;
            if (localName === new QName(node.type).localName) {
                color = isNullOrUndefined(givenColor) ? visual.color : givenColor;
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
            node.policies
        );
    }
}
