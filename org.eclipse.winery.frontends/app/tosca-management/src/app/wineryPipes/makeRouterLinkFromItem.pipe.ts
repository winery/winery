/*******************************************************************************
 * Copyright (c) 20188 Contributors to the Eclipse Foundation
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
 *
 *******************************************************************************/
import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'makeRouterLinkFromItem'
})

export class MakeRouterLinkFromItem implements PipeTransform {
    transform(value: any, args: any[]): string {
        if (value) {
            if (value === './Selfservice Portal') {
                return 'SELFSERVICE-Metadata';
            } else {
                return value.toString().toLowerCase().replace(/ /g, '');
            }
        }
    }
}
