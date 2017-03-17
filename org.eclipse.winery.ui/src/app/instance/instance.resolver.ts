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

import { Injectable }             from '@angular/core';
import {
    ActivatedRouteSnapshot, Resolve, ResolveData,
    Router, RouterStateSnapshot
} from '@angular/router';

import { isNullOrUndefined } from 'util';
import { sections } from '../configuration';
import { InstanceResolverData } from '../interfaces/resolverData';

@Injectable()
export class InstanceResolver implements Resolve<InstanceResolverData> {
    constructor(private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): InstanceResolverData {
        // TODO: get the instance from the server, only return it, when it's valid
        let section = sections[route.params['section']];
        let namespace = route.params['namespace'];
        let instanceId = route.params['instanceId'];

        if (!isNullOrUndefined(instanceId) && !isNullOrUndefined(namespace) && !isNullOrUndefined(section)) {
            return {
                section: section,
                namespace: decodeURIComponent(decodeURIComponent(namespace)),
                instanceId: instanceId,
                path: state.url,
            };
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
