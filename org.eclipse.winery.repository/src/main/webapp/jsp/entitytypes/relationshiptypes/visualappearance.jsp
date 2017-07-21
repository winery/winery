<%--
/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Yves Schubert - switch to bootstrap 3
 *    Niko Stadelmaier - removal of select2 library
 *******************************************************************************/
--%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>

<script type='text/javascript' src='${pageContext.request.contextPath}/components/raphael/raphael.js'></script>
<script type='text/javascript' src='${pageContext.request.contextPath}/components/colorwheel/javascripts/colorwheel.js'></script>

<ul class="nav nav-tabs" id="myTab">
    <li class="active"><a href="#icon">Icon</a></li>
    <li><a href="#colors">Colors</a></li>
    <li><a href="#arrowDiv">Arrow</a></li>
</ul>

<div class="tab-content">
    <div class="tab-pane active" id="icon">
        <br />
        <t:imageUpload
            label="Icon (16x16) used in palette"
            URL="visualappearance/16x16"
            id="upSmall"
            width="16px"
            resize="16"
            accept="image/*"/>
    </div>

    <div class="tab-pane" id="colors">
        <br />
        <form>
            <fieldset>
                <t:colorwheel label="Line Color" color="${it.color}" id="color" url="color" />
                <t:colorwheel label="Hover Color" color="${it.hoverColor}" id="hovercolor" url="hovercolor" />
            </fieldset>
        </form>
    </div>

    <div class="tab-pane" id="arrowDiv">
        <br />
        <form>
            <fieldset>
                <div class="form-group">
                    <label for="arrow">Arrow appearance</label>
                    <div style="width:100%" id="arrow">
                        <div style="float:left; ">
                            <!-- Same values as the beginning of the file names in src\main\webapp\images\relationshiptype -->
                            <div id="dropDownSourceHead">
                            <input type="radio" id="dropDownSourceHeadNone" name="sourceHead" value="none">
                            <label for="dropDownSourceHeadNone"></label>
                            <br>
                            <input type="radio" id="dropDownSourceHeadPain" name="sourceHead" value="PlainArrow">
                            <label for="dropDownSourceHeadPain"></label>
                            <br>
                            <input type="radio" id="dropDownSourceHeadDiamond" name="sourceHead" value="Diamond">
                            <label for="dropDownSourceHeadDiamond"></label>
                            </div>
                        </div>
                        <div id="lineSelect" style="float:left; margin-left: 2rem">

                            <input type="radio" id="lineSelectPlain" name="lineRadio" value="plain">
                            <!--  not yet supported
                            <option value="simpleArrow"></option>
                            <option value="doubleArrow"></option>
                            <option value="circle"></option>
                            <option value="square"></option> -->
                            </input>
                            <label for="lineSelectPlain"></label>
                            <br>
                            <input type="radio" id="lineSelectDotted" name="lineRadio" value="dotted">
                            <label for="lineSelectDotted"></label>
                            <br>
                            <input type="radio" id="lineSelectDotted2" name="lineRadio" value="dotted2">
                            <label for="lineSelectDotted2"></label>
                        </div>
                        <div id="dropDownTargetHead" style="float:left; margin-left: 2rem">
                            <input type="radio" id="dropDownTargetHeadNone" name="targetRadio" value="none">
                                <!--  not yet supported
                                <option value="simpleArrow"></option>
                                <option value="doubleArrow"></option>
                                <option value="circle"></option>
                                <option value="square"></option> -->
                            <label for="dropDownTargetHeadNone"></label>
                            <br>
                            <input type="radio" name="targetRadio" id="dropDownTargetHeadPlain" value="PlainArrow">
                            <label for="dropDownTargetHeadPlain"></label>
                            <br>
                            <input type="radio" id="dropDownTargetHeadDiamond" name="targetRadio" value="Diamond">
                            <label for="dropDownTargetHeadDiamond"></label>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>

<script>
$('#myTab a').click(function (e) {
    e.preventDefault();
    $(this).tab('show');
});

/**
 * @param sourceOrTarget "Source" or "Target"
 */
function formatArrow(config, sourceOrTarget) {
    var path = "${pageContext.request.contextPath}/images/relationshiptype/" + config.id + sourceOrTarget + ".png";
    return "<img width='16px' src='" + path +"' />";
}

var globalAJAXParamsForSelect2VisualAppearance = {
    type  : "PUT",
    contentType : "text/plain",
    success : function() {
        vShowSuccess("Successfully updated arrow appearance");
    },
    error : function(jqXHR, textStatus, errorThrown) {
        vShowAJAXError("Could not supdate arrow appearance", jqXHR, errorThrown);
    }
};

$(function(){

   $("#dropDownSourceHead label").each(function(){
        var radioVal = $("#" + $(this).attr("for")).val();
       $(this).html(formatArrowSource({id: radioVal}));
   });

    $("#lineSelect label").each(function(){
        var radioVal = $("#" + $(this).attr("for")).val();
        $(this).html(formatLine({id: radioVal}));
    });

    $("#dropDownTargetHead label").each(function(){
        var radioVal = $("#" + $(this).attr("for")).val();
        $(this).html(formatArrowTarget({id: radioVal}));
    });

});

/* source arrow head */

function formatArrowSource(config) {
    return formatArrow(config, "Source");
}

// set stored value
$("input[name='sourceHead'][value='${it.sourceArrowHead}']").prop('checked', true);
// enable storage on change of element
$("#dropDownSourceHead input[name='sourceHead']").on("change", function(e) {
    params = globalAJAXParamsForSelect2VisualAppearance;
    params.url = "visualappearance/sourcearrowhead";
    params.data = $(this).val();
    $.ajax(params);
});

/* line */
function formatLine(config) {
    var path = "${pageContext.request.contextPath}/images/relationshiptype/" + config.id + "Line.png";
    return "<img src='" + path +"' />";
}

//set stored value
$("input[name='lineRadio'][value='${it.dash}']").prop('checked', true);//enable storage on change of element
$("#lineSelect input[name='lineRadio']").on("change", function(e) {
    var params = globalAJAXParamsForSelect2VisualAppearance;
    params.url = "visualappearance/dash";
    params.data = $(this).val();
    $.ajax(params);
});

/* target arrow head */

function formatArrowTarget(config) {
    return formatArrow(config, "Target");
}

//set stored value
$("input[name='targetRadio'][value='${it.targetArrowHead}']").prop('checked', true);
//enable storage on change of element
$("#dropDownTargetHead input[name='targetRadio']").on("change", function(e) {
    var params = globalAJAXParamsForSelect2VisualAppearance;
    params.url = "visualappearance/targetarrowhead";
    params.data = $(this).val();
    $.ajax(params);
});
</script>

