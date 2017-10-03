/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 * Lukas Harzenetter - initial API and implementation
 */

package org.eclipse.winery.repository.rest.resources.apiData;

import javax.xml.bind.annotation.XmlAttribute;

import io.swagger.annotations.ApiModelProperty;

public class GenerateArtifactApiData {

	@XmlAttribute(required = true)
	@ApiModelProperty(value = "This is the name of the implementation/deployment artifact. " +
			"Is <em>also</em>used as prefix of the name of the corresponding artifact template if no specific template is provided. " +
			"In contrast to CS01, we require a artifactName also for the implementationArtifact to be able to properly referencing it.")
	public String artifactName;

	@ApiModelProperty(value = "QName of the artifact Template - used by Winery Backend instead of artifactTemplateName + artifactTemplateNS")
	public String artifactTemplate;

	@ApiModelProperty(value = "if provided and autoCreateArtifactTemplate, a template of this id localname and artifactTemplateNS generated. " +
			"Winery always sends this string if auto creation is desired.")
	public String artifactTemplateName;

	public String artifactTemplateNamespace;

	@ApiModelProperty(value = "if empty, no, or false, no artifact template is created. " +
			"An artifact type has to be given in that case. " +
			"Furthermore, an artifact template name + artifact template namespace has to be provided. " +
			"Otherwise, the artifactNameStr is used as name for the artifact and a <em>new</em> artifact template is created having {@code <artifactNameString>Template} as name")
	public String autoCreateArtifactTemplate;

	@XmlAttribute(required = true)
	@ApiModelProperty(value = "QName of the type, format: {namespace}localname. " +
			"Optional if artifactTemplateName + artifactTempalteNS is provided")
	public String artifactType;

	@ApiModelProperty(value = "<em>XML</em> snippet that should be put inside the artifact XML in the TOSCA serialization. " +
			"This feature will be removed soon. " +
			"TODO: This only works if there is a single child element expected and not several elements. " +
			"Future versions of the Winery will support arbitrary content there.")
	public String artifactSpecificContent;

	public String interfaceName;

	public String operationName;

	@ApiModelProperty(value = "If not empty, the IA generator will be called")
	public String autoGenerateIA;

	@ApiModelProperty(value = "The Java package to use for IA generation")
	public String javaPackage;

}
