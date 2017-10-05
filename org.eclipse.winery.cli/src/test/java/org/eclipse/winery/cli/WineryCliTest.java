package org.eclipse.winery.cli;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;

import org.junit.Assert;
import org.junit.Test;

public class WineryCliTest {

	private List<String> res = new ArrayList<>();
	
	@Test
	public void nodeTypeImplementationNamespace() throws Exception {
		NodeTypeImplementationId id = new NodeTypeImplementationId("http://winery.opentosca.org/test/nodetypeimplementations/fruits", "baobab_impl", false);
		WineryCli.checkNamespaceUri(res, EnumSet.of(WineryCli.Verbosity.NONE), id);
		Assert.assertEquals(Collections.emptyList(), res);
	}
}
