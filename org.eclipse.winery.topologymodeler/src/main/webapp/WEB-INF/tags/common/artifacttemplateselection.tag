<%--
/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *******************************************************************************/
--%>
<%@tag description="dialog for selecting one artifacttemplate" pageEncoding="UTF-8"%>

<%@attribute name="allNamespaces" required="true" type="java.util.Collection" description="All known namespaces"%>
<%@attribute name="repositoryURL" required="true" description="the URL of Winery's repository"%>
<%@attribute name="defaultNSForArtifactTemplate" required="true" description="the default namespace of the artifact template"%>

<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>

<div class="form-group-grouping">
	<!-- createArtifactTemplate class is required for artifactcreationdialog -->
	<div class="form-group createArtifactTemplate">
		<label>Artifact Template Name</label>
		<!-- name is an NCName -->
		<input class="artifactData form-control" id="artifactTemplateName" name="artifactTemplateName" type="text" required="required" autocomplete="on" placeholder="Enter name for artifact template" pattern="[\i-[:]][\c-[:]]*"/>
		<div id="artifactTemplateNameIsValid" class="invalid">
			<span id="artifactTemplateNameIsInvalidReason"></span>
		</div>
	</div>

	<t:namespaceChooser allNamespaces="${allNamespaces}" idOfInput="artifactTemplateNS" selected="${defaultNSForArtifactTemplate}"></t:namespaceChooser>
</div>

<script>
require(["artifacttemplateselection"], function(ast) {
	// configure the plugin
	ast.setRepositoryURL("${repositoryURL}");
});
</script>
