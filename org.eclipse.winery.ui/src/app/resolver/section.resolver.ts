/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 */

import { Injectable }             from '@angular/core';
import {
    Router, Resolve, RouterStateSnapshot,
    ActivatedRouteSnapshot, ResolveData
} from '@angular/router';

import { sections } from '../configuration';
import { isNullOrUndefined } from 'util';
import { ResolverData } from './resolverData';

@Injectable()
export class SectionResolver implements Resolve<ResolverData> {
    constructor(private router: Router) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ResolverData {
        let section = sections[route.params['section']];

        if (!isNullOrUndefined(section)) {
            return { section: section, path: route.params['section'] };
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
