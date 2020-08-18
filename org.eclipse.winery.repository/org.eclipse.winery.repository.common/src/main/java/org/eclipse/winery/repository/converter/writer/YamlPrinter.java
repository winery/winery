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
package org.eclipse.winery.repository.converter.writer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TStatusValue;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;

public class YamlPrinter extends AbstractResult<YamlPrinter> {

    private StringBuilder stringBuilder;
    private int indent;

    public YamlPrinter(int indent) {
        this(new StringBuilder(), indent);
    }

    public YamlPrinter(StringBuilder stringBuilder, int indent) {
        this.stringBuilder = stringBuilder;
        this.indent = 0;
        this.indent(indent);
    }

    @Override
    public YamlPrinter add(YamlPrinter printer) {
        this.print(printer);
        return this;
    }

    public boolean isEmpty() {

        return stringBuilder.length() == 0 || stringBuilder.length() == indent;
    }

    /**
     * Increases or decreases the indent
     */
    public YamlPrinter indent(int delta) {
        this.indent += delta;
        if (delta < 0) {
            for (int i = -1; i >= delta; i--) {
                if (stringBuilder.length() + delta < 0 || stringBuilder.charAt(stringBuilder.length() + i) != ' ') {
                    return this;
                }
            }
            stringBuilder.setLength(stringBuilder.length() + delta);
        } else {
            for (int i = 0; i < delta; i++) {
                print(' ');
            }
        }
        return this;
    }

    public YamlPrinter print(String value) {
        this.stringBuilder.append(value);
        return this;
    }

    public YamlPrinter print(char value) {
        this.stringBuilder.append(value);
        return this;
    }

    public YamlPrinter printQName(QName value) {
        this.stringBuilder.append(qNameToString(value));
        this.printNewLine();
        return this;
    }

    public YamlPrinter print(YamlPrinter printer) {
        if (Objects.isNull(printer) || printer.isEmpty()) return this;
        if (endsWithNewLine()) {
            int cut = 0;
            for (int i = stringBuilder.length() - 1; i >= 0; i--) {
                if (stringBuilder.charAt(i) != '\n') {
                    cut--;
                } else {
                    break;
                }
            }
            stringBuilder.setLength(stringBuilder.length() + cut);
        } else if (stringBuilder.length() == indent) {
            stringBuilder.setLength(0);
        } else if (stringBuilder.length() > 0) {
            print('\n');
        }
        if (printer.indent > this.indent && printer.endsWithNewLine()) {
            printer.stringBuilder.setLength(printer.stringBuilder.length() + this.indent - printer.indent);
        }
        print(printer.toString());
        return this;
    }

    public YamlPrinter print(Optional<YamlPrinter> printer) {
        printer.ifPresent(this::print);
        return this;
    }

    public YamlPrinter printYamlValue(String key, Object value) {
        return printYamlValue(key, value, false);
    }

    public YamlPrinter printYamlValue(String key, Object value, boolean printEmptyValues) {
        String stringValue = String.valueOf(value);
        Class<?> clazz = determineClazz(stringValue);
        if (Void.class == clazz && printEmptyValues) {
            print(key).print(": ").print("null").printNewLine();
        } else if (Boolean.class == clazz || Float.class == clazz || Integer.class == clazz || Map.class == clazz) {
            print(key).print(": ").print(stringValue).printNewLine();
        } else if (String.class == clazz) {
            if (isQuoted(stringValue)) {
                print(key).print(": ").print(stringValue).printNewLine();
            } else {
                print(key).print(": ").print("\"").print(stringValue).print("\"").printNewLine();
            }
        }
        return this;
    }

    private Class<?> determineClazz(String value) {
        if (Objects.isNull(value) || value.isEmpty() || value.equalsIgnoreCase("null")) {
            return Void.class;
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
            return Boolean.class;
        }
        if (value.contains(".")) {
            try {
                Float.parseFloat(value);
                return Float.class;
            } catch (Exception e) {
                // do nothing
            }
        }
        // checks if a yaml map is the value which is saved as a string 
        // because the map is not represented in the data model yet
        // e.g. intrinsic functions
        // !!! this might trigger false positives !!!
        String tmp = value.trim();
        if (tmp.startsWith("{") && tmp.endsWith("}")) {
            return Map.class;
        }
        try {
            Integer.parseInt(value);
            return Integer.class;
        } catch (Exception e) {
            // do nothing
        }
        return String.class;
    }

    private boolean isQuoted(String value) {
        return value.startsWith("\"") && value.endsWith("\"");
    }

    public YamlPrinter printKeyValue(String key, String value) {
        return printKeyValue(key, value, false, false);
    }

    public YamlPrinter printKeyValue(String key, String value, boolean printQuotes) {
        return printKeyValue(key, value, printQuotes, false);
    }

    public YamlPrinter printKeyValue(String key, String value, boolean printQuotes, boolean printEmptyValues) {
        if (Objects.isNull(value) || (!printEmptyValues && value.isEmpty())) return this;
        if (value.contains("\n")) {
            return print(key)
                .print(": >")
                .printNewLine()
                .indent(2)
                .print(value.trim().replaceAll("\n", "\n\n" + getIndentString()))
                .indent(-2)
                .printCheckNewLine();
        }
        YamlPrinter printer = print(key).print(": ");
        if (printQuotes) {
            printer.print("\"")
                .print(value)
                .print("\"");
        } else {
            printer.print(value);
        }
        return printer.printNewLine();
    }

    public YamlPrinter printKeyValue(String key, TVersion value) {
        if (Objects.isNull(value)) return this;
        return printKeyValue(key, value.getVersion());
    }

    public YamlPrinter printKeyValue(String key, TStatusValue value) {
        if (Objects.isNull(value)) return this;
        return printKeyValue(key, value.name());
    }

    public YamlPrinter printKeyValue(String key, Boolean value) {
        if (Objects.isNull(value)) return this;
        return print(key)
            .print(':')
            .print(' ')
            .print(value.toString())
            .printNewLine();
    }

    public YamlPrinter printKeyValue(String key, List<?> value) {
        if (Objects.isNull(value) || value.isEmpty()) return this;
        return print(key)
            .print(':')
            .print(' ')
            .print('[')
            .print(' ')
            .print(value.stream()
                .map((object) -> {
                    if (object instanceof QName) {
                        return qNameToString((QName) object);
                    } else {
                        return object.toString();
                    }
                })
                .collect(Collectors.joining(", ")))
            .print(' ')
            .print(']')
            .printNewLine();
    }

    public YamlPrinter printKeyValue(String key, QName value) {
        if (Objects.isNull(value) || value.getLocalPart().isEmpty()) return this;
        return printKeyValue(key, qNameToString(value));
    }

    public YamlPrinter printKey(String key) {
        return print(key)
            .print(":")
            .printNewLine();
    }

    public YamlPrinter printListKey(String key) {
        return print('-')
            .print(' ')
            .printKey(key);
    }

    public YamlPrinter printKeyObject(String key, Object object) {
        if (Objects.isNull(object)) return this;
        if (object instanceof String) {
            printKeyValue(key, (String) object, true);
        } else if (object instanceof Map) {
            if (key.isEmpty()) {
                print("{")
                    .printNewLine()
                    .indent(2);
            } else {
                printKey(key)
                    .indent(2);
            }
            Map<String, Object> map = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                printKeyObject(entry.getKey(), entry.getValue());
            }
            indent(-2);
            if (key.isEmpty()) {
                printCheckNewLine()
                    .print('}');
            }
        } else if (object instanceof List && !((List) object).isEmpty()) {
            printKey(key)
                .indent(2);
            for (Object entry : (List<Object>) object) {
                printListObject(entry);
            }
            indent(-2)
                .printNewLine();
        } else {
            printKeyValue(key, object.toString());
        }
        return this;
    }

    public YamlPrinter printListObject(Object object) {
        if (object instanceof String) {
            String value = (String) object;
            if (value.contains("\n")) {
                return print('-')
                    .print(' ')
                    .print('>')
                    .printNewLine()
                    .indent(2)
                    .print(value.replaceAll("\n", "\n" + getIndentString()))
                    .indent(-2)
                    .printNewLine();
            }
            return print('-')
                .print(' ')
                .print(value)
                .printNewLine();
        } else if (object instanceof Map) {
            print('-')
                .print(' ');
            this.indent += 2;
            Map<String, Object> map = (Map<String, Object>) object;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                printKeyObject(entry.getKey(), entry.getValue());
            }
            indent(-2)
                .printNewLine();
        }
        return this;
    }

    public YamlPrinter printKeyListObjectInline(String key, List<Object> list) {
        if (Objects.isNull(list) || list.isEmpty()) return this;
        print(key)
            .print(':')
            .print(' ')
            .print('[')
            .print(' ')
            .print(list.stream().map(Object::toString).collect(Collectors.joining(",")))
            .print(' ')
            .print(']')
            .printNewLine();
        return this;
    }

    public YamlPrinter printNewLine() {
        if (stringBuilder.length() > 0) {
            this.print('\n');
            for (int i = 0; i < indent; i++) {
                this.print(' ');
            }
        }
        return this;
    }

    public YamlPrinter printCheckNewLine() {
        if (!endsWithNewLine()) {
            this.printNewLine();
        }
        return this;
    }

    private Boolean endsWithNewLine() {
        for (int i = stringBuilder.length() - 1; i >= 0; i--) {
            if (stringBuilder.charAt(i) != ' ') {
                return stringBuilder.charAt(i) == '\n';
            }
        }
        return false;
    }

    private String qNameToString(QName name) {
        // when processing property types - only use the local part
        if (!name.getPrefix().isEmpty() && (
            name.getPrefix().equals("yaml") || name.getPrefix().equals("tosca"))
            || name.getNamespaceURI().isEmpty()) {
            return name.getLocalPart();
        }

        // TODO decide on namespace handling w.r.t. Simple Profile spec
        // current solution: use dotted notation for namespaces as in RADON particles
        return name.getNamespaceURI() + "." + name.getLocalPart();
    }

    private String getIndentString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    public String toString() {
        return stringBuilder.toString();
    }

    public YamlPrinter clear() {
        stringBuilder.setLength(0);
        return this;
    }
}
