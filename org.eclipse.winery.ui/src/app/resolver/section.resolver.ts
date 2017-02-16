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
