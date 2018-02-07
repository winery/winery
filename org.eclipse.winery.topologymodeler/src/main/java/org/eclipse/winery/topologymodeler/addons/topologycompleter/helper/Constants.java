/*******************************************************************************
 * Copyright (c) 2013 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.CompletionInterface;

import javax.xml.namespace.QName;

/**
 * This class contains several constants used by the completion add-on.
 */
public class Constants {

    /**
     * Constant for the QName of the "deferred" type.
     */
    public static final QName DEFERRED_QNAME = new QName("http://www.opentosca.org", "deferred");

    /**
     * Constant for the QName of the "PlaceHolder" type.
     */
    public static final QName PLACE_HOLDER_QNAME = new QName("http://www.opentosca.org", "PlaceHolder");

    /**
     * Contains possible types of expandable place holders.
     */
    public enum PlaceHolders {
        WEBSERVER, DATABASE, OPERATINGSYSTEM, CLOUDPROVIDER;

        /**
         * Overwritten toString() method to return formatted strings.
         */
        public String toString() {

            switch (this) {
                case WEBSERVER:
                    return "Webserver";
                case DATABASE:
                    return "Database";
                case OPERATINGSYSTEM:
                    return "OperatingSystem";
                case CLOUDPROVIDER:
                    return "CloudProvider";
                default:
                    return null;
            }
        }
    }

    /**
     * The messages returned by the {@link CompletionInterface}
     */
    public enum CompletionMessages {
        TOPOLOGYCOMPLETE, USERINTERACTION, STEPBYSTEP, SUCCESS, USERTOPOLOGYSELECTION, FAILURE;

        /**
         * Overwritten toString() method to return formatted strings.
         */
        public String toString() {

            switch (this) {
                case TOPOLOGYCOMPLETE:
                    return "topologyComplete";
                case USERINTERACTION:
                    return "userInteraction";
                case STEPBYSTEP:
                    return "stepByStep";
                case SUCCESS:
                    return "success";
                case USERTOPOLOGYSELECTION:
                    return "userTopologySelection";
                case FAILURE:
                    return "failure";
                default:
                    return null;
            }
        }
    }
}
