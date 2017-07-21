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
<%@tag description="displays a color wheel" pageEncoding="UTF-8"%>

<%@attribute name="id" required="true" description="id of the div to initialize"%>
<%@attribute name="url" required="true" description="URL to put to"%>
<%@attribute name="color" required="true" description="the initial color"%>
<%@attribute name="label" required="true" description="the label"%>

<div id="${id}" class="form-group" style="height:175px;">
    <label for="${id}Div">${label}</label>
    <div id="${id}Div" style="width:100%">
        <div class="colorwheel" style="float:left; margin-right:20px; width:300px; text-align:left;"></div>
        <div style="float:left; width:50%">
            <input id="${id}Input" name="input_example" value="${color}" size="7" style="background-color: ${color}">
            <p class="text-info">Enter the hex value above</p>
            <button type="button" class="btn btn-default btn-primary btn-sm" onclick="putColor('${url}', $('#${id}Input').val());">Save</button>
        </div>
    </div>
</div>

<script>
    var cw = Raphael.colorwheel($("#${id} div.colorwheel")[0],150);
    cw.input($("#${id} input")[0]);
</script>
