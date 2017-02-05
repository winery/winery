<%--
/*******************************************************************************
 * Copyright (c) 2013-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Yves Schubert - initial API and implementation and/or initial documentation
 *    Oliver Kopp - minor improvements
 *    Lukas Balzer, Nicole Keppler - switch to bootstrap-touchspin
 *******************************************************************************/
--%>
<%@tag description="A spinner with the possibility to set to inphty via button" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- Code copied between repository and topology-modeler --%>

<%--
Could also be realized as
 * HTML5 Web Component (http://www.ibm.com/developerworks/library/wa-html5components1/) or
 * x-tags (http://www.x-tags.org/)
We decided to use JSP tags to avoid an additional JavaScript library
--%>

<%@attribute name="label" required="true"%>
<%@attribute name="id" required="true"%>
<%@attribute name="min"%>
<%@attribute name="max" required="false" description="Maximum value. Default is 1000. The underlying library does not allow arbitrary high values."%>
<%@attribute name="name" required="false" description="The name of the input field. Defaults to the id"%>
<%@attribute name="withinphty" required="false" description="If set, then an inphty button is provded"%>
<%@attribute name="value"%>
<%@attribute name="width" required="false" description="The Column with according to bootstrap rules. Default is 3 (should not be smaller)."%>
<%@attribute name="changedfunction" required="false" description="Called if value changed"%>

<%-- Set default name value if required --%>
<c:if test="${empty name}">
	<c:set var="name" value="${id}"></c:set>
</c:if>

<c:if test="${empty width}">
	<c:set var="width" value="3"></c:set>
</c:if>

<div class="form-group">
	<label for="${id}">${label}</label>
	<div class="row">
		<div class="col-lg-${width}">
			<div class="input-sm">
				<input id="${id}" type="text" value="" name="${name}"
					   <c:if test="${not empty changedfunction}">onblur="${changedfunction}();"</c:if> />
			</div>
		</div>
	</div>
</div>

<script>
	<%--
	included multiple times.
	Drawback when not using HTML5 components and keeping the JavaScript functions closed to the HTML code
	--%>
	function setToInfin(id, changedFunction) {
		var spinner = $("#" + id);
		spinner.val('∞'); // &inphty; - jQuery does not decode that, but places the plain text. Therefore, we directly pass the char we want
		if (changedFunction !== undefined) {
			changedFunction();
		}
	}

	$(function() {
		var param = {}
		<c:if test="${not empty min}">
		param.min = "${min}";
		</c:if>
		<c:if test="${empty max}">
		param.max = 1000;
		</c:if>
		<c:if test="${not empty withinphty}">
		param.postfix='∞';
		param.postfix_extraclass= "btn btn-default";
		</c:if>
		param.verticalbuttons = true;
		param.verticalupclass= 'glyphicon glyphicon-chevron-up';
		param.verticaldownclass= 'glyphicon glyphicon-chevron-down';
		//use touchspin plugin
		$("#${id}").TouchSpin(param);
		<c:if test="${not empty withinphty}">
		$("#${id}").parent().find('.bootstrap-touchspin-postfix').on('mousedown',
				function(){
					//register mousedown function for calling the setToInf function
					setToInfin('${id}'<c:if test="${not empty changedfunction}">, ${changedfunction}</c:if>);
				});
		</c:if>
		<c:if test="${not empty changedfunction}">
		$("#${id}").on('valueChanged', ${changedfunction});
		</c:if>

	});

</script>
