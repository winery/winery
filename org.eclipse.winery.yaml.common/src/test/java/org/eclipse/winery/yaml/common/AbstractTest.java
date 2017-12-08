/********************************************************************************
 * Copyright (c) 2017 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.yaml.common;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.reader.yaml.Reader;
import org.eclipse.winery.yaml.common.writer.yaml.Writer;

import org.eclipse.jdt.annotation.NonNull;

public abstract class AbstractTest {
    protected static Path path;
    private final org.eclipse.winery.yaml.common.reader.xml.Reader xmlReader;
    private final Reader yamlReader;
    private final Writer yamlWriter;

    protected AbstractTest() {
        this.yamlReader = Reader.getReader();
        this.yamlWriter = new Writer();
        this.xmlReader = new org.eclipse.winery.yaml.common.reader.xml.Reader();
    }

    public static Path getYamlFile(String name) {
        return getFile(".yml", name);
    }

    public static Path getXmlFile(String name) {
        return getFile(".xml", name);
    }

    public static Stream<Path> getYamlFiles() throws Exception {
        return getFiles(".yml");
    }

    public static Stream<Path> getYamlFiles(Stream<String> names) throws Exception {
        return getFiles(".yml", names);
    }

    public static Stream<Path> getXmlFiles() throws Exception {
        return getFiles(".xml");
    }

    public static Stream<Path> getXmlFiles(Stream<String> names) throws Exception {
        return getFiles(".xml", names);
    }

    public static Path getFile(String suffix, String name) {
        return Paths.get(name.concat(suffix));
    }

    public static Stream<Path> getFiles(String suffix) throws Exception {
        return Files.walk(path)
            .filter(p1 -> p1.getFileName().toString().endsWith(suffix))
            .map(p -> path.relativize(p).toString())
            // Sorted stream (subdirectories first)
            .sorted((s1, s2) -> s1.contains(File.separator) ? -1 : s2.contains(File.separator) ? 1 : s1.compareTo(s2))
            .map(Paths::get);
    }

    public static Stream<Path> getFiles(String suffix, String name) throws Exception {
        return getFiles(suffix, Stream.of(name));
    }

    public static Stream<Path> getFiles(String suffix, Stream<String> names) throws Exception {
        return names.map(name -> Paths.get(name.concat(suffix)));
    }

    public void writeYamlServiceTemplate(TServiceTemplate serviceTemplate, Path path) {
        yamlWriter.write(serviceTemplate, path);
    }

    public Definitions getXmlDefinitions(Path name) throws Exception {
        return xmlReader.parse(path.resolve(name));
    }

    public TServiceTemplate getYamlServiceTemplate(Path name) throws MultiException {
        return yamlReader.parse(path, name);
    }

    public TServiceTemplate getYamlServiceTemplate(Path name, Path path) throws MultiException {
        return yamlReader.parse(path, name);
    }

    @NonNull
    public Metadata getMetadata(Path name) throws MultiException {
        return yamlReader.getMetadata(path, name);
    }
}
