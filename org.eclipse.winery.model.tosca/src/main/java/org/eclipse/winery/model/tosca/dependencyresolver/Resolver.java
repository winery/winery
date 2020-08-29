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

package org.eclipse.winery.model.tosca.dependencyresolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.Abstract.Language;
import org.eclipse.winery.model.tosca.dependencyresolver.Docker.Dockerfile;

/**
 * @author jery
 */
public class Resolver {

    // Folder Name, used by this framework
    static public final String folder = "References_resolver" + File.separator;

    // Active languages
    List<Language> languages;

    /**
     * Constructor
     */
    public Resolver() {
        this.languages = new LinkedList<>();
    }

    /**
     * Test, creates resolver with Bash language
     */
    public static void main(final String[] args) throws IOException {

        String source, target;
        final Resolver resolver = new Resolver();
        if (args.length >= 1) {
            source = args[0];
        } else {
            System.out.print("enter the input CSAR name (default: example.csar): ");
            source = new Scanner(System.in).nextLine();
            if (source.equals("")) {
                source = "example.csar";
            }
        }
        if (args.length >= 2) {
            target = args[1];
        } else {
            System.out.print("enter the output CSAR name (default: newexample.csar): ");
            target = new Scanner(System.in).nextLine();
            if (target.equals("")) {
                target = "newexample.csar";
            }
        }
        System.out.println("source: " + source);
        System.out.println("target: " + target);
        resolver.proceedCSAR(source, target);

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("END: " + timestamp);
    }

    /**
     * proceed CSAR
     *
     * @param filename input CSAR name
     * @param output   output CSAR name
     */
    public void proceedCSAR(final String filename, final String output) throws IOException {
        if (filename == null || output == null) {
            throw new NullPointerException();
        }

        System.out.println("Proceeding file " + filename);
        CSAR_handler ch;
        try {
            // create CSAR manager and unpack archive
            ch = new CSAR_handler(filename);

            // init Languages
            this.languages.add(new Dockerfile(ch));
            /*
             * TODO Node type. done Artifact Type for package. done Relationship Type for dependencies change
             * service template
             */
            // new File(ch.getFolder() + CSAR_handler.Definitions).mkdirs();
            // RR_PackageArtifactType.init(ch);
            // RR_ScriptArtifactType.init(ch);
            // RR_AnsibleArtifactType.init(ch);
            // RR_PreDependsOn.init(ch);
            // RR_DependsOn.init(ch);
        } catch (final FileNotFoundException e) {
            System.out.println("Error by unpacking " + filename + ", file not found");
            return;
        }
        // proceed all extracted files using each available language
        try {
            for (final Language l : this.languages) {
                l.proceed();
            }
        } catch (final JAXBException e1) {
            System.out.println("Unable to create xml annotation to package");
            e1.printStackTrace();
        }
        // pack CSAR
        try {
            ch.pack(output);
            System.out.println("packed to: " + output);
        } catch (final FileNotFoundException e) {
            System.out.println("File: not found during packing to: " + output);
            return;
        }
    }
}
