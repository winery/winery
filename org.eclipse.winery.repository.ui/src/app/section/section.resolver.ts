/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Niko Stadelmaier - initial API and implementation
 */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { isNullOrUndefined } from 'util';
import { sections } from '../configuration';
import { SectionResolverData } from '../wineryInterfaces/resolverData';

@Injectable()
export class SectionResolver implements Resolve<SectionResolverData> {
    constructor(private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): SectionResolverData {
        const section = sections[route.params['section']];
        const namespace = decodeURIComponent(decodeURIComponent(route.params['namespace']));

        if (!isNullOrUndefined(section)) {
            return {section: section, namespace: namespace, path: route.params['section']};
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
