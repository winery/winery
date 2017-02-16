import { Injectable }             from '@angular/core';
import {
    Router, Resolve, RouterStateSnapshot,
    ActivatedRouteSnapshot, ResolveData
} from '@angular/router';

import { isNullOrUndefined } from 'util';
import { sections } from '../configuration';
import { ResolverData } from './resolverData';

@Injectable()
export class InstanceResolver implements Resolve<ResolverData> {
    constructor(private router: Router) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ResolverData {
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
