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
/*

Script for dependent selection boxes.

One object for stating a map from value to content. The value is globally unique.

Verbose example:
<script>

var WSDLoperationsData = {
    "ns1" : {
        "options" : ["ns1:pt11", "ns1:pt12"],
    },

    "ns2" : {
        "options" : ["ns2:pt21"],
    },

    "ns3" : {
        "options" : ["ns3:pt31"],
    },

    "ns1:pt11" : {
        "label" : "PortType 1.1",
        "options" : ["ns1:pt11:op111", "ns1:pt11:op112"]
    },

    "ns1:pt12" : {
        "label" : "PortType 1.2",
        "options" : ["ns1:pt12:op113", "ns1:pt11:op114"]
    },


    "ns2:pt21" : {
        "label" : "PortType 2.1",
        "options" : ["ns2:pt21:op211", "ns2:pt21:op212"]
    },


    "ns3:pt31" : {
        "label" : "PortType 3.1",
        "options" : ["ns3:pt31:op311", "ns3:pt31:op312"]
    },


    "ns1:pt11:op111" : {
        "label" : "operation 1.1.1",
    },

    "ns1:pt11:op112" : {
        "label" : "operation 1.1.2",
    },

    "ns1:pt12:op113" : {
        "label" : "operation 1.1.3",
    },

    "ns1:pt12:op114" : {
        "label" : "operation 1.1.4",
    },

    "ns2:pt21:op211" : {
        "label" : "operation 2.1.1",
    },

    "ns2:pt21:op212" : {
        "label" : "operation 2.1.2",
    },

    "ns3:pt31:op311" : {
        "label" : "operation 3.1.1",
    },

    "ns3:pt31:op312" : {
        "label" : "operation 3.1.2",
    }
}

var WSDLdependendSelects = {
        "#portTypes" : "#operations"
}
</script>

<select size="15" onchange="updateListContent(this.value, '#portTypes', WSDLdependendSelects, WSDLoperationsData);" >
    <option value="ns1" selected="true" >Namespace1</option>
    <option value="ns2" >Namespace2</option>
    <option value="ns3" >Namespace3</option>
</select>

<select id="portTypes" size="15" onchange="updateListContent(this.value, '#operations', WSDLdependendSelects, WSDLoperationsData);">
    <option value="ns1:pt11" selected="true">PortType1.1</option>
    <option value="ns1:pt11">PortType1.2</option>
</select>

<select id="operations" size="15">
    <option value="ns1:pt11:op111" selected="true">op1.1.2</option>
    <option value="ns1:pt12:op112">op1.1.2</option>
    <option value="ns1:pt13:op113">op1.1.2</option>
</select>

 */

/**
 *
 * @param value the current selected value
 * @param targetElement the select to update
 * @param dependendSelects the data structure for subsequently dependent select elements
 * @param completeData the data structure with the complete data
 */
function updateListContent(value, targetElement, dependendSelects, completeData) {
    jQuery(targetElement).empty();
    var listData = completeData[value];
    if (listData !== undefined) {
        for (var i=0; i < listData.options.length; i++) {
            var optionName = listData.options[i];
            var label = completeData[optionName].label;
            var selected;
            if (i == 0) {
                selected = ' selected="selected"';
            } else {
                selected = '';
            }
            var toAppend = '<option value="' + optionName + '"' + selected + '>' + label + '</option>';
            jQuery(targetElement).append(toAppend);
        }
        var nextSelect = dependendSelects[targetElement];
        if (nextSelect !== undefined) {
            // We assume listData is not empty
            updateListContent(listData.options[0], nextSelect, dependendSelects, completeData);
        }
    }
    jQuery(targetElement).trigger("change");
}
