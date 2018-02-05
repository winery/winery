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
package org.eclipse.winery.common.ids;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;

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
            String res = Util.getRootPathFragment(tId.getClass());
            toInsert = tId.getNamespace().getEncoded();
            if (doubleEncode) {
                toInsert = Util.URLencode(toInsert);
            }
            res = res + toInsert + "/";
            toInsert = tId.getXmlId().getEncoded();
            if (doubleEncode) {
                toInsert = Util.URLencode(toInsert);
            }
            res = res + toInsert + "/";
            return res;
        } else if (id instanceof ToscaElementId) {
            toInsert = id.getXmlId().getEncoded();
            if (doubleEncode) {
                toInsert = Util.URLencode(toInsert);
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
     * <code>servicetemplates/{double encoded ns}/{double encoded name}/</encode>
     *
     * @param id the element to return the path fragment for
     * @return the path fragment to be used inside an URL
     */
    public static String getURLPathFragment(GenericId id) {
        return IdUtil.getPathFragment(id, true);
    }

}
