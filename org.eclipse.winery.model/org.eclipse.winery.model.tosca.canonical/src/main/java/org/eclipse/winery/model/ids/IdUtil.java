/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.ids;

import javax.xml.XMLConstants;

import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.model.ids.definitions.imports.WsdlImportId;
import org.eclipse.winery.model.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.model.ids.elements.ToscaElementId;

/**
 * Helper methods for Winery's Id system
 */
public class IdUtil {

    /**
     * Returns the namespace where the given Id is nested in. As the id is not a
     * DefinitionsChildId, it cannot be directly asked for its parent. Merely, the
     * parent has to be asked for its namespace. The parent, in turn, if it is
     * no DefinitionsChildId has to ask his parent.
     *
     * @param id the id refering to an element, where the namespace has to be
     *           checked for
     * @return the namespace of the element denoted by id
     */
    public static Namespace getNamespace(GenericId id) {
        if (id instanceof DefinitionsChildId) {
            return ((DefinitionsChildId) id).getNamespace();
        } else {
            return IdUtil.getNamespace(id.getParent());
        }
    }

    /**
     * Executes the real conversion to a path fragment
     *
     * @param id           the id to transform to a path
     * @param doubleEncode true if each sub fragment should be double encoded,
     *                     false if it should be encoded only once
     */
    private static String getPathFragment(final GenericId id, final boolean doubleEncode) {
        String toInsert;
        if (id instanceof DefinitionsChildId) {
            // @return "[ComponentName]s/{namespace}/{id}/"
            DefinitionsChildId tId = (DefinitionsChildId) id;
            String res = IdUtil.getRootPathFragment(tId.getClass());
            toInsert = tId.getNamespace().getEncoded();
            if (doubleEncode) {
                toInsert = EncodingUtil.URLencode(toInsert);
            }
            res = res + toInsert + "/";
            toInsert = tId.getXmlId().getEncoded();
            if (doubleEncode) {
                toInsert = EncodingUtil.URLencode(toInsert);
            }
            res = res + toInsert + "/";
            return res;
        } else if (id instanceof ToscaElementId) {
            toInsert = id.getXmlId().getEncoded();
            if (doubleEncode) {
                toInsert = EncodingUtil.URLencode(toInsert);
            }
            return IdUtil.getPathFragment(id.getParent()) + toInsert + "/";
        } else {
            throw new IllegalStateException("Unknown subclass of GenericId " + id.getClass());
        }
    }

    /**
     * Returns the fragment of the path belonging to the id
     * <p>
     * For instance, an Id of type ServiceTemplateId has
     * <code>servicetemplates/{encoded ns}/{encoded name}/</code>
     *
     * @param id the element to return the path fragment for
     * @return the path fragment. This is <em>not</em> intended to be used
     * inside a URL
     */
    public static String getPathFragment(GenericId id) {
        return IdUtil.getPathFragment(id, false);
    }

    /**
     * Returns the fragment of the URL path belonging to the id
     * <p>
     * For instance, an Id of type ServiceTemplateId has
     * <code>servicetemplates/{double encoded ns}/{double encoded name}/</code>
     *
     * @param id the element to return the path fragment for
     * @return the path fragment to be used inside an URL
     */
    public static String getURLPathFragment(GenericId id) {
        return IdUtil.getPathFragment(id, true);
    }

    public static String getEverythingBetweenTheLastDotAndBeforeId(Class<? extends GenericId> cls) {
        String res = cls.getName();
//        // Everything between the last "." and before "Id" is the Type
        int dotIndex = res.lastIndexOf('.');
        assert (dotIndex >= 0);
        return res.substring(dotIndex + 1, res.length() - "Id".length());
}

    /**
     * @return Singular type name for the given id. E.g., "ServiceTemplateId" gets "ServiceTemplate"
     */
    public static String getTypeForComponentId(Class<? extends DefinitionsChildId> idClass) {
        return getEverythingBetweenTheLastDotAndBeforeId(idClass);
    }

    /**
     * Returns the root path fragment for the given AbstractComponentIntanceResource
     * <p>
     * With trailing slash
     *
     * @return [ComponentName]s/
     */
    public static String getRootPathFragment(Class<? extends DefinitionsChildId> idClass) {
        // quick handling of imports special case
        // in the package naming, all other component instances have a this intermediate location, but not in the URLs
        // The package handling is in {@link org.eclipse.winery.repository.Utils.getIntermediateLocationStringForType(String, String)}
        String res;
        if (GenericImportId.class.isAssignableFrom(idClass)) {
            // this fires if idClass is a sub class from ImportCollectionId
            // special treatment for imports
            res = "imports/";
            if (XSDImportId.class.isAssignableFrom(idClass)) {
                res = res + EncodingUtil.URLencode(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            } else if (WsdlImportId.class.isAssignableFrom(idClass)) {
                res = res + EncodingUtil.URLencode(WsdlImportId.WSDL_URI);
            } else {
                throw new IllegalStateException("Not possible to determine local storage for generic imports class");
            }
            // we have the complete root path fragment
            return res;
        } else {
            res = "";
        }
        res = res + getFolderName(idClass);
        res = res + "/";
        return res;
    }

    public static String getFolderName(Class<? extends DefinitionsChildId> idClass) {
        String res = getTypeForComponentId(idClass);
        res = res.toLowerCase();
        res = res + "s";
        return res;
    }

    public static String getLastURIPart(String loc) {
        int posSlash = loc.lastIndexOf('/');
        return loc.substring(posSlash + 1);
    }
}
