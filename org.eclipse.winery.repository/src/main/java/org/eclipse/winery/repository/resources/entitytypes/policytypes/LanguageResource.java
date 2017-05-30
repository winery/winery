package org.eclipse.winery.repository.resources.entitytypes.policytypes;

public class LanguageResource {

	private PolicyTypeResource policyTypeResource;


	public LanguageResource(PolicyTypeResource policyTypeResource) {
		this.policyTypeResource = policyTypeResource;
	}

	public String getLanguage() {
		return this.policyTypeResource.getPolicyType().getPolicyLanguage();
	}
}
