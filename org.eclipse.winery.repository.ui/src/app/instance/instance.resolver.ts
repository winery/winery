/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Utils } from '../wineryUtils/utils';
import { ToscaComponent } from '../wineryInterfaces/toscaComponent';

@Injectable()
export class InstanceResolver implements Resolve<ToscaComponent> {

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ToscaComponent {
        const section = Utils.getToscaTypeFromString(route.url[0].path);
        const namespace = route.params['namespace'];
        const localName = route.params['localName'];
        const xsdSchemaType = route.params['xsdSchemaType'] ? route.params['xsdSchemaType'] : null;

        return new ToscaComponent(section, decodeURIComponent(decodeURIComponent(namespace)), localName, xsdSchemaType);
    }
}
