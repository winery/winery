/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import javax.xml.namespace.QName;

import org.eclipse.winery.topologymodeler.addons.topologycompleter.topologycompletion.CompletionInterface;

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
