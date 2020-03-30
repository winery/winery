/*******************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.resources.servicetemplates.boundarydefinitions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.model.tosca.utils.ModelUtilities;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.rest.datatypes.TypeWithShortName;
import org.eclipse.winery.repository.rest.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.rest.resources.admin.types.ConstraintTypesManager;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.taglibs.standard.functions.Functions;

public class BoundaryDefinitionsJSPData {

    private final TServiceTemplate ste;
    private final TBoundaryDefinitions defs;
    private final URI baseURI;
    private final IRepository repository;

    /**
     * @param ste     the service template of the boundary definitions. Required to get a list of all plans
     * @param baseURI the base URI of the service. Requried for rendering the topology template for the selections
     * @param repository
     */
    public BoundaryDefinitionsJSPData(TServiceTemplate ste, URI baseURI, IRepository repository) {
        this.ste = ste;
        this.defs = ste.getBoundaryDefinitions();
        this.baseURI = baseURI;
        this.repository = repository;
    }

    private String getDefinedProperties() {
        Properties p = ModelUtilities.getProperties(this.defs);
        Object o = p.getAny();
        if (o == null) {
            // nothing stored -> return empty string
            return "";
        } else {
            // something stored --> return that
            assert o instanceof org.w3c.dom.Element;
            return BackendUtils.getXMLAsString(p.getAny(), repository);
        }
    }

    public String getPropertiesAsXMLString() {
        return this.getDefinedProperties();
    }

    /**
     * Helper method to return an initialized properties object only containing the user-defined properties. The TOSCA
     * properties-element is not returned as the TOSCA XSD allows a single element only
     */
    public String getDefinedPropertiesAsEscapedHTML() {
        String s = this.getDefinedProperties();
        s = StringEscapeUtils.escapeHtml4(s);
        return s;
    }

    public String getDefinedPropertiesAsJSONString() {
        String s = this.getDefinedProperties();
        s = StringEscapeUtils.escapeEcmaScript(s);
        return s;
    }

    public TBoundaryDefinitions getDefs() {
        return this.defs;
    }

    public String getBoundaryDefinitionsAsXMLStringEncoded() {
        String res = BackendUtils.getXMLAsString(this.defs, repository);
        return Functions.escapeXml(res);
    }

    public String getBoundaryDefinitionsAsXMLString() {
        String res = BackendUtils.getXMLAsString(this.defs, repository);
        return res;
    }

    public Collection<TypeWithShortName> getConstraintTypes() {
        return ConstraintTypesManager.INSTANCE.getTypes();
    }

    public Collection<QName> getAllPolicyTypes() {
        SortedSet<PolicyTypeId> allDefinitionsChildIds = RepositoryFactory.getRepository().getAllDefinitionsChildIds(PolicyTypeId.class);
        return BackendUtils.convertDefinitionsChildIdCollectionToQNameCollection(allDefinitionsChildIds);
    }

    public String getRepositoryURL() {
        return this.baseURI.toString();
    }

    public List<Select2DataItem> getlistOfAllPlans() {
        TPlans plans = this.ste.getPlans();
        if (plans == null) {
            return null;
        } else {
            List<Select2DataItem> res = new ArrayList<>(plans.getPlan().size());
            for (TPlan plan : plans.getPlan()) {
                String id = plan.getId();
                String name = ModelUtilities.getNameWithIdFallBack(plan);
                Select2DataItem di = new Select2DataItem(id, name);
                res.add(di);
            }
            return res;
        }
    }
}
