/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Pipe, PipeTransform } from '@angular/core';
import { ToscaTypes } from '../wineryInterfaces/enums';
import { Utils } from '../wineryUtils/utils';

@Pipe({
    name: 'toscaTypeToCamelCase'
})
export class ToscaTypeToCamelCase implements PipeTransform {
    transform(value: ToscaTypes, args: any[]): string {
        if (value) {
            let toscaString = Utils.getToscaTypeNameFromToscaType(value);
            toscaString = toscaString.replace(/ /g, '');
            return toscaString.charAt(0).toLowerCase() + toscaString.slice(1);
        } else {
            return '';
        }
    }
}
