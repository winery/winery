/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { SectionResolverData } from '../wineryInterfaces/resolverData';
import { Utils } from '../wineryUtils/utils';
import { ToscaTypes } from '../wineryInterfaces/enums';

@Injectable()
export class SectionResolver implements Resolve<SectionResolverData> {
    constructor(private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): SectionResolverData {
        const section = Utils.getToscaTypeFromString(route.url[0].path);
        const namespace = route.params['namespace'] ? decodeURIComponent(decodeURIComponent(route.params['namespace'])) : null;
        const xsdSchemaType: string = ToscaTypes.Imports ? decodeURIComponent(decodeURIComponent(route.params['xsdSchemaType'])) : null;

        return { section: section, namespace: namespace, path: route.url[0].path, xsdSchemaType: xsdSchemaType };
    }
}
