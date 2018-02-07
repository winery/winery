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

<%@tag description="About for the repository" pageEncoding="UTF-8" %>

<div class="modal fade" id="about">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Winery Topology Modeler ${project.version}</h4>
            </div>
            <div class="modal-body">
                <p> Supporting <a href="docs.oasis-open.org/tosca/TOSCA/v1.0/os/TOSCA-v1.0-os.html">TOSCA-v1.0 &ndash;
                    Topology and Orchestration Specification for Cloud Applications Version 1.0. 25 November 2013. OASIS
                    Standard.</a><br/>
                    <br/>
                    Code contributions by Oliver Kopp, Uwe Breitenbücher, Kálmán Képes, Yves Schubert, and Tobias Unger.
                </p>
                <h3>License</h3>
                <p>The Eclipse Foundation makes available all content of this software (&ldquo;Content&rdquo;).
                    Unless otherwise indicated below, the Content is provided to you under the terms and conditions of
                    the Eclipse Public License Version 2.0 (&ldquo;EPL&rdquo;) and the and the Apache License 2.0.
                    A copy of the EPL is available at <a href="http://www.eclipse.org/legal/epl-v20.html">http://www.eclipse.org/legal/epl-v20.html</a>.
                    A copy of the ASL is available at <a href="http://www.apache.org/licenses/LICENSE-2.0.html">http://www.apache.org/licenses/LICENSE-2.0.html</a>.
                    For purposes of the EPL, &ldquo;Program&rdquo; will mean the Content.</p>
                <p>If you did not receive this Content directly from the Eclipse Foundation, the Content is being
                    redistributed by another party (&ldquo;Redistributor&rdquo;) and different terms and conditions may
                    apply to your use of any object code in the Content.
                    Check the Redistributor's license that was provided with the Content.
                    If no such license exists, contact the Redistributor.
                    Unless otherwise indicated below, the terms and conditions of the EPL still apply to any source code
                    in the Content and such source code may be obtained at <a href="http://www.eclipse.org">http://www.eclipse.org</a>.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" data-dismiss="modal" id="aboutDiagOKButton">Ok</button>
            </div>
        </div>
    </div>
</div>

<script>
    $("#about").on("shown.bs.modal", function () {
        $("#aboutDiagOKButton").focus();
    });

    function showAbout() {
        $("#about").modal("show");
    }
</script>
