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

import { Injectable } from '@angular/core';
import { isNullOrUndefined } from 'util';

declare const jsPlumb: any;

/**
 * Defines the JSPlumb instance which is used over the complete project.
 */
@Injectable()
export class JsPlumbService {
    jsPlumbInstance: any;

    getJsPlumbInstance(): any {
        jsPlumb.ready(() => {
        });
        if (isNullOrUndefined(this.jsPlumbInstance)) {
            this.jsPlumbInstance = jsPlumb.getInstance({
                PaintStyle: {
                    strokeWidth: 1,
                    stroke: '#212121',
                },
                Connector: ['Flowchart'],
                Endpoint: 'Blank',
                connectorOverlays: [
                    ['Arrow', { location: 1 }],
                ],
                ConnectionsDetachable: false,
                Anchor: 'Continuous'
            });
        }
        return this.jsPlumbInstance;
    }
}
