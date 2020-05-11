/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.repository.export;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.TMapImportDefinition;
import org.eclipse.winery.repository.export.entries.YAMLDefinitionsBasedCsarEntry;

public class YamlExportAdjustmentsBuilder {

    private YAMLDefinitionsBasedCsarEntry entry;
    private TServiceTemplate definitions;

    public YamlExportAdjustmentsBuilder(YAMLDefinitionsBasedCsarEntry entry) {
        this.entry = entry;
        this.definitions = this.entry.getDefinitions();
    }

    /**
     * removes imports of normative types
     */
    public YamlExportAdjustmentsBuilder removeNormativeTypeImports() {
        // prevents imports of TOSCA normative types
        for (TMapImportDefinition map : this.definitions.getImports()) {
            map.values().removeIf(val -> val.getNamespaceUri().startsWith("tosca"));
        }
        return this;
    }

    /**
     * changes key values in yaml to the display name
     *
     * FIXME: All node template references need to be updated with the display; atm not the case for relationship
     * references in requirements definitions
     */
    public YamlExportAdjustmentsBuilder setKeysToDisplayName() {
        if (this.definitions.getTopologyTemplate() != null) {
            Map<String, TNodeTemplate> newMap = new HashMap<>();
            Map<String, TNodeTemplate> oldMap = this.definitions.getTopologyTemplate().getNodeTemplates();
            for (String key : oldMap.keySet()) {
                newMap.put(oldMap.get(key).getMetadata().get("displayName"), oldMap.get(key));
            }
            this.definitions.getTopologyTemplate().setNodeTemplates(newMap);
        }
        return this;
    }

    public YamlExportAdjustmentsBuilder setMetadataName(DefinitionsChildId id) {
        if (id instanceof ServiceTemplateId) {
            definitions.getMetadata().add("name", id.getQName().getLocalPart());
        }
        return this;
    }

    public YAMLDefinitionsBasedCsarEntry build() {
        this.entry.setDefinitions(definitions);
        return entry;
    }
}
