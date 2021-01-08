/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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
import { TRelationshipTemplate } from '../../models/ttopology-template';

export class DetailsSidebarState {
    visible: boolean;
    nodeClicked: boolean;
    template: SidebarEntityTemplate;
    relationshipTemplate?: TRelationshipTemplate;
    minInstances: number;
    // this shoehorns the possibility of unicode infinity into the type
    maxInstances: number | '\u221E';
    // relationship editing information
    source: any;
    target: any;
}

export class SidebarEntityTemplate {
    id: string;
    name: string;
    type: string;
    properties: any;
}
