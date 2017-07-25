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
 *     Niko Stadelmaier - add admin component
 */
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { isNullOrUndefined } from 'util';
import { sections } from '../configuration';
import { InstanceResolverData } from '../wineryInterfaces/resolverData';

@Injectable()
export class InstanceResolver implements Resolve<InstanceResolverData> {
    constructor(private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): InstanceResolverData {
        const section = sections[route.params['section']];
        const namespace = route.params['namespace'];
        const instanceId = route.params['instanceId'];

        if (!isNullOrUndefined(instanceId) && !isNullOrUndefined(namespace) && !isNullOrUndefined(section)) {
            return {
                section: section,
                namespace: decodeURIComponent(decodeURIComponent(namespace)),
                instanceId: instanceId,
                path: state.url,
            };
        } else { // id not found, no section ,check if admin
            if (isNullOrUndefined(section) && state.url.match('/admin')) {

                return {
                    section: 'admin',
                    namespace: isNullOrUndefined(state.url.split('/')[2]) ? state.url.split('/')[2] : '',
                    instanceId: '',
                    path: state.url
                };
                // no id and not admin
            } else {
                this.router.navigate(['/notfound']);
                return null;
            }
        }
    }
}
