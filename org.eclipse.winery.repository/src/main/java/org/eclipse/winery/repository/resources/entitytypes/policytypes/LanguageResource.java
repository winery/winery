package org.eclipse.winery.repository.resources.entitytypes.policytypes;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.view.Viewable;

public class LanguageResource {
	
	private PolicyTypeResource policyTypeResource;
	
	
	public LanguageResource(PolicyTypeResource policyTypeResource) {
		this.policyTypeResource = policyTypeResource;
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Viewable getHTML() {
		return new Viewable("/jsp/entitytypes/policytypes/language.jsp", this);
	}
	
	public String getLanguage() {
		return this.policyTypeResource.getPolicyType().getPolicyLanguage();
	}
	
}
