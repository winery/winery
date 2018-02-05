/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

define(function () {

    /**
     * This module handles a click on "Complete Topology" in the enterTopologyCompletionInformation dialog.
     */
    var module = {
        complete: complete,
        restartCompletion: restartCompletion
    };
    return module;

    /**
     * This function start the topology completion. With information from the
     * PolicyInformationDialog the topologyCompletion.jsp is called which will invoke the
     * Topology Completion java component. The parameters determine if the current topology
     * will be overwritten or if a new topology is created.
     *
     * @param (String)  overwriteTopology
     *                        determines how to save the topology. If true, the current topology is overwritten.
     * @param (Boolean) openInNewWindow
     *                        determines if the result is opened in a new window
     * @param (String)  topologyName
     *                        the name of the new topology
     * @param (String)  topologyNamespace
     *                        the namespace of the new topology
     * @param (Boolean) stepByStep
     *                        if true, the completion will be processed step by step
     * @param (String)  repositoryURL
     *                        the URL to the repository the topology is saved to
     * @param (String)    toscaNamespace
     *                        the namespace URL of TOSCA
     * @param (String)    wineryNamespace
     *                        the namespace URL of winery
     * @param (String)    serviceTemplateName
     *                        the name of the service template containing the displayed topology template
     * @param (String)    topologyTemplateURL
     *                        the URL to the displayed topology template
     */
    function complete(overwriteTopology, openInNewWindow, topologyName, topologyNamespace, stepByStep, repositoryURL, serviceTemplateName, topologyTemplateURL) {

        // if the user wants to create a new topology, a post call is sent to the repository adding
        // the new ServiceTemplate
        if (!overwriteTopology) {
            var dataToSend = "name=" + topologyName + "&namespace=" + topologyNamespace;
            var url = repositoryURL + "/servicetemplates/";
            $.ajax(
                {
                    type: "POST",
                    async: false,
                    url: url,
                    "data": dataToSend,
                    dataType: "text",
                    error: function (jqXHR, textStatus, errorThrown) {
                        vShowAJAXError("Could not add Service Template.");
                    }
                }
            );
        }

        var topology;
        require(["winery-topologymodeler-AMD"], function (wt) {
            topology = wt.getTopologyTemplateAsXML(true);

            // call to the completion JSP which will call the java component
            $.post("jsp/topologyCompletion/topologyCompletion.jsp", {
                    topology: topology,
                    stName: serviceTemplateName,
                    templateURL: topologyTemplateURL,
                    overwriteTopology: overwriteTopology,
                    topologyName: topologyName,
                    topologyNamespace: topologyNamespace,
                    repositoryURL: repositoryURL,
                    stepByStep: stepByStep,
                    openInNewWindow: openInNewWindow,
                    restarted: "false"
                },
                /**
                 * Callback function which will either open a new window if a new topology was created or
                 * refresh the current browser window
                 *
                 * @param (String) data
                 *            the answer of the post call
                 */
                function (data) {
                    // checks the message returned by the CompletionInterface
                    if (data.indexOf("topologyComplete") != -1) {
                        vShowSuccess('The topology is already complete.');
                    } else if (data.indexOf("failure") != -1) {
                        vShowError(data);
                    } else if (data.indexOf("userTopologySelection") != -1) {
                        $(chooseTopologyDiag[0].children[0].children[0].children[1]).html(data);
                        window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
                        chooseTopologyDiag.modal("show");
                    } else if (data.indexOf("topologyComplete") == -1 && data.indexOf("userTopologySelection") == -1 && data.indexOf("userInteraction") != -1) {
                        $(chooseRelationshipTemplateDiag[0].children[0].children[0].children[1]).html(data);
                        window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
                        chooseRelationshipTemplateDiag.modal("show");
                    } else if (data.indexOf("topologyComplete") == -1 && data.indexOf("stepByStep") != -1) {
                        $(chooseNodeTemplateDiag[0].children[0].children[0].children[1]).html(data);
                        window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
                        chooseNodeTemplateDiag.modal("show");
                    } else {
                        if (openInNewWindow) {
                            // a new topology has been created, open it in a new window
                            var win = window.open('?repositoryURL=' + repositoryURL + '&ns=' + topologyNamespace + '&id=' + topologyName, '_blank');
                            win.focus();
                        } else if (overwriteTopology) {
                            // refresh page
                            document.location.reload(true);
                        }
                    }
                }
            );
        });
    }

    /**
     * This function restarts the topology completion when it has been stopped to get a
     * user decision.
     *
     * @param (String) topology
     *                    the topology as XML string
     * @param (String) overwriteTopology
     *                    determines how to save the topology. Can contain the values "createNew" or "overwrite"
     * @param (Boolean) openInNewWindow
     *                    determines if the result is opened in a new window
     * @param (String) topologyName
     *                    the name of the new topology
     * @param (String) topologyNamespace
     *                    the namespace of the new topology
     * @param (Boolean) stepByStep
     *                    if true, the completion will be processed step by step
     * @param (String)    serviceTemplateName
     *                        the name of the service template containing the displayed topology template
     * @param (String)    topologyTemplateURL
     *                        the URL to the displayed topology template
     * @param (String)  repositoryURL
     *                        the URL to the repository the topology is saved to
     */
    function restartCompletion(topology, overwriteTopology, openInNewWindow, topologyName, topologyNamespace, stepByStep, serviceTemplateName, topologyTemplateURL, repositoryURL) {

        // remove whitespaces in the topology XML string
        topology = topology.replace(/\s+/g, ' ');
        topology = topology.substr(1);

        // call to the completion JSP which will call the java component
        $.post("jsp/topologyCompletion/topologyCompletion.jsp", {
                topology: topology,
                stName: serviceTemplateName,
                templateURL: topologyTemplateURL,
                overwriteTopology: overwriteTopology,
                topologyName: topologyName,
                topologyNamespace: topologyNamespace,
                repositoryURL: repositoryURL,
                stepByStep: stepByStep,
                restarted: "true"
            },
            /**
             * Callback function which will either open a new window if a new topology was created or
             * refresh the current browser window
             *
             * @param (String) data
             *            the answer of the post call
             */
            function (data) {
                if (data.indexOf("Successful") == -1 && data.indexOf("stepByStep") == -1) {
                    $(chooseRelationshipTemplateDiag[0].children[0].children[0].children[1]).html(data);
                    window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
                    chooseRelationshipTemplateDiag.modal("show");
                } else if (data.indexOf("Successful") == -1 && data.indexOf("stepByStep") != -1) {
                    $(chooseNodeTemplateDiag[0].children[0].children[0].children[1]).html(data);
                    window.setTimeout(jsPlumb.repaintEverything, JQUERY_ANIMATION_DURATION);
                    chooseNodeTemplateDiag.modal("show");
                } else {
                    if (openInNewWindow) {
                        // a new topology has been created, open it in a new window
                        var win = window.open('?repositoryURL=' + "<%=repositoryURL%>" + '&ns=' + topologyNamespace + '&id=' + topologyName, '_blank');
                        win.focus();
                    } else if (overwriteTopology) {
                        // refresh page
                        document.location.reload(true);
                    }
                }
            }
        );
    }
});
