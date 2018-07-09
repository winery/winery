/*******************************************************************************
 * Copyright (c) 2013-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.csar.toscametafile;

import org.eclipse.virgo.util.parser.manifest.ManifestContents;

import java.io.Serializable;
import java.util.*;

/**
 * Provides structured access to the content of a TOSCA meta file.
 */
public class TOSCAMetaFile implements Serializable {

    private static final long serialVersionUID = 5636441655503533546L;

    private Map<String, String> block0 = new HashMap<>();
    private List<Map<String, String>> fileBlocks = new ArrayList<>();

    /**
     * Creates an empty TOSCA Meta file object
     */
    public TOSCAMetaFile() {
    }

    /**
     * Creates a new instance based on the data in the given manifest content
     *
     * @param manifestContent to create from
     */
    public TOSCAMetaFile(ManifestContents manifestContent) {
        this.block0 = manifestContent.getMainAttributes();
        for (String name : manifestContent.getSectionNames()) {
            Map<String, String> fileBlock = new HashMap<String, String>();
            fileBlock.put(TOSCAMetaFileAttributes.NAME, name);
            fileBlock.putAll(manifestContent.getAttributesForSection(name));
            this.fileBlocks.add(fileBlock);
        }
    }

    /**
     * @return Value of attribute <code>CSAR-Version</code> in block 0.
     */
    public String getCSARVersion() {
        return this.block0.get(TOSCAMetaFileAttributes.CSAR_VERSION);
    }

    /**
     * @return Value of attribute <code>TOSCA-Meta-Version</code> in block 0.
     */
    public String getTOSCAMetaVersion() {
        return this.block0.get(TOSCAMetaFileAttributes.TOSCA_META_VERSION);
    }

    /**
     * @return Value of attribute <code>Created-By</code> in block 0.
     */
    public String getCreatedBy() {
        return this.block0.get(TOSCAMetaFileAttributes.CREATED_BY);
    }

    /**
     * @return Value of attribute <code>Entry-Definitions</code> in block 0
     * (contains relative path to the root TOSCA file in the CSAR). If
     * attribute is not specified <code>null</code>.
     */
    public String getEntryDefinitions() {
        return this.block0.get(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS);
    }

    /**
     * @return Value of attribute <code>Description</code> in block 0 (contains
     * description of CSAR). If attribute is not specified
     * <code>null</code>.
     */
    public String getDescription() {
        return this.block0.get(TOSCAMetaFileAttributes.DESCRIPTION);
    }

    /**
     * @return Value of attribute <code>Topology</code> in block 0 (contains
     * relative path to topology picture in the CSAR). If attribute is
     * not specified <code>null</code>.
     */
    public String getTopology() {
        return this.block0.get(TOSCAMetaFileAttributes.TOPOLOGY);
    }

    /**
     * @return Block 0 (contains meta data about the CSAR itself).
     */
    public Map<String, String> getBlock0() {
        return this.block0;
    }

    /**
     * @return File blocks (block 1 to last block; contains meta data of files
     * in the CSAR). Every block is a element
     * <code>Map&lt;String, String&gt;</code> in the returned
     * <code>List</code>.
     */
    public List<Map<String, String>> getFileBlocks() {
        return this.fileBlocks;
    }

    /**
     * Returns the mime type for the given name
     *
     * @param name a reference to a file
     * @return the mime type associated with the given name, null if no mime
     * type was found
     */
    public String getMimeType(String name) {
        Objects.requireNonNull(name);
        for (Map<String, String> map : this.getFileBlocks()) {
            String storedName = map.get("Name");
            if (name.equals(storedName)) {
                // first hit, check whether content-type is stored
                String contentType = map.get("Content-Type");
                if (contentType != null) {
                    // hit - return the found content type
                    return contentType;
                }
            }
        }
        // nothing found
        return null;
    }
}
