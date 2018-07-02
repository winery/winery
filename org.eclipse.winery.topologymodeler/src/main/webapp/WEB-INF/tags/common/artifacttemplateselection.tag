<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--%>
<%@tag description="dialog for selecting one artifacttemplate" pageEncoding="UTF-8" %>

<%@attribute name="allNamespaces" required="true" type="java.util.Collection" description="All known namespaces" %>
<%@attribute name="repositoryURL" required="true" description="the URL of Winery's repository" %>
<%@attribute name="defaultNSForArtifactTemplate" required="true"
             description="the default namespace of the artifact template" %>

<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<div class="form-group-grouping">
    <!-- createArtifactTemplate class is required for artifactcreationdialog -->
    <div class="form-group createArtifactTemplate">
        <label>Artifact Template Name</label>
        <!-- name is an NCName -->
        <input class="artifactData form-control" id="artifactTemplateName" name="artifactTemplateName" type="text"
               required="required" autocomplete="on" placeholder="Enter name for artifact template"
               pattern="[\i-[:]][\c-[:]]*"/>
        <div id="artifactTemplateNameIsValid" class="invalid">
            <span id="artifactTemplateNameIsInvalidReason"></span>
        </div>
    </div>

    <t:namespaceChooser allNamespaces="${allNamespaces}" idOfInput="artifactTemplateNS"
                        selected="${defaultNSForArtifactTemplate}"></t:namespaceChooser>
</div>

<script>
    require(["artifacttemplateselection"], function (ast) {
        // configure the plugin
        ast.setRepositoryURL("${repositoryURL}");
    });
</script>
