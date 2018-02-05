<%--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2012-2015 Contributors to the Eclipse Foundation
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
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<div>
	<a class="btn btn-primary" id="newtab" style="cursor:pointer;" href="${it.location}" target="_blank" >Open Editor</a>
	<a class="btn btn-info" href="topologytemplate/?view" target="_blank" >Open View</a>
	<br>
	<br>
	<div id="loading" class="topologyTemplatePreviewSizing" style="position:absolute; background-color: white; z-index:5;">Loading preview...</div>
	<iframe id="topologyTemplatePreview" class="topologyTemplatePreviewSizing" src="topologytemplate/?view=small" onload="$('#loading').hide(1000);"></iframe>
</div>
