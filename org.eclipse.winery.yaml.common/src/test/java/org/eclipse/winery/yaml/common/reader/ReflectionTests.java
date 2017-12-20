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
package org.eclipse.winery.yaml.common.reader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TArtifactDefinition;
import org.eclipse.winery.model.tosca.yaml.TArtifactType;
import org.eclipse.winery.model.tosca.yaml.TAttributeAssignment;
import org.eclipse.winery.model.tosca.yaml.TAttributeDefinition;
import org.eclipse.winery.model.tosca.yaml.TCapabilityAssignment;
import org.eclipse.winery.model.tosca.yaml.TCapabilityDefinition;
import org.eclipse.winery.model.tosca.yaml.TConstraintClause;
import org.eclipse.winery.model.tosca.yaml.TDataType;
import org.eclipse.winery.model.tosca.yaml.TEntityType;
import org.eclipse.winery.model.tosca.yaml.TEntrySchema;
import org.eclipse.winery.model.tosca.yaml.TGroupDefinition;
import org.eclipse.winery.model.tosca.yaml.TGroupType;
import org.eclipse.winery.model.tosca.yaml.TImplementation;
import org.eclipse.winery.model.tosca.yaml.TImportDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceAssignment;
import org.eclipse.winery.model.tosca.yaml.TInterfaceDefinition;
import org.eclipse.winery.model.tosca.yaml.TInterfaceType;
import org.eclipse.winery.model.tosca.yaml.TNodeFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TNodeOrGroupType;
import org.eclipse.winery.model.tosca.yaml.TNodeTemplate;
import org.eclipse.winery.model.tosca.yaml.TNodeType;
import org.eclipse.winery.model.tosca.yaml.TOperationDefinition;
import org.eclipse.winery.model.tosca.yaml.TParameterDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPolicyType;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignment;
import org.eclipse.winery.model.tosca.yaml.TPropertyAssignmentOrDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyDefinition;
import org.eclipse.winery.model.tosca.yaml.TPropertyFilterDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipAssignment;
import org.eclipse.winery.model.tosca.yaml.TRelationshipDefinition;
import org.eclipse.winery.model.tosca.yaml.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.yaml.TRelationshipType;
import org.eclipse.winery.model.tosca.yaml.TRepositoryDefinition;
import org.eclipse.winery.model.tosca.yaml.TRequirementAssignment;
import org.eclipse.winery.model.tosca.yaml.TRequirementDefinition;
import org.eclipse.winery.model.tosca.yaml.TServiceTemplate;
import org.eclipse.winery.model.tosca.yaml.TStatusValue;
import org.eclipse.winery.model.tosca.yaml.TSubstitutionMappings;
import org.eclipse.winery.model.tosca.yaml.TTopologyTemplateDefinition;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.support.Metadata;
import org.eclipse.winery.yaml.common.AbstractTest;
import org.eclipse.winery.yaml.common.ReflectionUtil;
import org.eclipse.winery.yaml.common.exception.Invalid;
import org.eclipse.winery.yaml.common.exception.InvalidField;
import org.eclipse.winery.yaml.common.exception.InvalidParentType;
import org.eclipse.winery.yaml.common.exception.InvalidSyntax;
import org.eclipse.winery.yaml.common.exception.InvalidToscaSyntax;
import org.eclipse.winery.yaml.common.exception.InvalidToscaVersion;
import org.eclipse.winery.yaml.common.exception.InvalidType;
import org.eclipse.winery.yaml.common.exception.InvalidDefinition;
import org.eclipse.winery.yaml.common.exception.InvalidTypeExtend;
import org.eclipse.winery.yaml.common.exception.InvalidYamlSyntax;
import org.eclipse.winery.yaml.common.exception.MultiException;
import org.eclipse.winery.yaml.common.exception.Undefined;
import org.eclipse.winery.yaml.common.exception.UndefinedDefinition;
import org.eclipse.winery.yaml.common.exception.UndefinedField;
import org.eclipse.winery.yaml.common.exception.UndefinedFile;
import org.eclipse.winery.yaml.common.exception.UndefinedImport;
import org.eclipse.winery.yaml.common.exception.UndefinedPrefix;
import org.eclipse.winery.yaml.common.exception.UndefinedRequiredKeyname;
import org.eclipse.winery.yaml.common.exception.UndefinedToscaVersion;
import org.eclipse.winery.yaml.common.exception.UndefinedType;
import org.eclipse.winery.yaml.common.exception.YAMLParserException;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.MultipleFailuresError;
import org.opentest4j.ValueWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionTests extends AbstractTest {
    private static Logger logger = LoggerFactory.getLogger(ReflectionTests.class);
    private static Map<String, Class<? extends Exception>> exceptionClasses;
    private static Map<String, Class<?>> toscaYamlClasses;
    private ReflectionUtil reflectionUtil;

    public ReflectionTests() {
        this.reflectionUtil = new ReflectionUtil();
    }

    @BeforeAll
    private static void init() {
        AbstractTest.path = Paths.get("src/test/resources/builder/reflectionTests");

        exceptionClasses = Stream.of(
            MultiException.class,
            YAMLParserException.class,
            Invalid.class,
            InvalidSyntax.class,
            InvalidYamlSyntax.class,
            InvalidToscaSyntax.class,
            InvalidDefinition.class,
            InvalidType.class,
            InvalidParentType.class,
            InvalidTypeExtend.class,
            InvalidField.class,
            InvalidToscaVersion.class,
            Undefined.class,
            UndefinedType.class,
            UndefinedDefinition.class,
            UndefinedPrefix.class,
            UndefinedField.class,
            UndefinedToscaVersion.class,
            UndefinedRequiredKeyname.class,
            UndefinedFile.class,
            UndefinedImport.class
        ).map(exception -> Tuples.pair(exception.getSimpleName(), exception))
            .collect(Collectors.toMap(Pair::getOne, Pair::getTwo));

        toscaYamlClasses = Stream.of(
            String.class,
            Integer.class,
            Float.class,
            Double.class,
            Boolean.class,
            TArtifactDefinition.class,
            TArtifactType.class,
            TAttributeAssignment.class,
            TAttributeDefinition.class,
            TCapabilityAssignment.class,
            TCapabilityDefinition.class,
            TConstraintClause.class,
            TDataType.class,
            TEntityType.class,
            TEntrySchema.class,
            TGroupDefinition.class,
            TGroupType.class,
            TImplementation.class,
            TImportDefinition.class,
            TInterfaceAssignment.class,
            TInterfaceDefinition.class,
            TInterfaceType.class,
            TNodeFilterDefinition.class,
            TNodeOrGroupType.class,
            TNodeTemplate.class,
            TNodeType.class,
            TOperationDefinition.class,
            TParameterDefinition.class,
            TPolicyDefinition.class,
            TPolicyType.class,
            TPropertyAssignment.class,
            TPropertyAssignmentOrDefinition.class,
            TPropertyDefinition.class,
            TPropertyFilterDefinition.class,
            TRelationshipAssignment.class,
            TRelationshipDefinition.class,
            TRelationshipTemplate.class,
            TRelationshipType.class,
            TRepositoryDefinition.class,
            TRequirementAssignment.class,
            TRequirementDefinition.class,
            TServiceTemplate.class,
            TStatusValue.class,
            TSubstitutionMappings.class,
            TTopologyTemplateDefinition.class,
            TVersion.class
        ).map(exception -> Tuples.pair(exception.getSimpleName(), exception))
            .collect(Collectors.toMap(Pair::getOne, Pair::getTwo));
    }

    @ParameterizedTest
    @MethodSource("getYamlFiles")
    public void testServiceTemplates(Path fileName) throws Exception {
        Metadata metadata = getMetadata(fileName);
        String exception = metadata.get("exception");
        String assertValue = metadata.get("assert");
        String keyName = metadata.get("keyname");
        String assertTypeof = metadata.get("assert-typeof");

        testServiceTemplates(fileName, exception, keyName, assertValue, assertTypeof);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "capabilityTypes/invalid-capability_type-undefined_derived_from"
    })
    public void testSingleServiceTemplates(String fileName) throws Exception {
        testServiceTemplates(getYamlFile(fileName));
    }

    @ParameterizedTest
    @CsvSource({
        "valid-service_template, 'relationship_types.rlt1.interfaces.intf1.inputs.intf1.prt1.type = string'"
    })
    public void testSingleServiceTemplatesAssertValue(String fileName,
                                                      String assertValue) throws Exception {
        testServiceTemplates(getYamlFile(fileName), null, null, assertValue, null);
    }

    @ParameterizedTest
    @CsvSource({
        "valid-service_template, 'repositories.rp1 = TRepositoryDefinition'"
    })
    public void testSingleServiceTemplatesAssertTypeof(String fileName,
                                                       String assertTypeof) throws Exception {
        testServiceTemplates(getYamlFile(fileName), null, null, null, assertTypeof);
    }

    @ParameterizedTest
    @CsvSource({
        "invalid-yaml_syntax-missing_line_break, InvalidSyntax"
    })
    public void testSingleServiceTemplatesException(String fileName,
                                                    String exception) throws Exception {
        testServiceTemplates(getYamlFile(fileName), exception, null, null, null);
    }

    private void testServiceTemplates(Path fileName, String exception, String keyName, String assertValue, String assertTypeof) throws Exception {
        Assertions.assertNotNull(fileName, "FileName is null");
        logHead(fileName);
        // Handle expected exceptions
        if (Objects.nonNull(exception) && !exception.equalsIgnoreCase("none")) {
            Assertions.assertThrows(MultiException.class, () -> getYamlServiceTemplate(fileName));
            try {
                getYamlServiceTemplate(fileName);
            } catch (MultiException multi) {
                Assertions.assertEquals(exceptionClasses.get(exception), multi.getException().getClass());
                logger.info("Assertion(error) success: {}", exceptionClasses.get(exception));
            }
            return;
        }

        // Resolve keyname
        TServiceTemplate serviceTemplate = getYamlServiceTemplate(fileName);
        Object assertionObject;
        if (Objects.nonNull(keyName)) {
            assertionObject = reflectionUtil.resolve(keyName, serviceTemplate);
            Assertions.assertNotNull(assertionObject, "Keyname '" + keyName + "' is not resolvable.");
        } else {
            assertionObject = serviceTemplate;
            Assertions.assertNotNull(assertionObject, "Parsing ServiceTemplate returned null");
        }

        List<Throwable> errors = new ArrayList<>();

        // Value asserts
        if (Objects.nonNull(assertValue)) {
            errors.addAll(Arrays.stream(assertValue.split("\n"))
                .filter(ass -> {
                    int size = ass.split("=").length;
                    if (size == 2) {
                        return true;
                    } else {
                        errors.add(
                            new AssertionError("Unknown assertion format expected: \"keyname = value\" but actual: \""
                                .concat(ass)
                                .concat("\"")
                            )
                        );
                        return false;
                    }
                })
                .map(ass -> Tuples.pair(ass.split("=")[0].trim(), ass.split("=")[1].trim()))
                .map(pair -> {
                    try {
                        handleAsserts(pair, assertionObject);
                        return null;
                    } catch (AssertionFailedError e) {
                        logger.error("Assertion(value) error: {}", pair);
                        logger.error("\texpected '{}'",
                            Optional.ofNullable(e.getExpected()).map(ValueWrapper::getStringRepresentation)
                                .orElse(null));
                        logger.error("\tactual '{}'",
                            Optional.ofNullable(e.getActual()).map(ValueWrapper::getStringRepresentation)
                                .orElse(null));
                        return e;
                    } catch (AssertionError e) {
                        logger.error("Assertion(value) error: {}", pair);
                        return e;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }

        // Typeof asserts
        if (Objects.nonNull(assertTypeof)) {
            errors.addAll(Arrays.stream(assertTypeof.split("\n"))
                .map(ass -> Tuples.pair(ass.split("=")[0].trim(), ass.split("=")[1].trim()))
                .map(pair -> {
                    try {
                        handleTypeAsserts(pair, assertionObject);
                        return null;
                    } catch (AssertionError e) {
                        logger.error("Assertion(typeof) error: {}", pair);
                        return e;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        }

        // Throw all collected asserts errors
        if (!errors.isEmpty()) {
            throw new MultipleFailuresError("Assertions failed", errors);
        }
    }

    private void handleAsserts(Pair<String, String> pair, Object object) {
        Object result = reflectionUtil.resolve(pair.getOne(), object);
        Assertions.assertNotNull(result, "Could not resolve '" + pair.getOne() + "' for " + object);

        assertEquals(pair.getTwo(), result);
        logger.info("Assertion(value) success: {}", pair);
    }

    private void handleTypeAsserts(Pair<String, String> pair, Object object) {
        Object result = reflectionUtil.resolve(pair.getOne(), object);
        Assertions.assertNotNull(result, "Could not resolve '" + pair.getOne() + "' for " + object);

        Class<?> expected = toscaYamlClasses.get(pair.getTwo());
        Assertions.assertNotNull(expected, "Could not resolve class: '" + pair.getTwo() + "'");
        Boolean equals = assertEquals(expected, result.getClass());
        if (!equals) {
            logger.error("Assertion(typeof) failed: {} is not instanceof {}", result, expected);
        }
        Assertions.assertTrue(equals);
        logger.info("Assertion(typeof) success: {}", pair);
    }

    private void assertEquals(String expected, Object result) {
        if (result instanceof String) {
            Assertions.assertEquals(expected.trim(), ((String) result).trim());
        } else if (result instanceof Integer) {
            Assertions.assertEquals(Integer.valueOf(expected), result);
        } else if (result instanceof Float) {
            Assertions.assertEquals(Float.valueOf(expected), result);
        } else if (result instanceof Double) {
            Assertions.assertEquals(Double.valueOf(expected), result);
        } else if (result instanceof Boolean) {
            Assertions.assertEquals(Boolean.valueOf(expected), result);
        } else if (result instanceof QName) {
            Assertions.assertEquals(expected, ((QName) result).getLocalPart());
        } else if (result instanceof TVersion) {
            Assertions.assertEquals(expected, ((TVersion) result).getVersion());
        } else if (result instanceof TStatusValue) {
            Assertions.assertEquals(expected, ((TStatusValue) result).name());
        } else if (result instanceof TPropertyAssignment) {
            Assertions.assertEquals(expected, ((TPropertyAssignment) result).getValue());
        } else if (result instanceof TAttributeAssignment) {
            Assertions.assertEquals(expected, ((TAttributeAssignment) result).getValue());
        } else if (result instanceof List) {
            List<String> list = getList(expected);
            Assertions.assertEquals(list.size(), ((List) result).size(),
                "Size of expected list " + list + " does not equal size of actual list " + result);
            for (int i = 0; i < list.size(); i++) {
                assertEquals(list.get(i), ((List) result).get(i));
            }
        } else {
            Assertions.fail("Unknown instanceof for " + result);
        }
    }

    private boolean assertEquals(Class<?> expected, Class<?> actual) {
        return actual.equals(expected) || (!actual.equals(Object.class) && assertEquals(expected, actual.getSuperclass()));
    }

    private List<String> getList(String list) {
        Assertions.assertTrue(list.trim().startsWith("[") && list.trim().endsWith("]"),
            "Expected a list in String representation but received '" + list + "'");
        return Arrays.stream(list.substring(1, list.length() - 1).split(","))
            .map(String::trim)
            .collect(Collectors.toList());
    }

    private void logHead(Path fileName) {
        logger.info("------------------------------------------------------------------------");
        logger.info("- Start test of service template \"{}\"", fileName);
        logger.info("------------------------------------------------------------------------");
    }
}
