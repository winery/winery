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
package org.eclipse.winery.yaml.common.reader.yaml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.Utils;
import org.eclipse.winery.yaml.common.exception.MissingFile;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.UnrecognizedFieldException;
import org.eclipse.winery.yaml.common.exception.YAMLParserException;
import org.eclipse.winery.yaml.common.validator.ObjectValidator;
import org.eclipse.winery.yaml.common.validator.Validator;
import org.eclipse.winery.yaml.common.validator.support.ExceptionInterpreter;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.scanner.ScannerException;

public class Reader {
    public static final Logger logger = LoggerFactory.getLogger(Builder.class);
    private static Reader INSTANCE;
    private Yaml yaml;

    private Map<Path, byte[]> hashBuffer = new HashMap<>();
    private Map<Path, TServiceTemplate> serviceTemplateBuffer = new HashMap<>();
    private Map<Path, MultiException> exceptionBuffer = new HashMap<>();

    private Reader() {
        this.yaml = new Yaml();
    }

    public static Reader getReader() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Reader();
        }
        return INSTANCE;
    }

    public TServiceTemplate parse(Path path, Path file) throws MultiException {
        return this.readServiceTemplate(path, file, Namespaces.DEFAULT_NS);
    }

    public TServiceTemplate parse(Path path, Path file, String namespace) throws MultiException {
        return this.readServiceTemplate(path, file, namespace);
    }

    public TServiceTemplate parse(TImportDefinition definition, Path path, String namespace) throws MultiException {
        return this.readImportDefinition(definition, path, namespace);
    }

    public TServiceTemplate parseSkipTest(Path uri, String namespace) throws YAMLParserException {
        return this.readServiceTemplateSkipTest(uri, namespace);
    }

    @NonNull
    public Metadata getMetadata(Path path, Path file) throws YAMLParserException {
        return readServiceTemplateMetadataSkipTest(path.resolve(file), Namespaces.DEFAULT_NS);
    }

    @NonNull
    public String getNamespace(Path path, Path file) throws YAMLParserException {
        return getMetadata(path, file).getOrDefault("targetNamespace", Namespaces.DEFAULT_NS);
    }

    private TServiceTemplate readServiceTemplateSkipTest(Path filePath, String namespace) throws YAMLParserException {
        Object object = readObject(filePath);
        return buildServiceTemplate(object, namespace);
    }

    @NonNull
    private Metadata readServiceTemplateMetadataSkipTest(Path filePath, String namespace) throws YAMLParserException {
        Object object = readMetadataObject(filePath);
        return Optional.ofNullable(buildServiceTemplate(object, namespace)).map(TServiceTemplate::getMetadata).orElse(new Metadata());
    }

    private TServiceTemplate buildServiceTemplate(Object object, String namespace) throws UnrecognizedFieldException {
        Builder builder = new Builder(namespace);
        return builder.buildServiceTemplate(object);
    }

    /**
     * Uses snakeyaml to convert a file into an Object
     *
     * @param path name
     * @return Object (Lists, Maps, Strings, Integers, Dates)
     * @throws MissingFile if the file could not be found.
     */
    private Object readObject(Path path) throws MissingFile {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return this.yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            MissingFile ex = new MissingFile("The file \"" + path + "\" could not be found!");
            ex.setFileContext(path.toString());
            throw ex;
        } catch (IOException e) {
            logger.error("Could not read from inputstream", e);
            return null;
        }
    }

    /**
     * Uses snakeyaml to convert the part of an file containing metadata into an Object
     *
     * @return Object (Lists, Maps, Strings, Integers, Dates)
     * @throws MissingFile if the file could not be found.
     */
    private Object readMetadataObject(Path path) throws MissingFile {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
            String metadata = buffer.lines().collect(Collectors.joining("\n"));
            Matcher matcher = Pattern.compile("\\nmetadata:").matcher(metadata);
            // No metadata return null
            if (!matcher.find()) return null;

            // Prevent index out of bound
            int index = matcher.start() + 1;
            if (index >= metadata.length()) return null;

            // Get file string starting with "metadata:"
            metadata = metadata.substring(matcher.start() + 1);
            matcher = Pattern.compile(("\\n[^ ]")).matcher(metadata);
            if (matcher.find()) {
                // Cut of the part of the file after metadata (indicated by newline and a key) 
                metadata = metadata.substring(0, matcher.start());
            }

            return this.yaml.load(metadata);
        } catch (FileNotFoundException e) {
            MissingFile ex = new MissingFile("The file \"" + path + "\" could not be found!");
            ex.setFileContext(path.toString());
            throw ex;
        } catch (IOException e) {
            logger.error("Could not read from inputstream", e);
            return null;
        }
    }

    /**
     * Checks if a file has been read before and is changed
     *
     * @return false if has been read and did not change else true
     */
    private boolean fileChanged(Path path) {
        try {
            byte[] hash = Utils.getHashValueOfFile(path.toFile());
            // No file changes
            if (hashBuffer.containsKey(path) && Arrays.equals(hashBuffer.get(path), hash)) {
                return false;
            } else {
                // File changed or is new
                hashBuffer.put(path, hash);
                return true;
            }
        } catch (IOException e) {
            // File is not readable
            return true;
        }
    }

    /**
     * Reads a file and converts it to a ServiceTemplate
     *
     * @return ServiceTemplate
     * @throws MultiException the ServiceTemplate or the file is invalid.
     */
    private TServiceTemplate readServiceTemplate(Path path, Path file, String namespace) throws MultiException {
        Path filePath;
        if (Objects.isNull(path)) {
            filePath = file;
        } else {
            filePath = path.resolve(file);
        }

        // 
        if (!fileChanged(filePath)) {
            if (exceptionBuffer.containsKey(filePath)) {
                throw exceptionBuffer.get(filePath);
            }
            if (serviceTemplateBuffer.containsKey(filePath)) {
                return serviceTemplateBuffer.get(filePath);
            }
        }

        logger.debug("Read Service Template: {}", filePath);
        try {
            // pre parse checking
            try {
                ObjectValidator objectValidator = new ObjectValidator();
                objectValidator.validateObject(readObject(filePath));
            } catch (ConstructorException e) {
                ExceptionInterpreter interpreter = new ExceptionInterpreter();
                throw new MultiException().add(interpreter.interpret(e));
            } catch (ScannerException e) {
                ExceptionInterpreter interpreter = new ExceptionInterpreter();
                throw new MultiException().add(interpreter.interpret(e));
            } catch (YAMLParserException e) {
                throw new MultiException().add(e);
            }

            // parse checking
            TServiceTemplate result;
            try {
                result = buildServiceTemplate(readObject(filePath), namespace);
            } catch (YAMLParserException e) {
                throw new MultiException().add(e).add(filePath.toString());
            }

            // post parse checking
            Validator validator = new Validator(path);
            try {
                validator.validate(result, namespace);
            } catch (MultiException e) {
                e.add(filePath.toString());
                throw e;
            }

            serviceTemplateBuffer.put(filePath, result);
            return result;
        } catch (MultiException e) {
            exceptionBuffer.put(filePath, e);
            throw e;
        }
    }

    public TServiceTemplate readImportDefinition(TImportDefinition definition, Path path, String namespace) throws MultiException {
        if (definition == null) {
            return null;
        }

        String importNamespace = definition.getNamespaceUri() == null ? namespace : definition.getNamespaceUri();
        if (definition.getRepository() == null) {
            return readServiceTemplate(path, Paths.get(definition.getFile()), importNamespace);
        } else {
            // TODO Support Repositories
            return null;
        }
    }
}
