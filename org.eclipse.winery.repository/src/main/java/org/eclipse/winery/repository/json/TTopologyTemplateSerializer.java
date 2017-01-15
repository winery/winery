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
package org.eclipse.winery.repository.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class TTopologyTemplateSerializer extends JsonSerializer<TTopologyTemplate> {

	/**
	 * Does NOT wrap the result into an object. Assumes that the current
	 * position at jgen is in an object
	 *
	 * @param value the list of entity templates to serialize
	 */
	public void serialize(List<TEntityTemplate> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		List<TRelationshipTemplate> relationshipTemplates = new ArrayList<TRelationshipTemplate>();

		jgen.writeFieldName("nodeTemplates");
		jgen.writeStartObject();
		for (TEntityTemplate template : value) {
			if (template instanceof TNodeTemplate) {
				// write out as <id> : <default serialization>
				jgen.writeFieldName(template.getId());
				provider.defaultSerializeValue(template, jgen);

			} else {
				assert (template instanceof TRelationshipTemplate);
				relationshipTemplates.add((TRelationshipTemplate) template);
			}
		}
		jgen.writeEndObject();

		jgen.writeFieldName("relationshipTemplates");
		jgen.writeStartObject();
		for (TRelationshipTemplate template : relationshipTemplates) {
			// write out as <id> : <default serialization>
			jgen.writeFieldName(template.getId());
			provider.defaultSerializeValue(template, jgen);
		}
		jgen.writeEndObject();
	}

	@Override
	public void serialize(TTopologyTemplate topologyTemplate, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeStartObject();

		// write out the other fields unmodified
		jgen.writeFieldName("documentation");
		provider.defaultSerializeValue(topologyTemplate.getDocumentation(), jgen);
		jgen.writeFieldName("any");
		provider.defaultSerializeValue(topologyTemplate.getAny(), jgen);
		jgen.writeFieldName("otherAttributes");
		provider.defaultSerializeValue(topologyTemplate.getOtherAttributes(), jgen);

		// finally, write the topology template
		this.serialize(topologyTemplate.getNodeTemplateOrRelationshipTemplate(), jgen, provider);

		jgen.writeEndObject();
	}
}
