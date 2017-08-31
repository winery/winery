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
import { InstanceResolverData } from '../wineryInterfaces/resolverData';
import { Utils } from '../wineryUtils/utils';
import { ToscaComponent } from '../wineryInterfaces/toscaComponent';

@Injectable()
export class InstanceResolver implements Resolve<ToscaComponent> {
    constructor(private router: Router) {
    }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ToscaComponent {
        const section = Utils.getToscaTypeFromString(route.url[0].path);
        const namespace = route.params['namespace'];
        const localName = route.params['localName'];

        return new ToscaComponent(section, decodeURIComponent(decodeURIComponent(namespace)), localName);
    }
}
