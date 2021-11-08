/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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
 *******************************************************************************/

package org.eclipse.winery.crawler.chefcookbooks.kitchenparser;

import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

public final class KitchenUtilities {

    public static String correctPlatformName(String name) {
        if (name.indexOf("opensuse-leap") == 0) {
            return ChefDslConstants.OPENSUSELEAP + name.substring("opensuse-leap".length());
        } else if (name.indexOf("macos") == 0) {
            return "mac_os_x" + name.substring("macos".length());
        }
        return name;
    }

    public static boolean skipPlatform(String oldPlatform, String newPlatform) {
        if (newPlatform.indexOf("opensuseleap") == 0 && "opensuse".equals(oldPlatform)) {
            return true;
        } else if (newPlatform.indexOf("amazonlinux") == 0 && "amazon".equals(oldPlatform)) {
            return true;
        } else
            return false;
    }
}
