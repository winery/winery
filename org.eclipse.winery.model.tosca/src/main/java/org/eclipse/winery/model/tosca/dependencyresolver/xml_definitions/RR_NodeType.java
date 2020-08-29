/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.dependencyresolver.CSAR_handler;

/**
 * @author jery TOSCA
 */
public class RR_NodeType {

    /**
     * Creates xml description for my NodeType
     */
    public static void createNodeType(CSAR_handler ch, String packet) throws JAXBException, IOException {
        File dir = new File(ch.getFolder() + CSAR_handler.Definitions);
        dir.mkdirs();
        File temp = new File(ch.getFolder() + CSAR_handler.Definitions + getFileName(packet));
        if (temp.exists())
            temp.delete();
        temp.createNewFile();
        OutputStream output = new FileOutputStream(ch.getFolder() + CSAR_handler.Definitions + getFileName(packet));

        JAXBContext jc = JAXBContext.newInstance(Definitions.class);

        Definitions shema = new Definitions();

        shema.id = "winery-defs-for_" + getTypeName(packet);
        shema.import_impl = new RR_Import("http://opentosca.org/nodetypeimplementations",
            RR_TypeImplementation.getFileName(packet), "http://docs.oasis-open.org/tosca/ns/2011/12");
        shema.nodeType.name = getTypeName(packet);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(shema, output);
        ch.metaFile.addFileToMeta(CSAR_handler.Definitions + getFileName(packet),
            "application/vnd.oasis.tosca.definitions");
    }

    public static String getTypeName(String packet) {
        return "RR_NT_" + packet;
    }

    public static String getFileName(String packet) {
        return "RR_NT_" + packet + ".tosca";
    }

    /**
     * @author Yaroslav PackageType XML description
     */
    @XmlRootElement(name = "tosca:Definitions")
    @XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
    public static class Definitions {

        @XmlAttribute(name = "xmlns:tosca", required = true)
        public static final String tosca = "http://docs.oasis-open.org/tosca/ns/2011/12";
        @XmlAttribute(name = "xmlns:winery", required = true)
        public static final String winery = "http://www.opentosca.org/winery/extensions/tosca/2013/02/12";
        @XmlAttribute(name = "xmlns:ns1", required = true)
        public static final String ns1 = "http://www.eclipse.org/winery/model/selfservice";
        @XmlAttribute(name = "targetNamespace", required = true)
        public static final String targetNamespace = "http://opentosca.org/nodetypes";
        @XmlElement(name = "tosca:Import", required = true)
        public RR_Import import_impl;
        @XmlElement(name = "tosca:NodeType", required = true)
        public NodeType nodeType;
        @XmlAttribute(name = "id", required = true)
        public String id;

        public Definitions() {
            nodeType = new NodeType();
        }

        public static class NodeType {
            @XmlAttribute(name = "targetNamespace", required = true)
            public static final String targetNamespace = "http://opentosca.org/nodetypes";
            @XmlAttribute(name = "winery:bordercolor", required = true)
            public static final String bordercolor = "#802fb8"; // TODO
            @XmlAttribute(name = "name", required = true)
            public String name;
            @XmlElement(name = "tosca:Interfaces", required = true)
            public Interfaces interfaces;

            NodeType() {
                interfaces = new Interfaces();
            }

            public static class Interfaces {

                @XmlElement(name = "tosca:Interface", required = true)
                public Interface tInterface;

                Interfaces() {
                    tInterface = new Interface();
                }

                public static class Interface {
                    @XmlAttribute(name = "name", required = true)
                    public static final String name = "http://www.example.com/interfaces/lifecycle";
                    @XmlElement(name = "tosca:Operation", required = true)
                    public Operation operation;

                    Interface() {
                        operation = new Operation();
                    }

                    public static class Operation {
                        @XmlAttribute(name = "name", required = true)
                        public static final String name = "install";
                    }
                }
            }
        }
    }
}
