/*******************************************************************************
 * Copyright (c) 2013-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common.ids;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;

/**
 * Helper methods for Winery's Id system
 */
public class IdUtil {

    /**
     * Returns the namespace where the given Id is nested in. As the id is not a
     * TOSCAComponentId, it cannot be directly asked for its parent. Merely, the
     * parent has to be asked for its namespace. The parent, in turn, if it is
     * no TOSCAComponentId has to ask his parent.
     *
     * @param id the id referencing to an element, where the namespace has to be
     *            checked for
     * @return the namespace of the element denoted by id
     */
    public static Namespace getNamespace(GenericId id) {
        if (id instanceof TOSCAComponentId) {
            return ((TOSCAComponentId) id).getNamespace();
        } else {
            return IdUtil.getNamespace(id.getParent());
        }
    }

    /**
     * Returns the fragment of the path belonging to the id
     *
     * For instance, an Id of type ServiceTemplateId has
     * <code>servicetemplates/{encoded ns}/{encoded name}/</code>
     *
     * @param id the element to return the path fragment for
     * @return the path fragment. This is <em>not</em> intended to be used
     *         inside a URL
     */
    public static String getPathFragment(GenericId id) {
        String toInsert;
        if (id instanceof TOSCAComponentId) {
            // @return "[ComponentName]s/{namespace}/{id}/"
            TOSCAComponentId tId = (TOSCAComponentId) id;
            String res = Util.getRootPathFragment(tId.getClass());
            toInsert = tId.getNamespace().getEncoded();
            res = res + toInsert + "/";
            toInsert = tId.getXmlId().getEncoded();
            res = res + toInsert + "/";
            return res;
        } else if (id instanceof TOSCAElementId) {
            toInsert = id.getXmlId().getEncoded();
            return IdUtil.getPathFragment(id.getParent()) + toInsert + "/";
        } else {
            throw new IllegalStateException("Unknown subclass of GenericId " + id.getClass());
        }
    }

}
