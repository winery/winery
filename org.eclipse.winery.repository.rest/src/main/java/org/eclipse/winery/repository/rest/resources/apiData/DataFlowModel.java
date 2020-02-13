/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(name = "DataFlowModel")
public class DataFlowModel {

    @XmlAttribute
    public QName id;

    @XmlElementWrapper(name = "Pipes")
    @XmlElement(name = "Pipe")
    public List<Pipes> pipes;

    @XmlElementWrapper(name = "Filters")
    @XmlElement(name = "Filter")
    public List<Filter> filters;

    public DataFlowModel() {
    }

    public QName getId() {
        return id;
    }

    public List<Pipes> getPipes() {
        return pipes;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * Internal class to represent the filters of the extended Pipes and Filters pattern for data flow models.
     */
    public static class Filter {

        @XmlElementWrapper(name = "Properties")
        public Map<String, String> properties;

        @XmlElementWrapper(name = "Artifacts")
        @XmlElement(name = "Artifact")
        public List<QName> artifacts;

        @XmlAttribute
        private String id;

        @XmlAttribute
        private QName type;

        @XmlAttribute
        private String location;

        @XmlAttribute
        private String provider;

        public Filter() {
        }

        public String getId() {
            return id;
        }

        public QName getType() {
            return type;
        }

        public String getLocation() {
            return location;
        }

        public String getProvider() {
            return provider;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

        public List<QName> getArtifacts() {
            return artifacts;
        }
    }

    /**
     * Internal class to represent the pipes of the extended Pipes and Filters pattern for data flow models.
     */
    public static class Pipes {

        @XmlElement(name = "Source")
        public String source;

        @XmlElement(name = "Target")
        public String target;

        @XmlAttribute
        private String dataTransferType;

        public Pipes() {
        }

        public String getDataTransferType() {
            return dataTransferType;
        }

        public String getSource() {
            return source;
        }

        public String getTarget() {
            return target;
        }
    }
}
