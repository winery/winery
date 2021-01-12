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
    constructor(
        public visible: boolean,
        public nodeClicked?: boolean,
        public template?: SidebarEntityTemplate,
        public relationshipTemplate?: TRelationshipTemplate,
        public minInstances?: number,
        // this shoehorns the possibility of unicode infinity into the type
        public maxInstances?: number | '\u221E',
        // relationship editing information
        public source?: any,
        public target?: any) {

        if (!template) {
            this.template = {
                id: '',
                name: '',
                type: '',
                properties: {}
            };
        }

        if (!minInstances) {
            this.minInstances = -1;
        }
        if (!maxInstances) {
            this.maxInstances = -1;
        }
    }
}

export class SidebarEntityTemplate {
    id: string;
    name: string;
    type: string;
    properties: any;
}
