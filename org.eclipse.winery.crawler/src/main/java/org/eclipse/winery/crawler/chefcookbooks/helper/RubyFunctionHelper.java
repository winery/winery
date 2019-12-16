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

package org.eclipse.winery.crawler.chefcookbooks.helper;

import org.eclipse.winery.crawler.chefcookbooks.constants.ChefDslConstants;

// This class contains methods to solve Ruby Functions in Java.
public class RubyFunctionHelper {

    /**
     * This method converts a String to an Integer. When string contains all non-digits are replaced with blanks.
     * Function is equal to the ruby Function "num.to_i"
     *
     * @param intString String to convert.
     * @return Returns the integer or null when conversion fails.
     */
    public static Integer stringToInt(String intString) {
        Integer result;
        String string;
        string = intString.replaceAll("\\..*$", "");
        string = string.replaceAll("\\D.*$", "");
        try {
            result = Integer.parseInt(string);
        } catch (NumberFormatException e) {
            if (ChefDslConstants.SUPPORTSALLPLATFORMVERSIONS.equals(intString)) {
                result = 0;
            } else {
                result = null;
            }
        }
        return result;
    }
}
