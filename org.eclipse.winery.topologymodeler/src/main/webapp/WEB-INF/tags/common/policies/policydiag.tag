<%--
/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Niko Stadelmaier - removal of select2 library
 *******************************************************************************/
--%>

<%@tag import="org.eclipse.winery.model.tosca.TPolicyTemplate"%>
<%@tag description="Dialog to add or update a policy. Offers function showUpdateDiagForPolicy(policyElement) / showAddDiagForPolicy(nodeTemplateElement)" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@taglib prefix="o"  tagdir="/WEB-INF/tags/common/orioneditor"%>
<%@taglib prefix="w"  tagdir="/WEB-INF/tags"%>
<%@taglib prefix="ct"  tagdir="/WEB-INF/tags/common"%>
<%@taglib prefix="wc" uri="http://www.eclipse.org/winery/functions"%>

<%@attribute name="allPolicyTypes" required="true" type="java.util.Collection" description="Collection&lt;QName&gt; of all available policy types" %>
<%@attribute name="repositoryURL" required="true" type="java.lang.String" description="The URL of winery's repository"%>

<div class="modal fade" id="PolicyDiag">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Policy</h4>
			</div>
			<div class="modal-body">
				<ct:id_name_type idPrefix="policy" allTypes="${allPolicyTypes}" hideIdField="true" />

				<div class="form-group">
					<label for="policyTemplate" class="control-label">Policy Template:</label>

					<select id="policyTemplate" class="form-control" name="policyTemplate"></select>
				</div>

				<o:orioneditorarea areaid="OrionpolicyXML" withoutsavebutton="true" />
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				<button type="button" id="deletePolicy" class="btn btn-danger" onclick="deletePolicy();">Delete</button>
				<button type="button" id="updatePolicy" class="btn btn-primary" onclick="addOrUpdatePolicy(false);">Update</button>
				<button type="button" id="addPolicy" class="btn btn-primary" onclick="addOrUpdatePolicy(true);">Add</button>
			</div>
		</div>
	</div>
</div>

<c:set var="clazz" value="<%=org.eclipse.winery.model.tosca.TPolicy.class%>" />
<textarea id="emptyPolicy" class="hidden">${wc:XMLAsString(clazz, null)}</textarea>

<script>
	//global variable set by showUpdateDiagForPolicy and read by addOrUpdatePolicy
	var currentPolicyElement;

	// possibly this is a duplicate information as we also have "currentlySelectedNodeTemplate" (or similar)
	var currentNodeTemplateElement;

	function updatePolicyTemplateSelect(valueToSelect) {
		require(["winery-support-common"], function(w) {
			var type = $("#policyType").val();
			var fragment = w.getURLFragmentOutOfFullQName(type);
			var url = "${repositoryURL}/policytypes/" + fragment + "/instances/";
			$.ajax(url, {
				dataType: 'json'
			}).fail(function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not get policy templates", jqXHR, errorThrown);
			}).done(function(data) {
				// add "(none)" to available items
				var none = {
					id: "(none)",
					text: "(none)"
				};
//			data.unshift(none);

				if (typeof valueToSelect === "undefined") {
					valueToSelect = "(none)";
				}
				//create select optgroups out of data
				var polTemp = $("#policyTemplate");
				polTemp.empty();
				polTemp.append($("<option value='(none)'>(none)</option>"));
				$.each(data, function(index, val){
					var optGrp = $("<optgroup label=' " + val.text + "'>");
					$.each(val.children, function(index, item){
						var option = $("<option value='" + item.id + "'>" + item.text + "</option>");
						optGrp.append(option);
					});
					polTemp.append(optGrp);
				});
			$("#policyTemplate")
				.val(valueToSelect);
			});
		});
	}

	function showUpdateDiagForPolicy(policyElement) {
		currentPolicyElement = policyElement;

		$("#deletePolicy").show();
		$("#updatePolicy").show();
		$("#addPolicy").hide();

		var name = policyElement.children("div.name").text();
		var type = policyElement.children("span.type").text();

		$("#policyName").val(name);
		$("#policyType").val(type);

		// onchange of type is not called, we have to update the template selection field for ourselves
		// we also have to select the current user's choice
		updatePolicyTemplateSelect(policyElement.children("span.template").text());

		var diag = $("#PolicyDiag");
		require(["winery-support-common"], function(w) {
			w.replaceDialogShownHookForOrionUpdate(diag, "OrionpolicyXML", currentPolicyElement.children("textarea").val());
			diag.modal("show");
		});
	}

	function showAddDiagForPolicy(nodeTemplateElement) {
		currentNodeTemplateElement = nodeTemplateElement;

		$("#deletePolicy").hide();
		$("#updatePolicy").hide();
		$("#addPolicy").show();

		$("#policyName").val("");

		// fill policy template select field
		updatePolicyTemplateSelect();

		var diag = $("#PolicyDiag");
		require(["winery-support-common"], function(w) {
			w.replaceDialogShownHookForOrionUpdate(diag, "OrionpolicyXML", $("#emptyPolicy").val());
			diag.modal("show");
		});
	}

	function addOrUpdatePolicy(doAdd) {
		if (highlightRequiredFields()) {
			vShowError("Please fill in all required fields");
			return;
		}

		require(["winery-support-common", "tmpl"], function(wsc, tmpl) {
			var res = wsc.synchronizeNameAndType("policy", false, [{
				attribute: "policyType",
				fieldSuffix: "Type",
			}, {
				attribute: "policyRef",
				fieldSuffix: "Template"
			}]);

			if (res) {
				var policyTemplate = {id: $("#policyTemplate :selected").val(), text: $("#policyTemplate :selected").text()};
				var renderData = {
					name: $("#policyName").val(),

					policyTypeText: $("#policyType :selected").text(),
					policyTypeVal: $("#policyType").val(),

					policyTemplateText: policyTemplate.text,
					policyTemplateVal: policyTemplate.id,

					xml: res.xml
				};
				var div = tmpl("tmpl-policy", renderData);
				if (doAdd) {
					currentNodeTemplateElement.children("div.policiesContainer").children("div.content").children("div.addnewpolicy").before(div);
				} else {
					currentPolicyElement.replaceWith(div);
				}
				$("#PolicyDiag").modal("hide");
			} else {
				vShowError("Could not synchronize XML fields");
			}
		});
	}


	function deletePolicy() {
		// We just have to remove the HTML element:
		// The save operation converts the information in the HTML to XML
		currentPolicyElement.remove();
		$("#PolicyDiag").modal("hide");
	}


	$("#policyType")
			.on("change", updatePolicyTemplateSelect);
</script>

<%-- parameters: o.id, o.name, o.policyType, o.policyRef, o.xml. Has to be consistent with the HTML generated by policies.tag --%>
<script type="text/x-tmpl" id="tmpl-policy">
	<div class="policy row"> <%-- "even"/"odd" is not set. Could be done by using $.prev() --%>
		<div class="col-xs-4 policy name">{%=o.name%}</div>

		<%-- we do not provide a link here. Link is only available at reload. Makes life easier here. makeArtifactTemplateURL at winery-common.js is a first hint of how to generate links --%>
		<div class="col-xs-4 policy type">{%=o.policyTypeText%}</div>
		<span class="type">{%=o.policyTypeVal%}</span>

		<div class="col-xs-4 policy template">{%=o.policyTemplateText%}</div>
		<span class="template">{%=o.policyTemplateVal%}</span>

		<textarea class="policy_xml">{%=o.xml%}</textarea>
	</div>
</script>
