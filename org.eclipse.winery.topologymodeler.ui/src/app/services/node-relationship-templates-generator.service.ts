/*******************************************************************************
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
 *******************************************************************************/

import { Injectable } from '@angular/core';
import { Utils } from '../models/utils';
import { TNodeTemplate, TRelationshipTemplate, Visuals } from '../models/ttopology-template';

@Injectable()
export class NodeRelationshipTemplatesGeneratorService {

    constructor() {
    }

    generateNodeAndRelationshipTemplates(nodeTemplateArray: Array<TNodeTemplate>, relationshipTemplateArray: Array<TRelationshipTemplate>,
                                         nodeVisuals: Visuals[]): Array<any> {
        const nodeTemplates: Array<TNodeTemplate> = [];
        const relationshipTemplates: Array<TRelationshipTemplate> = [];

        // init node templates
        if (nodeTemplateArray.length > 0) {
            nodeTemplateArray.forEach(node => {
                nodeTemplates.push(Utils.createTNodeTemplateFromObject(node, nodeVisuals));
            });
        }
        // init relationship templates
        if (relationshipTemplateArray.length > 0) {
            relationshipTemplateArray.forEach(relationship => {
                const relationshipType = relationship.type;
                relationshipTemplates.push(
                    new TRelationshipTemplate(
                        relationship.sourceElement,
                        relationship.targetElement,
                        relationship.name,
                        `${relationship.sourceElement.ref}_${relationshipType.substring(relationshipType.indexOf('}') + 1)}_${relationship.targetElement.ref}`,
                        relationshipType,
                        relationship.documentation,
                        relationship.any,
                        relationship.otherAttributes
                    )
                );
            });
        }
        const nodeAndRelationshipTemplates = [];
        nodeAndRelationshipTemplates.push(nodeTemplates);
        nodeAndRelationshipTemplates.push(relationshipTemplates);
        return nodeAndRelationshipTemplates;
    }
}
