/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common;

import org.junit.Assert;
import org.junit.Test;

public class TestUtil {
	
	@Test
	public void testNamespaceToJavaPackageFullURL() {
		Assert.assertEquals("org.example.www.tosca.nodetypes", Util.namespaceToJavaPackage("http://www.example.org/tosca/nodetypes"));
	}
	
	@Test
	public void testNamespaceToJavaPackageURLWithHostOnly() {
		Assert.assertEquals("org.example.www", Util.namespaceToJavaPackage("http://www.example.org/"));
	}
	
	@Test
	public void testNamespaceToJavaPackageURLWithHostOnlyAndNoFinalSlash() {
		Assert.assertEquals("org.example.www", Util.namespaceToJavaPackage("http://www.example.org"));
	}
	
	@Test
	public void testNamespaceToJavaPackageURLWithNoHost() {
		Assert.assertEquals("plainNCname", Util.namespaceToJavaPackage("plainNCname"));
	}
	
	@Test
	public void testNCNameFromURL() {
		Assert.assertEquals("http___www.example.org", Util.makeNCName("http://www.example.org"));
	}
	
	@Test
	public void testNCNameFromNCName() {
		Assert.assertEquals("NCName", Util.makeNCName("NCName"));
	}
}
