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

import org.eclipse.winery.bpel2bpmn.model.gen.TCopy;
import org.eclipse.winery.bpel2bpmn.model.gen.TExtensibleElements;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Filter class who filters the {@link TExtensibleElements} by its pattern
 */
public final class ParameterFilter implements Predicate<TExtensibleElements> {
    private static Pattern PARAMETER_PATTERN = Pattern.compile("^prop_(?<name>[A-Z].*)_(?<variable>.*)$");

    @Override
    public boolean test(TExtensibleElements tExtensibleElements) {
        boolean isVariable = false;
        if (tExtensibleElements instanceof TCopy) {
            TCopy copy = (TCopy) tExtensibleElements;
            String variable = copy.getTo().getVariable();
            isVariable = PARAMETER_PATTERN.matcher(variable).matches();
        }

        return isVariable;
    }
}
