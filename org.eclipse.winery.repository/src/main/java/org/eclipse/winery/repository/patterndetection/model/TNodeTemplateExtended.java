/**
 * Copyright (c) 2017 Marvin Wohlfarth.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
package org.eclipse.winery.repository.patterndetection.model;

import org.eclipse.winery.model.tosca.TNodeTemplate;

public class TNodeTemplateExtended {

	private TNodeTemplate tNodeTemplate;
	private String label;
	private String keyword;


	public TNodeTemplateExtended() {

	}

	public TNodeTemplateExtended(TNodeTemplate tNodeTemplate, String label, String keyword) {
		this.tNodeTemplate = tNodeTemplate;
		this.label = label;
		this.keyword = keyword;
	}

	public TNodeTemplate getNodeTemplate() {
		return tNodeTemplate;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
