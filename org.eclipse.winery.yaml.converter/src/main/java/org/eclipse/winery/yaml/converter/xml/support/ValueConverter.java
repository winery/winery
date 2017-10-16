/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.converter.xml.support;

public class ValueConverter {
	public static ValueConverter INSTANCE = new ValueConverter();

	public String convert(String value) {
		if (value.matches("get_input:.*")) return "{ ".concat(value.trim()).concat(" }");
		return value;
	}

	public String convert(Object value) {
		return convert(value.toString());
	}
}
