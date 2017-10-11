/******************************************************************************
 * Copyright (c) 2015-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Alex Frank - initial API and implementation
 ******************************************************************************/

package org.eclipse.winery.bpel2bpmn.utils;

import org.eclipse.winery.bpel2bpmn.model.gen.TScope;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Filter class who filter for {@link TScope} by its pattern
 */
public final class ScopeFilter implements Predicate<Object> {
    private static final Pattern SCOPE_PATTERN = Pattern.compile("^.*_scope$");

    @Override
    public boolean test(Object object) {
        boolean isScope = false;
        if (object instanceof TScope) {
            TScope tScope = (TScope) object;
            isScope = SCOPE_PATTERN.matcher(tScope.getName()).matches();
        }
        return isScope;
    }
}
