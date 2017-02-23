/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *******************************************************************************/
package org.eclipse.winery.repository.resources.documentation;

import java.util.List;

public class DocumentationResourceAPIData {

	public String documentation;

	public DocumentationResourceAPIData(List<DocumentationResource> docList){
		documentation ="empty";
		for(DocumentationResource docRes : docList) {
			documentation ="";
			for (Object obj : docRes.getDocu().getContent()) {
				documentation += (String) obj + "\n";
			}
		}
	 }
}
