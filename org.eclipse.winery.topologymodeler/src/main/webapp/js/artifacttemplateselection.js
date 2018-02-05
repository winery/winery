/*******************************************************************************
 * Copyright (c) 2012-2013 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

// also loaded from the repository

// TODO: winery-common should be required -> encodeID; typing could be required (but that is no AMD module)
define([], function () {
    $("#artifactTemplateName").typing({
        start: function (event, $elem) {
            flagArtifactTemplateNameAsUpdating();
        },
        stop: function (event, $elem) {
            checkArtifactTemplateName();
        }
    });

    $("#artifactTemplateNS").on("blur", checkArtifactTemplateName).on("change", checkArtifactTemplateName).on("focus", flagArtifactTemplateNameAsUpdating);

    var repositoryURL;

    return {
        setRepositoryURL: function (url) {
            repositoryURL = url;
        },
        checkArtifactTemplateName: checkArtifactTemplateName,
        flagArtifactTemplateNameAsUpdating: flagArtifactTemplateNameAsUpdating
    };

    function checkArtifactTemplateName() {
        var ns = $("#artifactTemplateNS").val();
        var name = $("#artifactTemplateName").val();
        var url = repositoryURL + "/artifacttemplates/" + encodeID(ns) + "/" + encodeID(name) + "/";
        if (name == "") {
            var valid = false;
            var invalidReason = "No name provided";
            setValidityStatus(valid, invalidReason);
        } else {
            $.ajax(url, {
                type: 'HEAD',
                dataType: 'html',
                error: function (jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status == 404) {
                        // artifact template does not exist: everything is allright
                        setValidityStatus(true, null);
                    } else {
                        setValidityStatus(false, textStatus);
                    }
                },
                success: function (data, textStatus, jqXHR) {
                    setValidityStatus(false, "artifact template already exists");
                }
            });
        }
    }

    function flagArtifactTemplateNameAsUpdating() {
        $("#artifactTemplateNameIsValid").removeClass("invalid").removeClass("valid").addClass("unknown");
        $("#artifactTemplateNameIsInvalidReason").text("");
    }

    function setValidityStatus(valid, invalidReason) {
        $("#artifactTemplateNameIsValid").removeClass("unknown");
        if (valid) {
            $("#artifactTemplateNameIsValid").addClass("valid");
            $("#artifactTemplateNameIsInvalidReason").text("Ok");
        } else {
            $("#artifactTemplateNameIsValid").addClass("invalid");
            $("#artifactTemplateNameIsInvalidReason").text(invalidReason);
        }
    }


});
