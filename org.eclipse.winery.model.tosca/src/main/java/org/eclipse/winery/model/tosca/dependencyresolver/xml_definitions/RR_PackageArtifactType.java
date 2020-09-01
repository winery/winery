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
 * @author jery PackageArtifactType description
 */
public class RR_PackageArtifactType {

    // output filename
    public static final String filename = "RR_PackageArtifact.tosca";

    /**
     * Create PackageArtifactType xml description
     */
    public static void init(CSAR_handler ch) throws JAXBException, IOException {
        File dir = new File(ch.getFolder() + CSAR_handler.Definitions);
        dir.mkdirs();
        File temp = new File(ch.getFolder() + CSAR_handler.Definitions + filename);
        if (temp.exists())
            temp.delete();
        temp.createNewFile();
        OutputStream output = new FileOutputStream(ch.getFolder() + CSAR_handler.Definitions + filename);

        JAXBContext jc = JAXBContext.newInstance(Definitions.class);

        Definitions shema = new Definitions();

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(shema, output);
        ch.metaFile.addFileToMeta(CSAR_handler.Definitions + filename,
            "application/vnd.oasis.tosca.definitions");
    }

    /**
     * @author Yaroslav Package Artifact Type XML description
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
        @XmlAttribute(name = "id", required = true)
        public static final String id = "winery-defs-RR_script_artifact_type";
        @XmlAttribute(name = "targetNamespace", required = true)
        public static final String targetNamespace = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes";
        @XmlElement(name = "tosca:ArtifactType", required = true)
        public ArtifactType artifactType;

        public Definitions() {
            artifactType = new ArtifactType();
        }

        public static class ArtifactType {
            @XmlAttribute(name = "name", required = true)
            public static final String name = "RR_PackageArtifact";
            @XmlAttribute(name = "targetNamespace", required = true)
            public static final String targetNamespace = "http://docs.oasis-open.org/tosca/ns/2011/12/ToscaBaseTypes"; // TODO

            ArtifactType() {
            }
        }
    }
}
