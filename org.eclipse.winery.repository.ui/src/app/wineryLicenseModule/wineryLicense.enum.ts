/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */

import * as APL2 from './licenses/Apache-2.0';
import * as GPL3 from './licenses/GPL-3.0';
import * as MIT from './licenses/MIT';
import * as BSD2 from './licenses/BSD-2-Clause';
import * as BSD3 from './licenses/BSD-3-Clause';
import * as EPL1 from './licenses/EPL-1.0';
import * as EPL2 from './licenses/EPL-1.0';
import * as AGPL3 from './licenses/AGPL-3.0';
import * as GPL2 from './licenses/GPL-2.0';
import * as LGPL2_1 from './licenses/LGPL-2.1';
import * as LGPL3 from './licenses/LGPL-3.0';
import * as MPL2 from './licenses/MPL-2.0';
import * as Unlicense from './licenses/Unlicense';

export enum LicenseEnum {
    None       = 'None',
    APL20      = 'Apache License 2.0',
    GPL30      = 'GNU General Public License v3.0',
    MIT        = 'MIT License',
    BSD2Clause = 'BSD 2-clause "Simplified" License',
    BSD3Clause = 'BSD 3-clause "New" or "Revised" License',
    EPL10      = 'Eclipse Public License 1.0',
    EPL20      = 'Eclipse Public License 2.0',
    AGPL30     = 'GNU Affero General Public License v3.0',
    GPL20      = 'GNU General Public License v2.0',
    LGPL21     = 'GNU Lesser General Public License v2.1',
    LGPL30     = 'GNU Lesser General Public License v3.0',
    MPL20      = 'Mozilla Public License 2.0',
    Unlicense  = 'The Unlicense',
}

export class WineryLicense {

    public static getLicense(license: any) {
        let licenseText = '';

        switch (license) {
            case LicenseEnum.None:
                licenseText = '';
                break;
            case LicenseEnum.APL20:
                licenseText = APL2.license;
                break;
            case LicenseEnum.GPL30:
                licenseText = GPL3.license;
                break;
            case LicenseEnum.MIT:
                licenseText = MIT.license;
                break;
            case LicenseEnum.BSD2Clause:
                licenseText = BSD2.license;
                break;
            case LicenseEnum.BSD3Clause:
                licenseText = BSD3.license;
                break;
            case LicenseEnum.EPL10:
                licenseText = EPL1.license;
                break;
            case LicenseEnum.EPL20:
                licenseText = EPL2.license;
                break;
            case LicenseEnum.AGPL30:
                licenseText = AGPL3.license;
                break;
            case LicenseEnum.GPL20:
                licenseText = GPL2.license;
                break;
            case LicenseEnum.LGPL21:
                licenseText = LGPL2_1.license;
                break;
            case LicenseEnum.LGPL30:
                licenseText = LGPL3.license;
                break;
            case LicenseEnum.MPL20:
                licenseText = MPL2.license;
                break;
            case LicenseEnum.Unlicense:
                licenseText = Unlicense.license;
                break;
            default:
                licenseText = 'error no valid license type';
                break;
        }
        return licenseText;
    }

}
