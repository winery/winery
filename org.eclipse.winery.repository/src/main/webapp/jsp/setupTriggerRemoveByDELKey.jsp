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
<%--
JavaScript snippet binding the delete button to a trigger of the "Remove" button in case there is only one such button and that no input field is selected
--%>

    var removeButtons = $("button:contains('Remove')");
    if (removeButtons.length == 1) {
        requirejs(["keyboardjs"], function(KeyboardJS) {
            KeyboardJS.on("del", function() {
                if ($(document.activeElement).is("body")) {
                    // we are not in an input field etc.
                    removeButtons.trigger("click");
                }
            });
        });
    }
