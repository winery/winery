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

/**
This .js file is shared between the Winery Repository and the Winery Topology Modeler

This should be rewritten as AMD module. A start is made in winery-support-common.js.

vConfirmYesNo is defined in jsp/shared/dialogs.jsp
*/

/**
 * Highlights fields, which are required but not filled out by the user.
 * The check for required fields is done via the CSS class "required"
 *
 * @return true if a required field is not filled with a value
 */
function highlightRequiredFields() {
	var requiredFieldMissing = false;
	// includes check input field attribute "required" of HTML5: http://www.w3.org/TR/html-markup/input.radio.html#input.radio.attrs.required
	$("input.required:visible:enabled, input[required]:visible").each(function(){
		if ($(this).val() == '') {
			$(this).parent().addClass('has-warning');
			requiredFieldMissing = true;
		} else {
			$(this).parent().removeClass('has-warning');
		}
	});
	return requiredFieldMissing;
}

function vPnotify(text, title, type) {
	require(["pnotify"], function() {
		var notice = $.pnotify({
			title: title,
			text: text,
			type: type
		});
		notice.on("click", function(e) {
			var target = $(e.target);
			if (target.is(".ui-pnotify-closer *, .ui-pnotify-sticker *, a")) {
				// buttons clicked - call their functionality
				return true;
			} else {
				// click on text leads to display of a dialog showing the complete content

				var textDiv;
				if (target.is("div.ui-pnotify-text")) {
					textDiv = target;
				} else {
					textDiv = target.closest("div.ui-pnotify-container").find("div.ui-pnotify-text");
				}

				// put text into dialog and show it
				$("#diagmessagetitle").text("Full notification");
				$("#diagmessagemsg").html(textDiv.html());
				$("#diagmessage").modal("show");

				return false;
			}
		});
	});
}

/**
 * @param title optional title
 */
function vShowError(text, title) {
	vPnotify(text, title, "error");
}

function vShowAJAXError(msg, jqXHR, errorThrown) {
	vShowError(msg + "<br />" + errorThrown + "<br/>" + jqXHR.responseText);
}

/**
 * @param title optional title
 */
function vShowNotification(text, title) {
	vPnotify(text, title, "notification");
}


/**
 * @param title optional title
 */
function vShowSuccess(text, title) {
	vPnotify(text, title, "success");
}

/**
 * Deletes the given resource with confirmation.
 * if deletion fails, an error message is shown
 *
 * @param onSuccess: function(data, textStatus, jqXHR) to call if deletion has been successful
 * @param onError: function(jqXHR, textStatus, errorThrown) called if deletion lead to an error (optional)
 * @param onStart: function() called if user has agreed to delete resource (optional)
 * @param withoutConfirmation if given, the resource is deleted without any confirmation
 */
function deleteResource(nameOfResource, url, onSuccess, onError, onStart, withoutConfirmation) {
	var f = function() {
		$.ajax({
			url:  url,
			type: 'DELETE',
			async: true,
			error: function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not delete " + nameOfResource, jqXHR, errorThrown);
				if (onError) onError();
			},
			success: function(data, textStatus, jqXHR) {
				vShowSuccess("Successfully deleted " + nameOfResource);
				onSuccess(data, textStatus, jqXHR);
			}
		});
	};
	if (withoutConfirmation) {
		f();
	} else {
		vConfirmYesNo("Do you really want to delete " + nameOfResource + "?", f);
	}
}

/**
 * Function to create a td with two columns: one for a key and one for a value
 *
 * Has to be called from a td element: This function uses $(this) to determine the td
 * It changes the content of the td to an input field. In case the input was updated, it is POSTed to the given URL
 *
 * TODO: The editing mode should use x-editable instead of a self-written input handling
 *
 * @param url the URL to post the key/value pair to
 * @param keyName the keyname of the key - used at POST with <keyName>=<newKey>
 * @param valueName the keyname of the value - used at POST with <valueName>=<newValue>
 *
 */
function vCreateTdClickFunction(url, keyName, valueName) {
	var inputId = "thingedit" + Math.floor((Math.random()*100)+1); ;
	keyName = keyName || "key";
	valueName = valueName || "value";

	var f = function(e) {
		var input = $("#" + inputId);
		if (input.length != 0) {
			// input field already there
			return;
		}

		var td = $(this);
		var oldPrefix = td.text();
		var html =  "<input id='" + inputId + "' value='" + oldPrefix + "'></input>";
		td.html(html);

		// new field generated, has to be looked up
		input = $("#" + inputId);

		input.keydown(function(e) {
			if (e.keyCode == 27) {
				// ESC key pressed
				input.off("blur");
				td.html(oldPrefix);
			} else if (e.keyCode == 13) {
				// enter key pressed
				input.trigger("blur");
			}
		});

		input.focus();

		input.on("blur", function() {
			var newPrefix = input.val();
			if (newPrefix == oldPrefix) {
				td.html(newPrefix);
			} else {
				var namespace = td.next().text();
				newPrefixEscaped = escape(newPrefix);
				namespaceEscaped = escape(namespace);
				var data = keyName + "=" + newPrefixEscaped + "&" + valueName + "=" + namespaceEscaped;
				$.ajax({
					url: url,
					type: "POST",
					async: false,
					data: data,
					error: function(jqXHR, textStatus, errorThrown) {
						vShowAJAXError("Could not update data", jqXHR, errorThrown);
						input.focus();
					},
					success: function(data, textSTatus, jqXHR) {
						vShowSuccess("Successfully updated data");
						td.html(newPrefix);
					}
				});
			}
		});
	};

	return f;
}

/**
 * This function is also availble at winery-support-common.js
 * This function here is due to legacy reasons and all callers should move to winery-support-common.js
 *
 * @param qname a QName in the form {namespace}localname
 * @return { namespace: namespace, localname: localname }
 */
function getNamespaceAndLocalNameFromQName(qname) {
	var i = qname.indexOf("}");
	var res = {
		namespace : qname.substr(1,i-1),
		localname : qname.substr(i+1)
	};
	return res;
}

/**
 * Converts a QName of the form {namespace}id to the form prefix:id
 * with a lookup of the prefix in the element
 *
 * returns wrong data if prefix wsa not found
 */
function getQNameOutOfFullQName(fullQname, element) {
	var nsAndId = getNamespaceAndLocalNameFromQName(fullQname);
	var prefix = element.lookupPrefix(nsAndId.namespace);
	var qname = prefix + ":" + nsAndId.localname;
	return qname;
}

/**
 * Converts a QName of the form prefix:id to the form {namespace}id
 * with a lookup of the prefix in the element
 *
 * Currently not used anywhere
 */
function getFullQNameOutOfQName(qname, element) {
	var i = qname.indexOf(":");
	var prefix = qname.substr(0,i-1);
	var localname = qname.substr(i+1);
	var namespace = element.lookupPrefix(prefix);
	return "{" + namespace + "}" + localname;
}

function encodeID(id) {
	// the URL sent to the server should be the encoded id
	id = encodeURIComponent(id);
	// therefore, we have to encode it twice
	id = encodeURIComponent(id);
	return id;
}

// URLs from QName are provided by winery-support-common.js
function makeArtifactTemplateURL(repoURL, namespace, id) {
	return repoURL + "/artifacttemplates/" + encodeID(namespace) + "/" + encodeID(id) + "/";
}

if (!window.winery) window.winery = {};
window.winery.BOOTSTRAP_ANIMATION_DURATION = 400;
