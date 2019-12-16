/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ********************************************************************************/
/**
 * This class is used to determine the modal's size
 */
export class WineryModalSize {

    static readonly SMALL = 'modal-sm';
    static readonly LARGE = 'modal-lg';

    static validSize(size: string) {
        return size && (size === this.SMALL || size === this.LARGE);
    }

}
