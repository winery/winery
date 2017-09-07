/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.librarytests;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

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
