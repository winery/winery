package org.eclipse.winery.repository;

import org.eclipse.winery.repository.resources.MainResource;
import org.restdoc.jersey.server.RestDocFeature;

public class RestDocFilter extends RestDocFeature {
	
	@Override
	protected Class<?>[] getClasses() {
		Class<?>[] res = {MainResource.class};
		return res;
	}
	
}
