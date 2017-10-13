/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package org.eclipse.winery.yaml.common.reader.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.yaml.common.Namespaces;
import org.eclipse.winery.yaml.common.exception.MissingFile;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.UnrecognizedFieldException;
import org.eclipse.winery.yaml.common.exception.YAMLParserException;
import org.eclipse.winery.yaml.common.validator.ObjectValidator;
import org.eclipse.winery.yaml.common.validator.Validator;
import org.eclipse.winery.yaml.common.validator.support.ExceptionInterpreter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.ConstructorException;
import org.yaml.snakeyaml.scanner.ScannerException;

public class Reader {
    public static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

    private Yaml yaml;

    public Reader() {
        this.yaml = new Yaml();
    }

    public TServiceTemplate parse(String path, String file) throws MultiException {
        return this.readServiceTemplate(path, file, Namespaces.DEFAULT_NS);
    }

    public TServiceTemplate parse(String path, String file, String namespace) throws MultiException {
        return this.readServiceTemplate(path, file, namespace);
    }

    public TServiceTemplate parse(TImportDefinition definition, String path, String namespace) throws MultiException {
        return this.readImportDefinition(definition, path, namespace);
    }

    public TServiceTemplate parseSkipTest(String path, String file, String namespace) throws YAMLParserException {
        return this.readServiceTemplateSkipTest(path + File.separator + file, namespace);
    }

    public TServiceTemplate parseSkipTest(String uri, String namespace) throws YAMLParserException {
        return this.readServiceTemplateSkipTest(uri, namespace);
    }

    public String getNamespace(String path, String file) throws YAMLParserException {
        TServiceTemplate serviceTemplate = parseSkipTest(path, file, Namespaces.DEFAULT_NS);
        return serviceTemplate.getMetadata().getOrDefault("targetNamespace", Namespaces.DEFAULT_NS);
    }

    private TServiceTemplate readServiceTemplateSkipTest(String uri, String namespace) throws YAMLParserException {
        Object object = readObject(uri);
        return buildServiceTemplate(object, namespace);
    }

    private TServiceTemplate buildServiceTemplate(Object object, String namespace) throws UnrecognizedFieldException {
        Builder builder = new Builder(namespace);
        return builder.buildServiceTemplate(object);
    }


    /**
     * Uses snakeyaml to convert a file into an Object
     *
     * @param fileName name
     * @return Object (Lists, Maps, Strings, Integers, Dates)
     * @throws MissingFile if the file could not be found.
     */
    private Object readObject(String fileName) throws MissingFile {
        InputStream inputStream;
        try {
            File file = new File(fileName);
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            MissingFile ex = new MissingFile("The file \"" + fileName + "\" could not be found!");
            ex.setFileContext(fileName);
            throw ex;
        }
        return this.yaml.load(inputStream);
    }

    /**
     * Reads a file and converts it to a ServiceTemplate
     *
     * @return ServiceTemplate
     * @throws MultiException the ServiceTemplate or the file is invalid.
     */
    private TServiceTemplate readServiceTemplate(String path, String file, String namespace) throws MultiException {
        String uri = file;
        String filePath = path;
        if (path.isEmpty()) {
            filePath = new File(file).getParentFile().getPath();
        } else {
            uri = path + File.separator + file;
        }
        LOGGER.debug("Read Service Template: {}", uri);

        // pre parse checking
        try {
            ObjectValidator objectValidator = new ObjectValidator();
            objectValidator.validateObject(readObject(uri));
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
            result = buildServiceTemplate(readObject(uri), namespace);
        } catch (YAMLParserException e) {
            throw new MultiException().add(e).add(uri);
        }

        // post parse checking
        Validator validator = new Validator(filePath);
        try {
            validator.validate(result, namespace);
        } catch (MultiException e) {
            e.add(uri);
            throw e;
        }

        return result;
    }

    public TServiceTemplate readImportDefinition(TImportDefinition definition, String path, String namespace) throws MultiException {
        if (definition == null) {
            return null;
        }

        String importNamespace = definition.getNamespaceUri() == null ? namespace : definition.getNamespaceUri();
        if (definition.getRepository() == null) {
            return readServiceTemplate(path, definition.getFile(), importNamespace);
        } else {
            // TODO Support Repositories
            return null;
        }
    }
}
