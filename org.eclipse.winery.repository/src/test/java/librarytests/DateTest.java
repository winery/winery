/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.librarytests;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Date parsing test, when the system locale is not ENGLISH
 */
public class DateTest {

    public static void main(String[] args) throws ParseException {
        // In case the following line is commented, this method throws a ParseException
        Locale.setDefault(Locale.ENGLISH);
        String modified = "Fri, 23 Mar 2012 11:04:56 GMT";
        Date modifiedDate = DateUtils.parseDate(modified, org.eclipse.winery.repository.DateUtils.DEFAULT_PATTERNS);
        System.out.println(modifiedDate);
    }

}
