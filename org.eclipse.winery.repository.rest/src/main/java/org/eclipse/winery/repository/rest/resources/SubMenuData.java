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
package org.eclipse.winery.repository.rest.resources;

/**
 * Data used to render a submenu item
 */
public class SubMenuData {

	public static final SubMenuData SUBMENU_DOCUMENTATION = new SubMenuData("#documentation", "Documentation");
	public static final SubMenuData SUBMENU_XML = new SubMenuData("#xml", "XML");

	private final String href;
	private final String text;

	public SubMenuData(String href, String text) {
		this.href = href;
		this.text = text;
	}

	public String getHref() {
		return this.href;
	}

	public String getText() {
		return this.text;
	}
}
