/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzenetter - initial API and implementation
 */
import { isNullOrUndefined } from 'util';
import { ToscaTypes } from './enums';

export class ToscaComponent {

    readonly path: string;

    constructor(public readonly toscaType: ToscaTypes,
                public readonly namespace: string,
                public readonly localName: string) {
        this.path = '/' + this.toscaType;
        if (!isNullOrUndefined(this.namespace)) {
            this.path += '/' + encodeURIComponent(encodeURIComponent(this.namespace));
            if (!isNullOrUndefined(this.localName)) {
                this.path += '/' + this.localName;
            }
        }
    }
}
