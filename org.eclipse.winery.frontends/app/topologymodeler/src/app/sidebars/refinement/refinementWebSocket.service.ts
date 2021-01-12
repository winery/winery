/********************************************************************************
 * Copyright (c) 2018-2019 Contributors to the Eclipse Foundation
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
import { BehaviorSubject } from 'rxjs';
import { BackendService } from '../../services/backend.service';
import { TTopologyTemplate } from '../../models/ttopology-template';
import { AbstractRefinementWebSocketService, RefinementWebSocketData } from './abstractRefinementWebSocket.service';

export enum RefinementTasks {
    START = 'START',
    REFINE_WITH = 'REFINE_WITH',
    STOP = 'STOP'
}

export interface RefinementElement {
    refinementCandidates: PatternRefinementModel[];
    serviceTemplateContainingRefinements: {
        xmlId?: {
            decoded: string;
        };
        namespace?: {
            decoded: string
        };
    };
    currentTopology: TTopologyTemplate;
}

export interface PatternRefinementModel {
    id: number;
    nodeIdsToBeReplaced: string[];
    refinementModel: {
        name: string;
        targetNamespace: string;
    };
}

@Injectable()
export class RefinementWebSocketService extends AbstractRefinementWebSocketService<RefinementElement> {

    constructor(private bs: BackendService) {
        super(bs);
    }

    startRefinement(refinementType: string) {
        return this.startRefinementSocket('/refinetopology?type=' + refinementType);
    }


    refineWith(option: PatternRefinementModel) {
        const update: RefinementWebSocketData = {
            task: RefinementTasks.REFINE_WITH,
            refineWith: option.id
        };
        this.socket.send(JSON.stringify(update));
    }

}
