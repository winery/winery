/*******************************************************************************
 * Copyright (c) 2012-2016 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Niko Stadelmaier - removal of select2 library
 *******************************************************************************/

/**
 * Functions copied from winery-common.js to replace it in the long term
 *
 * Shared between topology modeler and repository
 */
define([], function() {
		var xmlParser = new DOMParser();
		var VALUE_OF_NONE_CHOOSEN = "(none)"; // constant to indicate that nothing is chosen in a select2

		return {
			encodeId: encodeId,
			getNamespaceAndLocalNameFromQName: getNamespaceAndLocalNameFromQName,
			replaceDialogShownHookForOrionUpdate: replaceDialogShownHookForOrionUpdate,
			writeCollectionDefinedByATextArea: writeCollectionDefinedByATextArea,
			getDocument: getDocument,

			getURLFragmentOutOfFullQName: getURLFragmentOutOfFullQName,
			makeArtifactTypeURLFromQName: makeArtifactTypeURLFromQName,
			makeNodeTypeURLFromQName: makeNodeTypeURLFromQName,
			makeRelationshipTypeURLFromQName: makeRelationshipTypeURLFromQName,
			makeRelationshipTypeURLFromNSAndLocalName: makeRelationshipTypeURLFromNSAndLocalName,

			qname2href: qname2href,

			fetchSelect2DataAndInitSelect2: fetchSelect2DataAndInitSelect2,
			removeItemFromSelect2Field: removeItemFromSelect2Field,

			checkXMLValidityAndShowErrorIfInvalid: checkXMLValidityAndShowErrorIfInvalid,
			synchronizeNameAndType: synchronizeNameAndType,

			VALUE_OF_NONE_CHOOSEN: VALUE_OF_NONE_CHOOSEN
		};

		/**
		 * OriginalName: encodeID
		 */
		function encodeId(id) {
			// the URL sent to the server should be the encoded id
			id = encodeURIComponent(id);
			// therefore, we have to encode it twice
			id = encodeURIComponent(id);
			return id;
		}

		/**
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
		 * Orion does not update content if field not fully shown
		 * therefore, we hook in into the "shown" event
		 */
		function replaceDialogShownHookForOrionUpdate(diag, orionAreaId, content) {
			diag.off("shown.bs.modal");
			diag.on("shown.bs.modal", function() {
				var area = window.winery.orionareas[orionAreaId];
				area.editor.setText(content);
				area.fixEditorHeight();
			});
		}

		function getURLFragmentOutOfNSAndLocalName(nsAndLocalName) {
			var res;
			res = encodeID(nsAndLocalName.namespace);
			res = res + "/";
			res = res + encodeID(nsAndLocalName.localname);
			return res;
		}

		/**
		 * Extracts an URL fragment of the form <encoded namespace>/<encoded id> out of a full QName
		 *
		 * @param qname a QName in the form {namespace}localname
		 */
		function getURLFragmentOutOfFullQName(qname) {
			var d = getNamespaceAndLocalNameFromQName(qname);
			return getURLFragmentOutOfNSAndLocalName(d);
		}

		/**
		 * @param w the XMLwriter
		 * @param elementSet the set of HTML elements to write
		 * @param elementName the name of the wrapper element (e.g., "Requirements", "Policies")
		 */
		function writeCollectionDefinedByATextArea(w, elementSet, elementName) {
			if (elementSet.length !== 0) {
				w.writeStartElement(elementName);
				elementSet.each(function(i, element) {
					// XML contains element completely
					// we do not have to parse reqorcap.children("div.id").children("span.id").text() or the span.name
					var text = $(element).children("textarea").val();
					w.writeXML(text);
				});
				w.writeEndElement();
			}
		}

		function makeArtifactTypeURLFromQName(repoURL, qname) {
			return repoURL + "/artifacttypes/" + getURLFragmentOutOfFullQName(qname) + "/";
		}

		function makeNodeTypeURLFromQName(repoURL, qname) {
			return repoURL + "/nodetypes/" + getURLFragmentOutOfFullQName(qname) + "/";
		}

		function makeRelationshipTypeURLFromQName(repoURL, qname) {
			return repoURL + "/relationshiptypes/" + getURLFragmentOutOfFullQName(qname) + "/";
		}

		function makeRelationshipTypeURLFromNSAndLocalName(repoURL, nsAndLocalName) {
			return repoURL + "/relationshiptypes/" + getURLFragmentOutOfNSAndLocalName(nsAndLocalName) + "/";
		}

		/**
		 * functionality similar to org.eclipse.winery.common.Util.qname2href(String, Class<? extends TExtensibleElements>, QName)
		 */
		function qname2href(repositoryUrl, componentPathFragment, qname) {
			var nsAndId = getNamespaceAndLocalNameFromQName(qname);
			var absoluteURL = repositoryUrl + "/" + componentPathFragment + "/" + getURLFragmentOutOfNSAndLocalName(nsAndId);
			var res = "<a target=\"_blank\" data-qname=\"" + qname + "\" href=\"" + absoluteURL + "\">" + nsAndId.localname + "</a>";
			return res;
		}

		/**
		 * Inspired by
		 *
		 * @param field is the jquery field
		 * @param id_to_remove the id to remove
		 */
		function removeItemFromSelect2Field(field, id_to_remove) {
			// nothing can be done currently
			// see https://github.com/ivaynberg/select2/issues/535#issuecomment-30210641 for a disucssion
			vShowNotification("The select field shows stale data. Refresh the page to get rid of that.")
		}
	
		/**
		 * Fetches select2 data from the given URL and initializes the field provided by the fieldId
		 *
		 * Calls vShowError if something went wrong
		 *
		 * @param onSuccess (optional)
		 * @param allowAdditions (optional) if set to true, select2 is initalized with the functionality to allow additions during the search
		 */
		function fetchSelect2DataAndInitSelect2(fieldId, url, onSuccess, allowAdditions) {
			$.ajax({
				url: url,
				dataType: "json"
			}).done(function (result) {

				var convResult = result.map(function(item){
					return {id: item.id, name: item.text};
				});
				var params = {"source": convResult,
					showHintOnFocus: "all"
				};
				if (typeof allowAdditions === "boolean") {
					// params.createSearchChoice = function(term) {
					// 	// enables creation of new namespaces
					// 	return {id:term, text:term};
					// }
				}
				// console.log("params", params);
				// init select2 and select first item
				require(["bootstrap3-typeahead"], function() {
					$("#" + fieldId).typeahead('destroy');
					$("#" + fieldId).typeahead(params);
					if (result.length === 0) {
						$("#" + fieldId).val("");
					} else {
						$("#" + fieldId).val(result[0].id);
						$("#" + fieldId).typeahead("lookup", result[0].name);
					}
				});

				if (typeof onSuccess === "function") {
					onSuccess();
				}
			}).fail(function(jqXHR, textStatus, errorThrown) {
				vShowAJAXError("Could not fetch select2 data from " + url, jqXHR, errorThrown);
			});

		}


		function getDocument(xmlString) {
			var xmlDoc = xmlParser.parseFromString(xmlString, "text/xml");
			return xmlDoc;
		}

		/**
		 * Checks given XML string for validity. If it's invalid, an error message is shown
		 * Relies on the current browser's XML handling returning a HTML document if something went wrong during parsing
		 *
		 * @return XMLdocument if XML is valid, false otherwise
		 */
		function checkXMLValidityAndShowErrorIfInvalid(xmlString) {
			var doc = getDocument(xmlString);
			var errorMsg = "";
			if (doc.firstChild.localName == "html") {
				errorMsg = new XMLSerializer().serializeToString(doc);
			} else {
				// at Chrome, the error may be nested in the XML

				// quick hack, only "xhtml" is quered
				function nsResolover(x) {
					return "http://www.w3.org/1999/xhtml";
				}

				var element = doc.evaluate( '//x:parsererror', doc, nsResolover, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;
				if (element !== null) {
					errorMsg = new XMLSerializer().serializeToString(element);
				}
			}

			if (errorMsg !== "") {
				vShowError(errorMsg);
				return false;
			} else {
				return doc;
			}
		}

		/**
		 * Updates the XML in the orion editor based on the values given in the input fields.
		 * Shows error if XML is invalid
		 *
		 * changed to work with standard html select fields - select2 has been removed
		 *
		 * @param idPrefix: (new|edit)${shortName}, derived names: [idPrefix]Name, [idPrefix]Id, Orion[idPrefix]XML
		 * @param hasIdField: whether the Id should be read and written
		 * @param selectFields: array of {attribute, fieldSuffix}, where attribute is a name of an attribute having a QName.
		 * 		Each select box is determined by #[idPrefix][fieldSuffix].
		 * 		The select box content is converted to a QName and the result is written to the attribute [name]
		 * 		Default: {attribute: "type", fieldSuffix: "Type"}
		 *
		 * @return false if XML is invalid, true: an object if id/name/attribute1/attribute2/... (qname + attribute1FullQName: qname object)/xml (to be used in tmpl-${cssClassPrefix})
		 * 	{id:id, name:name, type: "ns5:type", typeFullQName: "{http://www.example.org}type"}
		 */
		function synchronizeNameAndType(idPrefix, hasIdField, selectFields) {
			if (typeof hasIdField === undefined) {
				hasIdField = true;
			}
			if (typeof selectFields === undefined) {
				selectFields = [{attribute: "type", fieldSuffix: "Type"}];
			}


			var val = window.winery.orionareas["Orion" + idPrefix + "XML"].editor.getText();
			var xmlDoc = checkXMLValidityAndShowErrorIfInvalid(val);
			if (xmlDoc) {
				// handle name
				var name = $("#" + idPrefix + "Name").val();
				// initialize result object
				var res = {
					name: name
				};
				xmlDoc.firstChild.setAttribute("name", name);

				// write id and name to XML
				if (hasIdField) {
					var id = $("#" + idPrefix + "Id").val();
					if (!id) {
						// TODO a checking should be done if the id exists
						// probably not here, but at caller's side
						id = name;
					}
					xmlDoc.firstChild.setAttribute("id", id);
					res.id = id;
				}

				// write each selectField to xml
				// for that, we have to determine the QName
				$(selectFields).each(function(i, m) {

					var content = $("#" + idPrefix + m.fieldSuffix).val();

					if (content == VALUE_OF_NONE_CHOOSEN) {
						// if nothing is chosen do not put it into the result
						return;
					}
					// determine qname of type
					//getQNameOutOfFullQName(type, xmlDoc.firstChild) does not always work as xmlDoc.firstChild does not have ALL *available* namespace prefixes
					var typeNSAndId = getNamespaceAndLocalNameFromQName(content);
					var prefix = xmlDoc.firstChild.lookupPrefix(typeNSAndId.namespace);
					if (!prefix) {
						// we have to ask the repo for a prefix
						$.ajax({
							type: "GET",
							async: false,
							"url": winery.repositoryURL + "/admin/namespaces/" + encodeID(typeNSAndId.namespace),
							dataType: "text",
							error: function(jqXHR, textStatus, errorThrown) {
								vShowAJAXError("Could not determine prefix", jqXHR, errorThrown);
							},
							success: function(resData, textStatus, jqXHR) {
								prefix = resData;
							}
						});
						// new prefix fetched, xmlns attribute has to be written
						xmlDoc.firstChild.setAttribute("xmlns:" + prefix, typeNSAndId.namespace);
					}
					var qname = prefix + ":" + typeNSAndId.localname;
					res[m.attribute] = qname;
					res[m.attribute + "FullQName"] = typeNSAndId;
					xmlDoc.firstChild.setAttribute(m.attribute, qname);
				});

				var xml = new XMLSerializer().serializeToString(xmlDoc);
				res.xml = xml;

				return res;
			} else {
				return false;
			}
		}


	}
);
