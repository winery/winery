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

<%@tag description="About for the repository" pageEncoding="UTF-8"%>

<%@taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="t"  tagdir="/WEB-INF/tags"%>
<%@taglib prefix="w"  uri="http://www.eclipse.org/winery/repository/functions"%>

<div class="modal fade" id="about">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title">Winery ${project.version}</h4>
			</div>
			<div class="modal-body">
				<p>	Supporting <a href="docs.oasis-open.org/tosca/TOSCA/v1.0/os/TOSCA-v1.0-os.html">TOSCA-v1.0 &ndash;
                    Topology and Orchestration Specification for Cloud Applications Version 1.0. 25 November 2013. OASIS Standard.</a><br/>
					<br/>
					Part of the <a href="http://www.cloudcycle.org">CloudCycle</a> ecosystem.<br/>
					<br/>
					Code contributions by Oliver Kopp, Tobias Binz, Uwe Breitenbücher, Kálmán Képes, Rene Trefft, Yves Schubert, Timur Sungur, Jerome Tagliaferri, and Tobias Unger.
				</p>
				<h3>License</h3>
				<p>The Eclipse Foundation makes available all content of this software (&ldquo;Content&rdquo;).
				Unless otherwise indicated below, the Content is provided to you under the terms and conditions of the Eclipse Public License Version 1.0 (&ldquo;EPL&rdquo;) and the and the Apache License 2.0.
				A copy of the EPL is available at <a href="http://www.eclipse.org/legal/epl-v10.html">http://www.eclipse.org/legal/epl-v10.html</a>.
				A copy of the ASL is available at <a href="http://www.apache.org/licenses/LICENSE-2.0.html">http://www.apache.org/licenses/LICENSE-2.0.html</a>.
				For purposes of the EPL, &ldquo;Program&rdquo; will mean the Content.</p>
				<p>If you did not receive this Content directly from the Eclipse Foundation, the Content is being redistributed by another party (&ldquo;Redistributor&rdquo;) and different terms and conditions may apply to your use of any object code in the Content.
				Check the Redistributor's license that was provided with the Content.
				If no such license exists, contact the Redistributor.
				Unless otherwise indicated below, the terms and conditions of the EPL still apply to any source code in the Content and such source code may be obtained at <a href="http://www.eclipse.org">http://www.eclipse.org</a>.</p>
			</div>
			<div class="modal-footer">
				<c:if test="${w:isRestDocDocumentationAvailable()}">
					<a class="btn btn-primary" target="_blank" href="/restdoc.html">Show documentation of REST API</a>
				</c:if>
				<button type="button" class="btn btn-primary" data-dismiss="modal" id="aboutDiagOKButton">Ok</button>
			</div>
		</div>
	</div>
</div>

<script>
$("#about").on("shown.bs.modal", function() {
	$("#aboutDiagOKButton").focus();
});

function showAbout() {
	$("#about").modal("show");
}
</script>