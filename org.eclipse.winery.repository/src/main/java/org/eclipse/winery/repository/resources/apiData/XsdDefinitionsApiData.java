package org.eclipse.winery.repository.resources.apiData;

import com.fasterxml.jackson.databind.node.ArrayNode;

public class XsdDefinitionsApiData {

	public ArrayNode xsdDefinitions;

	public XsdDefinitionsApiData() {}

	public XsdDefinitionsApiData(ArrayNode xsdDefinitions) {
		this.xsdDefinitions = xsdDefinitions;
	}
}
