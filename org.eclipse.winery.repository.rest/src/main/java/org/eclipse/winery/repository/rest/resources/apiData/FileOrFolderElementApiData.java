/*******************************************************************************
 * Copyright (c) 2022 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.rest.resources.apiData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FileOrFolderElement")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileOrFolderElementApiData {

    public boolean isFile;
    public String name;
    public long size;
    public String modified;

    public FileOrFolderElementApiData(boolean isFolder, String name, long size, String modified) {
        this.isFile = isFolder;
        this.name = name;
        this.size = size;
        this.modified = modified;
    }

    public FileOrFolderElementApiData(Path file) {
        this.isFile = !Files.isDirectory(file);
        this.name = file.getFileName().toString();
        if (this.isFile) {
            try {
                this.size = Files.size(file);
            } catch (IOException e) {
                e.printStackTrace();
                this.size = 0;
            }
        } else {
            this.size = 0;
        }
        try {
            this.modified = formatTime(Files.getLastModifiedTime(file));
        } catch (IOException e) {
            e.printStackTrace();
            this.modified = "-";
        }
    }

    public static String formatTime(FileTime fileTime) {
        LocalDateTime localDateTime = fileTime
            .toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        return localDateTime.format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
