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
<script>
    function switchStyledTabSelection(element, onSuccess) {
        var target = element.attr("href");
        target = target.replace('#','');
        target += "/";
        $(".styledTabMenuButton2ndlevel").removeClass("selected");
        element.addClass("selected");
        $("#ccontainer").html('<div id="loading" style="display:none;">loading...</div>');
        $("#loading").fadeIn(3000);
        $("#ccontainer").load(target, function(response, status, xhr) {
            if (status == "error") {
                vShowError("Could not load tab content: " + xhr.status + " " + xhr.statusText);
            } else {
                if (onSuccess) {
                    onSuccess();
                }
            }
        });
    }

    /**
     * @param onSuccess called if subTabs are available and the super tab could be loaded
     */
    function doTheTabSelection(onSuccess) {
        var hash = window.location.hash;

        // get rid of ";" additions
        var posColon = hash.indexOf(";");
        var subTab;
        if (posColon >= 0) {
            subTab = hash.substr(posColon + 1);
            hash = hash.substr(0, posColon);
        }

        if ($.inArray(hash, ${param.validpages}) == -1) {
            hash = "${param.defaultpage}";
        }

        var callBack = onSuccess;
        if (subTab) {
            var posColon = subTab.indexOf(";");
            if (posColon > 0) {
                // more information added, for instance xmltree data
                // the first part is the subtab
                subTab = subTab.substr(0, posColon);
            }
            callBack = function () {
                $('#myTab a[href="#' + subTab + '"]').tab('show');
                if (onSuccess) {
                    onSuccess();
                }
            };
        }

        switchStyledTabSelection($(".styledTabMenuButton2ndlevel[href='" + hash + "']"), callBack);
    }

    // Firefox does not fire the "popstate" event at the first load,
    // but Chrome does
    // Because of Firefox, we have to call the refresh here
    doTheTabSelection();

    // see http://blog.mgm-tp.com/2011/10/must-know-url-hashtechniques-for-ajax-applications/ for a broad discussion
    $(window).on("hashchange", function(e) {
        if (internalHashChange) {
            // we do nothing
            internalHashChange = false;
        } else {
            // we have to check whether only additional data changed and thus no real change is required
            var oldURL = e.originalEvent.oldURL;
            var idx = oldURL.indexOf("#");
            if (idx != -1) {
                var oldHash = oldURL.substr(idx);
                var newURL = e.originalEvent.newURL;
                // get the hash value only
                idx = newURL.indexOf("#");
                if (idx != -1) {
                    var newHash = newURL.substr(idx);
                    // search for the first ";"
                    idx = oldHash.indexOf(";");
                    if (idx != -1) {
                        // search for the second ";"
                        idx = oldHash.indexOf(";", idx+1);
                        if (idx == -1) {
                            // The new hash is only a refinement of the old hash:
                            // Only a new ";" has been added
                            // We do not need to reload
                            return;
                        } else {
                            if (oldHash.substr(0, idx) == newHash.substr(0, idx)) {
                                // the two hashes equal until the second ";"
                                // we don't have to do any reload
                                return;
                            }
                        }
                    }
                }
            }

            // switch to the new tab
            doTheTabSelection();
        }
    });

    var internalHashChange = false;

    $(document).on('shown.bs.tab', "ul.nav-tabs > li > a", function (e) {
        var id = $(e.target).attr("href").substr(1);
        var hash = window.location.hash;
        var additionalData = "";
        var posColon = hash.indexOf(";");
        if (posColon >= 0) {
            // search for additionalData
            var secondColon = hash.indexOf(";", posColon+1);
            if (secondColon > 0) {
                // include the ";" in the additional data
                additionalData = hash.substr(secondColon);
            }

            // wipe everything after the colon
            hash = hash.substr(0, posColon);
        }
        hash = hash + ";" + id + additionalData;
        internalHashChange = true;
        window.location.hash = hash;
    });
</script>
