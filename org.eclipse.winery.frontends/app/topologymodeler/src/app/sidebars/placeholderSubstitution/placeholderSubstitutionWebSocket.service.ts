/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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
import {
    AbstractRefinementWebSocketService, RefinementTasks, RefinementWebSocketData
} from '../refinement/abstractRefinementWebSocket.service';
import { Injectable } from '@angular/core';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { BackendService } from '../../services/backend.service';

export interface SubstitutionElement {
    substitutionCandidates: PlaceholderSubstitutionCandidate[];
    serviceTemplateContainingSubstitution: {
        xmlId?: {
            decoded: string;
        };
        namespace?: {
            decoded: string
        };
    };
    currentTopology: TTopologyTemplate;
}

export interface PlaceholderSubstitutionCandidate {
    serviceTemplateQName: string;
    id: number;
}

@Injectable()
export class PlaceholderSubstitutionWebSocketService extends AbstractRefinementWebSocketService<SubstitutionElement> {

    constructor(bs: BackendService) {
        super(bs);
    }

    startPlaceholderSubstitution(subGraphDetector: TTopologyTemplate) {
        return this.startRefinementSocket('/substitutePlaceholder', subGraphDetector);
    }


    substituteWith(option: PlaceholderSubstitutionCandidate) {
        const update: RefinementWebSocketData = {
            task: RefinementTasks.REFINE_WITH,
            refineWith: option.id
        };
        this.socket.send(JSON.stringify(update));
    }

}
