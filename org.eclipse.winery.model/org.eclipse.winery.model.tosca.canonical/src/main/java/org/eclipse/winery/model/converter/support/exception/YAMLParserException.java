/********************************************************************************
 * Copyright (c) 2017-2020 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.converter.support.exception;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.helpers.MessageFormatter;

public abstract class YAMLParserException extends Exception {
    private List<Object> argArray;
    private String inlineContext;
    private Path fileContext;

    public YAMLParserException(@NonNull String messagePattern, Object... args) {
        super(messagePattern);
        argArray = new ArrayList<>();
        if (Objects.nonNull(args)) argArray.addAll(Arrays.asList(args));
    }

    public YAMLParserException setContext(List<String> context) {
        inlineContext = String.join(":", context);
        return this;
    }

    public String getMessage() {
        return MessageFormatter.arrayFormat(
            super.getMessage(),
            argArray.toArray()
        ).getMessage().trim()
            .concat(getFileContext())
            .concat(getInlineContext());
    }

    public String getFileContext() {
        if (Objects.isNull(fileContext)) return "";
        return "\nContext::FILE = ".concat(String.valueOf(fileContext));
    }

    public YAMLParserException setFileContext(Path path) {
        fileContext = path;
        return this;
    }

    public String getInlineContext() {
        if (Objects.isNull(inlineContext)) return "";
        return "\nContext::INLINE = ".concat(String.valueOf(inlineContext));
    }
}
