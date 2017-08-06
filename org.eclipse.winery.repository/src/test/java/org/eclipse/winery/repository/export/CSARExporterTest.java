/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.winery.Logger;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.AbstractWineryWithRepositoryTest;
import org.eclipse.winery.repository.backend.Repository;

import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@Ignore("currently not working")
@RunWith(Parameterized.class)
public class CSARExporterTest extends AbstractWineryWithRepositoryTest {

	/**
	 * Test multiple branches with different commits and all instances of all TOSCAComponents 
	 */
	@Parameterized.Parameters
	public static Collection<Object[]> data() throws Exception {
		AbstractWineryWithRepositoryTest.init();
		Set<Object[]> res = new UnifiedSet<>();
		for (String commitId: Arrays.asList("black")) {
			setRevisionTo(commitId);
			for (Class<? extends TOSCAComponentId> idClass : new Class[]{
					ArtifactTypeId.class, ServiceTemplateId.class}) {
				Repository.INSTANCE.getAllTOSCAComponentIds(idClass).stream().sorted().forEach(id -> res.add(new Object[]{commitId, id}));
			}
		}
		return res;
	}

	private final String commitId;
	private TOSCAComponentId id;

	private ByteArrayOutputStream os;
	private InputStream is;
	
	public CSARExporterTest(String commitId, TOSCAComponentId id) {
		Logger.debug(this, "Debugging %s and %s", commitId, id);
		this.commitId = commitId;
		this.id = id;
	}
	
	@Before
	public void createOutputAndInputStream() throws Exception {
		setRevisionTo(commitId);
		CSARExporter exporter = new CSARExporter();
		os = new ByteArrayOutputStream();
		exporter.writeCSAR(id, os);
		is = new ByteArrayInputStream(os.toByteArray());
	}
	
	@Test
	public void csarIsNotZeroBytes() throws Exception {
		Assert.assertNotEquals(0, os.size());
	}

	@Test
	public void csarIsValidZip() throws Exception {
		try (ZipInputStream zis = new ZipInputStream(is)) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				Assert.assertNotNull(entry.getName());
			}
		}
	}

}
