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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.common.configuration.FileBasedRepositoryConfiguration;
import org.eclipse.winery.repository.backend.PatternAtlasRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.backend.patternAtlas.PatternAtlasConsumer;

import org.eclipse.jgit.api.errors.GitAPIException;

public class PatternAtlasResolver implements IRepositoryResolver {
    
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
    public PatternAtlasRepository createRepository(File repositoryLocation, String id) throws IOException, GitAPIException, URISyntaxException {
        FileBasedRepositoryConfiguration compositeConfiguration = new FileBasedRepositoryConfiguration(Paths.get(repositoryLocation.toString()));

        PatternAtlasConsumer consumer = PatternAtlasConsumer.getInstance(new URL(this.patternAtlasApiURL));
        List<PatternAtlasConsumer.PatternLanguage> patternLanguages = consumer.getPatternLanguages();
        List<PatternAtlasConsumer.Pattern> patterns = new ArrayList<>();
        for (PatternAtlasConsumer.PatternLanguage language : patternLanguages) {
            List<PatternAtlasConsumer.Pattern> patternsOfPatternLanguage = consumer.getPatternsOfPatternLanguage(language);
            patterns.addAll(patternsOfPatternLanguage);
        }
        return new PatternAtlasRepository(RepositoryFactory.createXmlOrYamlRepository(compositeConfiguration, repositoryLocation.toPath(), id), patternLanguages, patterns, this.patternAtlasUI);
    }
}
