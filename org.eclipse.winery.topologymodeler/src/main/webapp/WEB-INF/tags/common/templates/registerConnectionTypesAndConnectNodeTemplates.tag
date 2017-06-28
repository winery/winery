<%--
/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
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
<%@tag description="Registers the connection types to jsPlumb" pageEncoding="UTF-8"%>

<%@attribute name="relationshipTypes" description="the known relationship types" required="true" type="java.util.Collection"%>
<%@attribute name="relationshipTemplates" description="the relationship templates to render" required="true" type="java.util.Collection"%>
<%@attribute name="ondone" description="JavaScript code executed when everything has been done" required="false"%>
<%@attribute name="repositoryURL" required="true" %>
<%@attribute name="readOnly" required="false" type="java.lang.Boolean"%>

<%@tag import="java.util.Collection"%>
<%@tag import="org.eclipse.winery.model.tosca.TRequirement"%>
<%@tag import="org.eclipse.winery.model.tosca.TCapability"%>
<%@tag import="org.eclipse.winery.model.tosca.TNodeTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipType"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate"%>
<%@tag import="org.eclipse.winery.model.tosca.TRelationshipTemplate.SourceOrTargetElement"%>
<%@tag import="org.eclipse.winery.common.Util"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
Collection<TRelationshipType> relationshipTypesCasted = (Collection<TRelationshipType>) relationshipTypes;
Collection<TRelationshipTemplate> relationshipTemplatesCasted = (Collection<TRelationshipTemplate>) relationshipTemplates;
%>

<script>
// we should load jquery and jquery.ui to have it available at jsPlumb
// however, jquery.ui cannot be loaded as it conflicts with bootstrap (when using toogle buttons)
require(["jquery", "jsplumb", "winery-common-topologyrendering"], function(globallyavailablea, jsPlumb, wct) {
	jsPlumb.bind("ready", function() {
		/**
		 * Shows error if req or cap does not exist
		 */
		function getNodeTemplateIdForReqOrCapId(id) {
			var reqOrCap = $("#" + id);
			if (reqOrCap.length == 0) {
				vShowError("Requirement/Capability with id " + id + " not found");
			}
			var res = reqOrCap.parent().parent().parent().attr("id");
			return res;
		}

		// register the "selected" type to enable selection of arrows
		jsPlumb.registerConnectionTypes({
			"selected":{
				paintStyle:{ strokeStyle:"red", lineWidth:5 },
				hoverPaintStyle:{ lineWidth: 7 }
			}
		});

<%
		int i=0;
		String when = "";
		String whenRes = "";
		if (relationshipTypesCasted.isEmpty()) {
%>
			vShowError("No relationship types exist. Please add relationship types to the repository");
<%
		}
		for (TRelationshipType relationshipType: relationshipTypesCasted) {
			String fnName = "ajaxRTdata" + i;
			when = when + fnName + "(), ";
			whenRes = whenRes + fnName + ", ";
%>
			function <%=fnName%>() { return $.ajax({
				url: "<%=repositoryURL%>/relationshiptypes/<%=Util.DoubleURLencode(relationshipType.getTargetNamespace())%>/<%=Util.DoubleURLencode(relationshipType.getName())%>/visualappearance/",
				dataType: "json",
				success: function(data, textStatus, jqXHR) {
					jsPlumb.registerConnectionType(
						"{<%=relationshipType.getTargetNamespace()%>}<%=relationshipType.getName()%>",
						data);
				},
				error: function(jqXHR, textStatus, errorThrown) {
					vShowAJAXError("Could not load relationship type {<%=relationshipType.getTargetNamespace()%>}<%=relationshipType.getName()%>", jqXHR, errorThrown);
				}
			});};
<%
			i++;
		}

		if (!relationshipTypesCasted.isEmpty()) {
			// strip last comma
			when = when.substring(0, when.length()-2);
			whenRes = whenRes.substring(0, whenRes.length()-2);
		}
%>
		// as soon as all relationship types are registered as jsPlumb object,
		// create connection end points and connect the nodes
		$.when(<%=when%>).done(function(<%=whenRes%>){
			require(["winery-common-topologyrendering"], function(wct) {
				// A NodeTemplateShape also appears in the palette. There, it is hidden.
				// These should not be initialized as the template will be initialized later on

				<c:if test="${readOnly}">wct.setReadOnly();</c:if>

				// Quick hack: All node templates are draggable, even in the readonly view
				wct.initNodeTemplate(jsPlumb.getSelector(".NodeTemplateShape:not('.hidden')"), true);

				var sourceId;
				var targetId;
<%
				for (TRelationshipTemplate relationshipTemplate : relationshipTemplatesCasted) {
%>
					var req = undefined;
					var cap = undefined;
<%
					// Source: Either NodeTemplate or Requirement
					SourceOrTargetElement sourceElement = relationshipTemplate.getSourceElement();
					if (sourceElement == null) {
						%>vShowError("sourceElement is null for <%=relationshipTemplate.getId()%>");<%
						continue;
					}
					Object source = sourceElement.getRef();
					if (source instanceof TRequirement) {
%>
						req = "<%=((TRequirement)source).getId()%>";
						sourceId = getNodeTemplateIdForReqOrCapId(req);
<%
					} else {
						TNodeTemplate sourceT = (TNodeTemplate) source;
						if (sourceT == null) {
							%>vShowError("sourceElement.getRef() is null for <%=relationshipTemplate.getId()%>");<%
							continue;
						}
%>
						sourceId = "<%=sourceT.getId()%>";
<%
					}

					// Target: Either NodeTemplate or Requirement
					SourceOrTargetElement targetElement = relationshipTemplate.getTargetElement();
					if (targetElement == null) {
						%>vShowError("targetElement is null for <%=relationshipTemplate.getId()%>");<%
						continue;
					}
					Object target = targetElement.getRef();
					if (target instanceof TCapability) {
%>
						cap = "<%=((TCapability)target).getId()%>";
						targetId = getNodeTemplateIdForReqOrCapId(cap);
<%
					} else {
						TNodeTemplate targetT = (TNodeTemplate) target;
						if (targetT == null) {
							%>vShowError("targetElement.getRef() is null for <%=relationshipTemplate.getId()%>");<%
							continue;
						}
%>
						targetId = "<%=targetT.getId()%>";
<%
					}
%>
					var c = jsPlumb.connect({
						source: sourceId,
						target: targetId,
						type:"<%=relationshipTemplate.getType()%>"
					});
					wct.handleConnectionCreated(c);
					// we have to store the TOSCA id as jsPlumb does not allow to pass ids from user's side
					// we could overwrite c.id, but we are not aware the side effects...
					winery.connections[c.id].id = "<%=relationshipTemplate.getId()%>";
					if (req) {
						winery.connections[c.id].req = req;
					}
					if (cap) {
						winery.connections[c.id].cap = cap;
					}
<%
					if (relationshipTemplate.getName() != null) {
%>
						winery.connections[c.id].name = "<%=relationshipTemplate.getName()%>";
<%
					}
				}
%>

				// all connections are there
				// we can register the events now

				jsPlumb.bind("connection", wct.handleConnectionCreated);

				${ondone}

			// end of the when waiting for all relationship types
			});
		// end of require binding
		});
	// jsPlumb.ready
	});
// requirejs
});
</script>
