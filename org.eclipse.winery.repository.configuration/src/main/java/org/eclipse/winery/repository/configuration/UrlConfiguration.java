package org.eclipse.winery.repository.configuration;

import org.apache.commons.configuration2.Configuration;

public class UrlConfiguration {

	private String repositoryApiUrl = "http://localhost:8080/winery";
	private String bpmn4ToscaModelerUrl = "http://localhost:8080/winery-topoloymodeler";
	private String topologyModelerUrl = "http://localhost:8080/winery-workflowmodeler";

	public UrlConfiguration() {
	}
	
	public UrlConfiguration(Configuration configuration) {
		this.setBpmn4ToscaModelerUrl(configuration.getString(Environment.KEY_URL_BPMN4TOSCA_MODELER, null));
		this.setTopologyModelerUrl(configuration.getString(Environment.KEY_URL_TOPOLOGY_MODELER, null));
	}

	public String getRepositoryApiUrl() {
		return repositoryApiUrl;
	}

	public void setRepositoryApiUrl(String repositoryApiUrl) {
		this.repositoryApiUrl = repositoryApiUrl;
	}

	public String getBpmn4ToscaModelerUrl() {
		return bpmn4ToscaModelerUrl;
	}

	public void setBpmn4ToscaModelerUrl(String bpmn4ToscaModelerUrl) {
		this.bpmn4ToscaModelerUrl = bpmn4ToscaModelerUrl;
	}

	/*
	 * @return the base URL of the BPMN4TOSCA plan modeler. NULL if not
	 *         configured. May also be empty.
	 */
	public String getTopologyModelerUrl() {
		return topologyModelerUrl;
	}

	public void setTopologyModelerUrl(String topologyModelerUrl) {
		this.topologyModelerUrl = topologyModelerUrl;
	}

	public void update(Configuration configuration) {
		configuration.setProperty(Environment.KEY_URL_BPMN4TOSCA_MODELER, this.getBpmn4ToscaModelerUrl());
		configuration.setProperty(Environment.KEY_URL_TOPOLOGY_MODELER, this.getTopologyModelerUrl());
	}
}
