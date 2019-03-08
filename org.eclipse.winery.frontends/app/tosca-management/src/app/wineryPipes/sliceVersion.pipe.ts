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
import {Pipe, PipeTransform} from '@angular/core';
import {WineryVersion} from '../model/wineryVersion';

@Pipe({
    name: 'sliceVersion'
})
export class SliceVersionPipe implements PipeTransform {
    transform(value: string, ...args: any[]): any {
        if (args[0] === true) {
            return value.split(WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR)[0];
        } else {
            return value;
        }
    }
}
