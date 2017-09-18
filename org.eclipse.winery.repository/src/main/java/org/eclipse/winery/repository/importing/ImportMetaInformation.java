/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation and/or initial documentation
 *     Karoline Saatkamp - adapted variables
 */
package org.eclipse.winery.repository.importing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;

public class ImportMetaInformation {

	public final List<String> errors;
	public Optional<ServiceTemplateId> entryServiceTemplate = Optional.empty();

	public ImportMetaInformation() {
		this.errors = new ArrayList<>();
	}

}
