/**
 * 
 */
package org.eclipse.winery.repository.rest.resources._support;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.winery.repository.rest.RestUtils;
import org.eclipse.winery.repository.rest.resources.apiData.InheritanceResourceApiData;
import org.eclipse.winery.repository.rest.resources.servicetemplates.ServiceTemplateResource;

/**
 * @author kalmankepes
 *
 */
public class ExtendedInheritanceResource {

	private ServiceTemplateResource managedResource;

	public ExtendedInheritanceResource(ServiceTemplateResource res) {
		this.managedResource = res;
	}

	public String getDerivedFrom() {
		return this.managedResource.getServiceTemplate().getDerivedFrom();
	}

	/**
	 * Produces a JSON object containing all necessary data for displaying and
	 * editing the inheritance.
	 *
	 * @return JSON object in the format { "isAbstract": "no", "isFinal": "yes",
	 *         "derivedFrom": "[QName]" }
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public InheritanceResourceApiData getInheritanceManagementJSON() {
		return new InheritanceResourceApiData(this.managedResource);
	}

	/**
	 * Saves the inheritance management from a putted json object in the format: {
	 * "isAbstract": "no", "isFinal": "yes", "derivedFrom": "[QName]" }
	 *
	 * @param json
	 *            Should at least contain values for abstract, final and QName.
	 * @return Response
	 */
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveInheritanceManagementFromJSON(InheritanceResourceApiData json) {
		this.managedResource.getServiceTemplate().setAbstract(json.isAbstract);
		this.managedResource.getServiceTemplate().setDerivedFrom(json.derivedFrom);
		this.managedResource.getServiceTemplate().setFinal(json.isFinal);

		return RestUtils.persist(managedResource);
	}

}
