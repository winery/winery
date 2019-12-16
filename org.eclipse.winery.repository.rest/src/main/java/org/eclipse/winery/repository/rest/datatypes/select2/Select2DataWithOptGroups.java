/********************************************************************************
 * Copyright (c) 2014-2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.repository.rest.datatypes.select2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Select2DataWithOptGroups {

    Map<String, Select2OptGroup> idx = new HashMap<>();

    /**
     * Add an item to a group
     *
     * @param group the group
     * @param id    the id of the item
     * @param text  the text of the item {@inheritDoc}
     */
    public void add(String group, String id, String text) {
        String groupId = group;
        try {
            groupId = URLEncoder.encode(group, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Select2OptGroup optGroup = this.idx.get(groupId);
        if (optGroup == null) {
            optGroup = new Select2OptGroup(groupId, group);
            this.idx.put(groupId, optGroup);
        }

        Select2DataItem item = new Select2DataItem(id, text);
        optGroup.addItem(item);
    }

    public SortedSet<Select2OptGroup> asSortedSet() {
        // convert the index to the real result
        SortedSet<Select2OptGroup> res = new TreeSet<>();
        for (Select2OptGroup optGroup : this.idx.values()) {
            res.add(optGroup);
        }

        return res;
    }
}
