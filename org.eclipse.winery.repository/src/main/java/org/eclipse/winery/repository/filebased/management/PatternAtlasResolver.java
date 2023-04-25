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

package org.eclipse.winery.repository.filebased.management;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.backend.PatternAtlasRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.patternAtlas.PatternAtlasConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternAtlasResolver implements IRepositoryResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternAtlasResolver.class);

    private String patternAtlasApiURL;
    private String patternAtlasUI;

    public PatternAtlasResolver(String url, String ui) {
        patternAtlasApiURL = url;
        patternAtlasUI = ui;
    }

    @Override
    public String getVcsSystem() {
        // We have no VCS
        return null;
    }

    @Override
    public String getUrl() {
        return patternAtlasApiURL.toString();
    }

    @Override
    public String getRepositoryMaintainerUrl() {
        return patternAtlasApiURL.toString();
    }

    @Override
    public String getRepositoryMaintainer() {
        return null;
    }

    @Override
    public String getRepositoryName() {
        return "pattern-atlas";
    }

    @Override
    public PatternAtlasRepository createRepository(File repositoryLocation, String id) {
        FileBasedRepositoryConfiguration compositeConfiguration = new FileBasedRepositoryConfiguration(Paths.get(repositoryLocation.toString()));
        AbstractFileBasedRepository repository = RepositoryFactory.createXmlOrYamlRepository(compositeConfiguration, repositoryLocation.toPath(), id);

        PatternAtlasConsumer consumer;
        try {
            consumer = PatternAtlasConsumer.getInstance(new URL(this.patternAtlasApiURL));
        } catch (URISyntaxException | MalformedURLException e) {
            LOGGER.error("Invalid PatternAtlas URI provided!");
            LOGGER.info("Continuing with existing PatternAtlas Repository...");
            return new PatternAtlasRepository(repository, this.patternAtlasUI);
        }

        List<PatternAtlasConsumer.PatternLanguage> patternLanguages = consumer.getPatternLanguages();
        List<PatternAtlasConsumer.Pattern> patterns = new ArrayList<>();
        for (PatternAtlasConsumer.PatternLanguage language : patternLanguages) {
            List<PatternAtlasConsumer.Pattern> patternsOfPatternLanguage = consumer.getPatternsOfPatternLanguage(language);
            patterns.addAll(patternsOfPatternLanguage);
        }
        return new PatternAtlasRepository(
            repository,
            patternLanguages,
            patterns,
            this.patternAtlasUI
        );
    }
}
