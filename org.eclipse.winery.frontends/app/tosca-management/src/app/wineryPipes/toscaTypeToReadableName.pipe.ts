/*******************************************************************************
 * Copyright (c) 2017-2019 Contributors to the Eclipse Foundation
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
import {Pipe, PipeTransform} from '@angular/core';
import {ToscaTypes} from '../model/enums';
import {Utils} from '../wineryUtils/utils';

@Pipe({
    name: 'toscaTypeToReadableName'
})
export class ToscaTypeToReadableNamePipe implements PipeTransform {
    transform(value: ToscaTypes): string {
        if (value) {
            return Utils.getToscaTypeNameFromToscaType(value);
        } else {
            return '';
        }
    }
}
