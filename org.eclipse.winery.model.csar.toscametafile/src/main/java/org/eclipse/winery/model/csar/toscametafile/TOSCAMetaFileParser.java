/*******************************************************************************
 * Copyright (c) 2013-2020 Contributors to the Eclipse Foundation
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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.eclipse.virgo.util.parser.manifest.ManifestContents;
import org.eclipse.virgo.util.parser.manifest.ManifestParser;
import org.eclipse.virgo.util.parser.manifest.ManifestProblem;
import org.eclipse.virgo.util.parser.manifest.RecoveringManifestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and validates a TOSCA meta file.
 */
public class TOSCAMetaFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TOSCAMetaFileParser.class);

    /**
     * Parses and validates the <code>toscaMetaFile</code>.
     *
     * @param toscaMetaFile path to the metadata file to process
     * @return <code>TOSCAMetaFile</code> that gives access to the content of
     * the TOSCA meta file. If the given file doesn't exist or is invalid <code>null</code>.
     */
    public TOSCAMetaFile parse(Path toscaMetaFile) {
        FileReader reader = null;
        ManifestParser parser;
        ManifestContents manifestContent;
        TOSCAMetaFile toscaMetaFileContent = null;

        try {
            parser = new RecoveringManifestParser();
            reader = new FileReader(toscaMetaFile.toFile());
            TOSCAMetaFileParser.LOGGER.debug("Parsing TOSCA meta file \"{}\"...", toscaMetaFile.getFileName().toString());
            manifestContent = parser.parse(reader);
            reader.close();

            // counts the errors during parsing
            int numErrors = 0;

            for (ManifestProblem problem : parser.getProblems()) {
                this.logManifestProblem(problem);
                numErrors++;
            }

            toscaMetaFileContent = this.parse(manifestContent, numErrors);
        } catch (FileNotFoundException exc) {
            TOSCAMetaFileParser.LOGGER.error("\"{}\" doesn't exist or is not a file.", toscaMetaFile, exc);
        } catch (IOException exc) {
            TOSCAMetaFileParser.LOGGER.error("An IO Exception occured.", exc);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException exc) {
                    TOSCAMetaFileParser.LOGGER.warn("An IOException occured.", exc);
                }
            }
        }

        return toscaMetaFileContent;
    }

    /**
     * Parses and validates the <code>toscaMetaFile</code>.
     *
     * @param manifestContent  generically parsed manifest file
     * @param parseErrorsCount number of errors found during the generic parsing of the meta file.
     * @return <code>TOSCAMetaFile</code> that gives access to the content of
     * the TOSCA meta file. If the given file doesn't exist or is invalid <code>null</code>.
     */
    public TOSCAMetaFile parse(ManifestContents manifestContent, int parseErrorsCount) {
        // counts the errors during parsing
        int numErrors = parseErrorsCount;
        TOSCAMetaFile toscaMetaFileContent = null;

        numErrors += this.validateBlock0(manifestContent);
        numErrors += this.validateFileBlocks(manifestContent);

        if (numErrors == 0) {
            TOSCAMetaFileParser.LOGGER.debug("Parsing TOSCA meta file completed without errors. TOSCA meta file is valid.");
            toscaMetaFileContent = new TOSCAMetaFile(manifestContent);
        } else {
            TOSCAMetaFileParser.LOGGER.error("Parsing TOSCA meta file failed - {} error(s) occurred. TOSCA meta file is invalid.", numErrors);
        }

        return toscaMetaFileContent;
    }

    /**
     * Validates block 0 of the TOSCA meta file.
     * <p>
     * Required attributes in block 0:
     * <ul>
     * <li><code>TOSCA-Meta-Version</code> (value must be <code>1.0</code>)</li>
     * <li><code>CSAR-Version</code> (value must be <code>1.0</code>)</li>
     * <li><code>Created-By</code></li>
     * </ul>
     * Optional attributes in block 0:
     * <ul>
     * <li><code>Entry-Definitions</code></li>
     * <li><code>Description</code></li>
     * <li><code>Topology</code></li>
     * </ul>
     * <p>
     * Further, arbitrary attributes are also allowed.
     *
     * @param mf to validate
     * @return Number of errors occurred during validation.
     */
    protected int validateBlock0(ManifestContents mf) {
        int numErrors = 0;
        Map<String, String> mainAttr = mf.getMainAttributes();

        numErrors += validateMetaVersion(mainAttr.get(TOSCAMetaFileAttributes.TOSCA_META_VERSION));
        numErrors += validateCsarVersion(mainAttr.get(TOSCAMetaFileAttributes.CSAR_VERSION));
        numErrors += validateCreatedBy(mainAttr.get(TOSCAMetaFileAttributes.CREATED_BY));
        numErrors += validateEntryDefinitions(mainAttr.get(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS));
        numErrors += validateDescription(mainAttr.get(TOSCAMetaFileAttributes.DESCRIPTION));
        numErrors += validateTopology(mainAttr.get(TOSCAMetaFileAttributes.TOPOLOGY));

        return numErrors;
    }

    protected int validateMetaVersion(String metaFileVersion) {
        int errors = 0;
        if (metaFileVersion == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.TOSCA_META_VERSION, 0);
            errors++;
        } else if (!metaFileVersion.trim().equals(TOSCAMetaFileAttributes.TOSCA_META_VERSION_VALUE)) {
            this.logAttrWrongVal(TOSCAMetaFileAttributes.TOSCA_META_VERSION, 0, TOSCAMetaFileAttributes.TOSCA_META_VERSION_VALUE);
            errors++;
        }
        return errors;
    }

    protected int validateCsarVersion(String csarVersion) {
        int errors = 0;

        if (csarVersion == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.CSAR_VERSION, 0);
            errors++;
        } else if (!csarVersion.trim().equals(TOSCAMetaFileAttributes.CSAR_VERSION_VALUE)) {
            this.logAttrWrongVal(TOSCAMetaFileAttributes.CSAR_VERSION, 0, TOSCAMetaFileAttributes.CSAR_VERSION_VALUE);
            errors++;
        }
        return errors;
    }

    protected int validateCreatedBy(String createdBy) {
        int errors = 0;
        if (createdBy == null) {
            this.logAttrMissing(TOSCAMetaFileAttributes.CREATED_BY, 0);
            errors++;
        } else if (createdBy.trim().isEmpty()) {
            this.logAttrValEmpty(TOSCAMetaFileAttributes.CREATED_BY, 0);
            errors++;
        }

        return errors;
    }

    protected int validateEntryDefinitions(String entryDefinitions) {
        int errors = 0;
        if ((entryDefinitions != null) && entryDefinitions.trim().isEmpty()) {
            this.logAttrValEmpty(TOSCAMetaFileAttributes.ENTRY_DEFINITIONS, 0);
            errors++;
        }

        return errors;
    }

    protected int validateDescription(String description) {
        int errors = 0;
        if ((description != null) && description.trim().isEmpty()) {
            this.logAttrValEmpty(TOSCAMetaFileAttributes.DESCRIPTION, 0);
            errors++;
        }

        return errors;
    }

    protected int validateTopology(String topology) {
        int errors = 0;
        if ((topology != null) && topology.trim().isEmpty()) {
            this.logAttrValEmpty(TOSCAMetaFileAttributes.TOPOLOGY, 0);
            errors++;
        }
        return errors;
    }

    /**
     * Validates the file blocks (block 1 to last block) of the TOSCA meta file.
     * <p>
     * Each file block has the following required attributes:
     * <ul>
     * <li><code>Name</code></li>
     * <li><code>Content-Type</code> (will be checked for correct syntax)</li>
     * </ul>
     * <p>
     * Further, arbitrary attributes are also allowed in a file block.
     *
     * @param mf to validate.
     * @return Number of errors occurred during validation.
     */
    private int validateFileBlocks(ManifestContents mf) {
        int blockNr = 0;
        int numErrors = 0;

        String contentType;

        List<String> names = mf.getSectionNames();

        for (String name : names) {

            blockNr++;

            if ((name != null) && name.trim().isEmpty()) {
                this.logAttrValEmpty(name, blockNr);
                numErrors++;
            }

            Map<String, String> attr = mf.getAttributesForSection(name);
            contentType = attr.get(TOSCAMetaFileAttributes.CONTENT_TYPE);

            if (contentType == null) {
                this.logAttrMissing(TOSCAMetaFileAttributes.CONTENT_TYPE, blockNr);
                numErrors++;
            } else if (!contentType.trim().matches("^[-\\w\\+\\.]+/[-\\w\\+\\.]+$")) {
                this.logAttrWrongVal(TOSCAMetaFileAttributes.CONTENT_TYPE, blockNr);
                numErrors++;
            }
        }

        return numErrors;
    }

    /**
     * Logs that attribute <code>attributeName</code> in block
     * <code>blockNr</code> is missing.
     */
    void logAttrMissing(String attributeName, int blockNr) {
        TOSCAMetaFileParser.LOGGER.warn("Required attribute {} in block {} is missing.", attributeName, blockNr);
    }

    /**
     * Logs that attribute <code>attributeName</code> in block
     * <code>blockNr</code> has an invalid value. Correct is
     * <code>correctValue</code>.
     */
    void logAttrWrongVal(String attributeName, int blockNr, String correctValue) {
        TOSCAMetaFileParser.LOGGER.warn("Attribute {} in block {} has an invalid value. Must be {}.", attributeName, blockNr, correctValue);
    }

    /**
     * Logs that attribute <code>attributeName</code> in block
     * <code>blockNr</code> has an invalid value.
     */
    private void logAttrWrongVal(String attributeName, int blockNr) {
        TOSCAMetaFileParser.LOGGER.warn("Attribute {} in block {} has an invalid value.", attributeName, blockNr);
    }

    /**
     * Logs that attribute <code>attributeName</code> in block
     * <code>blockNr</code> has an empty value.
     */
    void logAttrValEmpty(String attributeName, int blockNr) {
        TOSCAMetaFileParser.LOGGER.warn("Attribute {} in block {} has a empty value.", attributeName, blockNr);
    }

    /**
     * Logs the ManifestProblem <code>problem</code>.
     */
    private void logManifestProblem(ManifestProblem problem) {
        TOSCAMetaFileParser.LOGGER.warn(problem.toString());
    }
}
