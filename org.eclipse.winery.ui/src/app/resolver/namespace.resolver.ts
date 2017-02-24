/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Lukas Harzentter - initial API and implementation
 *******************************************************************************/

import { Injectable }             from '@angular/core';
import {
    Router, Resolve, RouterStateSnapshot,
    ActivatedRouteSnapshot, ResolveData
} from '@angular/router';

import { isNullOrUndefined } from 'util';
import { sections } from '../configuration';

@Injectable()
export class NamespaceResolver implements Resolve<ResolveData> {
    constructor(private router: Router) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ResolveData {
        let section = sections[route.params['section']];
        let namespace = route.params['namespace'];
        // TODO: get the namespace from the server, only return it, when it's valid

        if (!isNullOrUndefined(namespace) && !isNullOrUndefined(section)) {
            return { section: section, namespace: namespace };
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
