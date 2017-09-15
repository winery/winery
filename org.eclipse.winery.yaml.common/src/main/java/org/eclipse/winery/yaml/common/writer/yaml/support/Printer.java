/*******************************************************************************
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Christoph Kleine - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.yaml.common.writer.yaml.support;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.yaml.TStatusValue;
import org.eclipse.winery.model.tosca.yaml.TVersion;
import org.eclipse.winery.model.tosca.yaml.visitor.AbstractResult;

public class Printer extends AbstractResult<Printer> {

    private StringBuilder stringBuilder;
    private int indent;

    public Printer(int indent) {
        this(new StringBuilder(), indent);
    }

    public Printer(StringBuilder stringBuilder, int indent) {
        this.stringBuilder = stringBuilder;
        this.indent = 0;
        this.indent(indent);
    }

    @Override
    public Printer add(Printer printer) {
        this.print(printer);
        return this;
    }

    public boolean isEmpty() {

        return stringBuilder.length() == 0 || stringBuilder.length() == indent;
    }

    /**
     * Increases or decreases the indent
     */
    public Printer indent(int delta) {
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

    public Printer print(String value) {
        this.stringBuilder.append(value);
        return this;
    }

    public Printer print(char value) {
        this.stringBuilder.append(value);
        return this;
    }

    public Printer print(Printer printer) {
        if (!Objects.nonNull(printer) || printer.isEmpty()) return this;
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

    public Printer print(Optional<Printer> printer) {
        printer.ifPresent(this::print);
        return this;
    }

    public Printer printKeyValue(String key, String value) {
        if (!Objects.nonNull(value) || value.isEmpty()) return this;
        if (value.contains("\n")) {
            return print(key)
                .print(": >")
                .printNewLine()
                .indent(2)
                .print(value.trim().replaceAll("\n", "\n\n" + getIndentString()))
                .indent(-2)
                .printCheckNewLine();
        }
        return print(key)
            .print(": ")
            .print(value)
            .printNewLine();
    }

    public Printer printKeyValue(String key, TVersion value) {
        if (!Objects.nonNull(value)) return this;
        return printKeyValue(key, value.getVersion());
    }

    public Printer printKeyValue(String key, TStatusValue value) {
        if (!Objects.nonNull(value)) return this;
        return printKeyValue(key, value.name());
    }

    public Printer printKeyValue(String key, Boolean value) {
        if (!Objects.nonNull(value)) return this;
        return print(key)
            .print(':')
            .print(' ')
            .print(value.toString())
            .printNewLine();
    }

    public Printer printKeyValue(String key, String value, boolean print) {
        if (print) {
            printKeyValue(key, value);
        }
        return this;
    }

    public Printer printKeyValue(String key, List<? extends Object> value) {
        if (!Objects.nonNull(value) || value.isEmpty()) return this;
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

    public Printer printKeyValue(String key, QName value) {
        if (!Objects.nonNull(value) || value.getLocalPart().isEmpty()) return this;
        return printKeyValue(key, qNameToString(value));
    }

    public Printer printKey(String key) {
        return print(key)
            .print(":")
            .printNewLine();
    }

    public Printer printListKey(String key) {
        return print('-')
            .print(' ')
            .printKey(key);
    }

    public Printer printKeyObject(String key, Object object) {
        if (!Objects.nonNull(object)) return this;
        if (object instanceof String) {
            printKeyValue(key, (String) object);
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

    public Printer printListObject(Object object) {
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

    public Printer printKeyListObjectInline(String key, List<Object> list) {
        if (!Objects.nonNull(list) || list.isEmpty()) return this;
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

    public Printer printNewLine() {
        if (stringBuilder.length() > 0) {
            this.print('\n');
            for (int i = 0; i < indent; i++) {
                this.print(' ');
            }
        }
        return this;
    }

    public Printer printCheckNewLine() {
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
        if (!name.getPrefix().isEmpty() && (
            name.getPrefix().equals("yaml")
                || name.getPrefix().equals("tosca")
        )) return name.getLocalPart();
        return name.getPrefix().isEmpty() ?
            ("\"{" + name.getNamespaceURI() + "}" + name.getLocalPart() + "\"") :
            (name.getPrefix() + ":" + name.getLocalPart());
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

    public Printer clear() {
        stringBuilder.setLength(0);
        return this;
    }
}
