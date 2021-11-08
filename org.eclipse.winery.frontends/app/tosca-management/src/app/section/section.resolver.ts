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
import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot} from '@angular/router';
import {SectionResolverData} from '../model/resolverData';
import {Utils} from '../wineryUtils/utils';
import {ToscaTypes} from '../model/enums';

@Injectable()
export class SectionResolver implements Resolve<SectionResolverData> {
    constructor(private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): SectionResolverData {
        const section = Utils.getToscaTypeFromString(route.url[0].path);
        const namespace = route.params['namespace'] ? decodeURIComponent(decodeURIComponent(route.params['namespace'])) : null;
        const xsdSchemaType: string = ToscaTypes.Imports ? decodeURIComponent(decodeURIComponent(route.params['xsdSchemaType'])) : null;

        return {section: section, namespace: namespace, path: route.url[0].path, xsdSchemaType: xsdSchemaType};
    }
}
