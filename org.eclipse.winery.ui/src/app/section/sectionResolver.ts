import { Injectable }             from '@angular/core';
import { Router, Resolve, RouterStateSnapshot,
    ActivatedRouteSnapshot } from '@angular/router';

import { sections } from '../configuration';
import { isNullOrUndefined } from 'util';

@Injectable()
export class SectionResolver implements Resolve<String> {
    constructor(private router: Router) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): String {
        let section = sections[route.params['section']];

        if (!isNullOrUndefined(section)) {
            return section.toLower();
        } else { // id not found
            this.router.navigate(['/notfound']);
            return null;
        }
    }
}
