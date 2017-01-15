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
package org.eclipse.winery.repository.resources.entitytypes.properties;

import java.util.List;

import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.TEntityType;

/**
 * Collects data used by the JSP
 */
public class JSPData {

	// FIXME: this is a quick hack and provides a fixed list of available
	// property types only. This list has to be made dynamically updatable (and offer plugins to edit)
	// currently only http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#built-in-datatypes are supported
	private static final String[] availablePropertyTypes = {"xsd:string", "xsd:boolean", "xsd:decimal", "xsd:float", "xsd:anyURI", "xsd:QName"};

	private final PropertiesDefinitionResource propertiesDefinitionResource;
	private final WinerysPropertiesDefinition wpd;


	public JSPData(PropertiesDefinitionResource propertiesDefinitionResource, WinerysPropertiesDefinition wpd) {
		this.propertiesDefinitionResource = propertiesDefinitionResource;
		this.wpd = wpd;
	}

	public List<PropertyDefinitionKV> getPropertyDefinitionKVList() {
		// as this method is used by the JSP, we have to initialize the list and not provide a fake list
		// in other words: we are in the mode, where the user has chosen the winery property handling
		assert (this.getIsWineryKeyValueProperties());
		if (this.wpd.getPropertyDefinitionKVList() == null) {
			return java.util.Collections.emptyList();
		} else {
			return this.wpd.getPropertyDefinitionKVList();
		}
	}

	public Boolean getIsWineryKeyValueProperties() {
		return (this.wpd != null);
		// the jsp renders list data only if the list is existing
		// we could (somehow) also always keep the old list, but we opted for keeping the choice between the four options also in the XML (and not storing stale data)
		// in the case, the WPD is derived from XSD, the list is rendered nevertheless
	}

	public boolean getIsWineryKeyValuePropertiesDerivedFromXSD() {
		return ((this.wpd != null) && (this.wpd.getIsDerivedFromXSD() != null));
	}

	public String[] getAvailablePropertyTypes() {
		return JSPData.availablePropertyTypes;
	}

	public TEntityType getEntityType() {
		return this.propertiesDefinitionResource.getEntityType();
	}

	public String getElementName() {
		if (this.wpd == null) {
			return null;
		} else {
			return this.wpd.getElementName();
		}
	}

	public String getNamespace() {
		if (this.wpd == null) {
			return null;
		} else {
			return this.wpd.getNamespace();
		}
	}

}
