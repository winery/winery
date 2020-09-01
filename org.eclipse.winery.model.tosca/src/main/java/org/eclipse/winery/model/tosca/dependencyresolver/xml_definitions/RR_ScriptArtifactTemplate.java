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
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.model.tosca.dependencyresolver.CSAR_handler;
import org.eclipse.winery.model.tosca.dependencyresolver.Package_Handler;
import org.eclipse.winery.model.tosca.dependencyresolver.Resolver;
import org.eclipse.winery.model.tosca.dependencyresolver.Utils;

/**
 * @author Yaroslav Script Artifact Template for packages
 */
public class RR_ScriptArtifactTemplate {
    public static final String extension = "_IA.tosca";

    /**
     * Create template for package
     */
    public static void createScriptArtifact(CSAR_handler ch, String packet)
        throws IOException, JAXBException {
        System.out.println("creating Script Template ");

        File temp = new File(ch.getFolder() + CSAR_handler.Definitions + getFileName(packet));
        if (temp.exists())
            temp.delete();
        temp.createNewFile();
        OutputStream output = new FileOutputStream(temp);

        JAXBContext jc = JAXBContext.newInstance(Definitions.class);

        Definitions template = new Definitions();

        template.id = "winery-defs-for_" + getIAName(packet);
        template.artifactTemplate.id = getIAName(packet);
        template.artifactTemplate.artifactReferences.artifactReference.reference = getScriptPosition(packet);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(template, output);

        ch.metaFile.addFileToMeta(CSAR_handler.Definitions + getFileName(packet), "application/vnd.oasis.tosca.definitions");

        Utils.createFile(ch.getFolder() + getScriptPosition(packet), ScriptContent(packet));
        Runtime.getRuntime().exec("chmod +x " + ch.getFolder() + getScriptPosition(packet));
        ch.metaFile.addFileToMeta(ch.getFolder() + getScriptPosition(packet), "application/x-sh");
    }

    /**
     * Create template for package
     */
    public static void createScriptArtifact(CSAR_handler ch, String source, List<String> packages)
        throws IOException, JAXBException {
        System.out.println("creating Script Template ");

        File temp = new File(ch.getFolder() + CSAR_handler.Definitions + getFileName(source));
        if (temp.exists())
            temp.delete();
        temp.createNewFile();
        OutputStream output = new FileOutputStream(temp);

        JAXBContext jc = JAXBContext.newInstance(Definitions.class);

        Definitions template = new Definitions();

        template.id = "winery-defs-for_" + getIAName(source);
        template.artifactTemplate.id = getIAName(source);
        template.artifactTemplate.artifactReferences.artifactReference.reference = getScriptPosition(source);

        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(template, output);

        ch.metaFile.addFileToMeta(CSAR_handler.Definitions + getFileName(source), "application/vnd.oasis.tosca.definitions");

        Utils.createFile(ch.getFolder() + getScriptPosition(source), ScriptContent(packages));
        Runtime.getRuntime().exec("chmod +x " + ch.getFolder() + getScriptPosition(source));
        ch.metaFile.addFileToMeta(ch.getFolder() + getScriptPosition(source), "application/x-sh");
    }

    public static String getIAName(String packet) {
        return "RR_" + packet + "_IA";
    }

    public static String getFileName(String packet) {
        return "RR_" + packet + "_IA" + ".tosca";
    }

    /**
     * Content of script, which must install package
     */
    public static String ScriptContent(String packet) {
        return (String) "#!/bin/bash\n" +
            "\n" +
            "dpkg -i " + packet + Package_Handler.Extension + "\n";
    }

    public static String ScriptContent(List<String> packages) {
        String output = "#!/bin/bash\n" +
            "\n" +
            "dpkg -i ";
        for (String temp : packages)
            output += " " + temp + Package_Handler.Extension;
        output += "\n";
        return output;
    }

    public static String getScriptPosition(String packet) {
        return Resolver.folder + packet + File.separator + "RR_" + packet + ".sh";
    }

    /**
     * @author Yaroslav Script Artifact Template description
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
        public static final String targetNamespace = "http://opentosca.org/artifacttemplates"; //TODO
        @XmlElement(name = "tosca:Import", required = true)
        public RR_Import tImport;
        @XmlElement(name = "tosca:ArtifactTemplate", required = true)
        public ArtifactTemplate artifactTemplate;
        @XmlAttribute(name = "id", required = true)
        public String id;

        public Definitions() {
            artifactTemplate = new ArtifactTemplate();
            tImport = new RR_Import(RR_ScriptArtifactType.Definitions.ArtifactType.targetNamespace,
                RR_ScriptArtifactType.filename, "http://docs.oasis-open.org/tosca/ns/2011/12");
        }

        public static class ArtifactTemplate {

            @XmlAttribute(name = "xmlns:tbt", required = true)
            public static final String tbt = RR_ScriptArtifactType.Definitions.ArtifactType.targetNamespace;
            @XmlAttribute(name = "type", required = true)
            public static final String type = RR_ScriptArtifactType.Definitions.ArtifactType.name;
            @XmlElement(name = "tosca:ArtifactReferences", required = true)
            public ArtifactReferences artifactReferences;
            @XmlAttribute(name = "id", required = true)
            public String id;

            ArtifactTemplate() {
                artifactReferences = new ArtifactReferences();
            }

            public static class ArtifactReferences {

                @XmlElement(name = "tosca:ArtifactReference", required = true)
                public ArtifactReference artifactReference;

                ArtifactReferences() {
                    artifactReference = new ArtifactReference();
                }

                public static class ArtifactReference {
                    @XmlAttribute(name = "reference", required = true)
                    public String reference;

                    ArtifactReference() {
                    }
                }
            }
        }
    }
}
