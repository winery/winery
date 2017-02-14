import { Injectable }             from '@angular/core';
import {
    Router, Resolve, RouterStateSnapshot,
    ActivatedRouteSnapshot, ResolveData
} from '@angular/router';

import { sections } from '../configuration';
import { isNullOrUndefined } from 'util';

@Injectable()
export class SectionResolver implements Resolve<ResolveData> {
    constructor(private router: Router) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): ResolveData {
        let section = sections[route.params['section']];

        if (!isNullOrUndefined(section)) {
            return { section: section };
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
