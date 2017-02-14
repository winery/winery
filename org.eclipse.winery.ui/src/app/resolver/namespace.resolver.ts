/**
 * Created by lharz on 14.02.2017.
 */
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
