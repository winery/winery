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

package org.eclipse.winery.repository.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.winery.model.ids.GenericId;
import org.eclipse.winery.model.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.model.ids.definitions.EntityTypeId;
import org.eclipse.winery.model.ids.definitions.NodeTypeId;
import org.eclipse.winery.model.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.backend.filebased.AbstractFileBasedRepository;
import org.eclipse.winery.repository.backend.filebased.NamespaceProperties;
import org.eclipse.winery.repository.backend.patternAtlas.PatternAtlasConsumer;
import org.eclipse.winery.repository.common.RepositoryFileReference;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.mime.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PatternAtlasRepository extends AbstractFileBasedRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(PatternAtlasRepository.class);
    private final String uiUrl;

    private final AbstractFileBasedRepository repository;

    public PatternAtlasRepository(AbstractFileBasedRepository repository, List<PatternAtlasConsumer.PatternLanguage> languages,
                                  List<PatternAtlasConsumer.Pattern> patterns, String patternAtlasUI) throws IOException {
        super(repository.getRepositoryRoot(), repository.getId());
        this.repository = repository;
        this.uiUrl = patternAtlasUI;
        Map<String, NamespaceProperties> namespaces = this.getNamespaceManager().getAllNamespaces();
        for (PatternAtlasConsumer.PatternLanguage language : languages) {
            String namespace = language.getUri().toString();
            if (namespaces.get(namespace) == null) {
                namespaces.put(namespace, new NamespaceProperties(namespace, language.getName().replaceAll("\\W+", "")));
            }
            namespaces.get(namespace).setPatternCollection(true);
            getNamespaceManager().setNamespaceProperties(namespace, namespaces.get(namespace));
        }
        cloneMissingPatterns(patterns);
    }

    /**
     * Will clone any missing patterns. If no patterns are missing, nothing is done.
     */
    private void cloneMissingPatterns(List<PatternAtlasConsumer.Pattern> patterns) throws IOException {
        for (PatternAtlasConsumer.Pattern pattern : patterns) {
            if (pattern.getDeploymentModelingStructurePattern()) {
                NodeTypeId id = new NodeTypeId(pattern.getNamespace(), pattern.getName(), false);
                if (!this.repository.exists(id)) {
                    create(id, pattern);
                }
            }
            if (pattern.getDeploymentModelingBehaviorPattern()) {
                PolicyTypeId id = new PolicyTypeId(pattern.getNamespace(), pattern.getName(), false);
                if (!this.repository.exists(id)) {
                    create(id, pattern);
                }
            }
        }
    }

    private void create(EntityTypeId id, PatternAtlasConsumer.Pattern pattern) throws IOException {
        if (id instanceof NodeTypeId) {
            this.repository.setElement(id, pattern.toTNodeType());
        }
        if (id instanceof PolicyTypeId) {
            this.repository.setElement(id, pattern.toPolicyType());
        }
        RepositoryFileReference readMeReference = new RepositoryFileReference(id, "README.md");
        this.repository.putContentToFile(readMeReference, generateReadMe(pattern), MediaType.TEXT_PLAIN);

        if (pattern.getIconURL() != null) {
            RepositoryFileReference logoReference = new RepositoryFileReference(id, Paths.get("appearance"), Filename.FILENAME_BIG_ICON);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                try (CloseableHttpResponse execute = client.execute(new HttpGet(pattern.getIconURL().toURI()))) {
                    LOGGER.debug("Status of downloading File was {}", execute.getEntity());
                    this.repository.putContentToFile(
                        logoReference,
                        execute.getEntity().getContent(),
                        MediaType.image("png")
                    );
                }
            } catch (URISyntaxException e) {
                LOGGER.error("Error while downloading Pattern Icon!", e);
            }
        }
    }

    private String generateReadMe(PatternAtlasConsumer.Pattern pattern) {
        return "# " + pattern.getName().replace("-", " ") + " Pattern \n" +
            "[![PatternAtlas](https://img.shields.io/badge/PatternAtlas-" + pattern.getName().replace("-", "%20") + "-success.svg?logo=data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz4NCjwhLS0gR2VuZXJhdG9yOiBBZG9iZSBJbGx1c3RyYXRvciAxOS4wLjAsIFNWRyBFeHBvcnQgUGx1Zy1JbiAuIFNWRyBWZXJzaW9uOiA2LjAwIEJ1aWxkIDApICAtLT4NCjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0iRWJlbmVfMSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIiB4bWxuczp4bGluaz0iaHR0cDovL3d3dy53My5vcmcvMTk5OS94bGluayIgeD0iMHB4IiB5PSIwcHgiDQoJIHZpZXdCb3g9IjAgMCAxMi41IDEwLjEiIHN0eWxlPSJlbmFibGUtYmFja2dyb3VuZDpuZXcgMCAwIDEyLjUgMTAuMTsiIHhtbDpzcGFjZT0icHJlc2VydmUiPg0KPHN0eWxlIHR5cGU9InRleHQvY3NzIj4NCgkuc3Qwe2ZpbGw6I0ZGRkZGRjt9DQo8L3N0eWxlPg0KPHBhdGggY2xhc3M9InN0MCIgZD0iTTEyLjIsMS4zaC0wLjlWMC40YzAtMC4yLTAuMi0wLjQtMC40LTAuNGMwLDAtMC45LTAuMS0xLjksMC4xQzcuOSwwLjMsNi45LDAuOCw2LjMsMS42DQoJQzUuNywwLjgsNC44LDAuMywzLjYsMC4xQzIuNi0wLjEsMS43LDAsMS43LDBDMS41LDAsMS4zLDAuMiwxLjMsMC40djAuOUgwLjRDMC4yLDEuMywwLDEuNSwwLDEuN3Y3LjljMCwwLjEsMC4xLDAuMywwLjIsMC4zDQoJQzAuMywxMCwwLjUsMTAsMC42LDEwYzAsMCwzLjItMS4xLDUuNSwwYzAuMSwwLjEsMC4yLDAuMSwwLjMsMGMyLjMtMS4xLDUuNSwwLDUuNSwwSDEyYzAuMSwwLDAuMiwwLDAuMi0wLjENCgljMC4xLTAuMSwwLjItMC4yLDAuMi0wLjNWMS43QzEyLjYsMS41LDEyLjQsMS4zLDEyLjIsMS4zeiBNMSw5LjFWMi4yaDAuNXY1LjZjMCwwLjEsMC4xLDAuMiwwLjEsMC4zYzAuMSwwLjEsMC4yLDAuMSwwLjMsMC4xDQoJYzAsMCwxLjgtMC4zLDMuMiwwLjdDMy40LDguNSwxLjgsOC45LDEsOS4xeiBNNS45LDguNEM1LjMsNy45LDQuNiw3LjUsMy42LDcuM0MzLjIsNy4yLDIuOCw3LjIsMi40LDcuMmMtMC4xLDAtMC4yLDAtMC4zLDBWMC43bDAsMA0KCWMwLjgsMCwyLjgsMCwzLjcsMS42TDUuOSw4LjRMNS45LDguNHogTTYuOCwyLjRjMC45LTEuNiwyLjktMS43LDMuNy0xLjZ2Ni41Yy0wLjQsMC0wLjksMC0xLjQsMC4xQzguMiw3LjYsNy40LDcuOSw2LjgsOC41TDYuOCwyLjQNCglMNi44LDIuNHogTTcuNyw4LjhjMS4zLTAuOSwzLjEtMC43LDMuMi0wLjdzMC4yLDAsMC4zLTAuMWMwLjEtMC4xLDAuMS0wLjIsMC4xLTAuM1YyLjFoMC41VjlDMTAuOSw4LjksOS4zLDguNSw3LjcsOC44eiIvPg0KPC9zdmc+DQo=)]" +
            "(" + uiUrl + "/pattern-languages/" + pattern.getPatternLanguageId() + "/" + pattern.getId() + ") \n" +
            "\n" +
            "## Haftungsausschluss\n" +
            "\n" +
            "Dies ist ein Forschungsprototyp und enthält insbesondere Beiträge von Studenten.\n" +
            "Diese Software enthält möglicherweise Fehler und funktioniert möglicherweise, insbesondere bei variierten oder neuen Anwendungsfällen, nicht richtig.\n" +
            "Insbesondere beim Produktiveinsatz muss 1. die Funktionsfähigkeit geprüft und 2. die Einhaltung sämtlicher Lizenzen geprüft werden.\n" +
            "Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden ausgeschlossen.\n" +
            "\n" +
            "## Disclaimer of Warranty\n" +
            "\n" +
            "Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor\n" +
            "provides its Contributions) on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express\n" +
            "or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT,\n" +
            "MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE. You are solely responsible for determining the\n" +
            "appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of\n" +
            "permissions under this License.";
    }

    @Override
    @Deprecated
    public void serialize(TDefinitions definitions, OutputStream target) throws IOException {
        this.repository.serialize(definitions, target);
    }

    @Override
    public boolean exists(GenericId id) {
        return this.repository.exists(id);
    }

    @Override
    public boolean exists(RepositoryFileReference ref) {
        return this.repository.exists(ref);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
        this.repository.putContentToFile(ref, inputStream, mediaType);
    }

    @Override
    public void putContentToFile(RepositoryFileReference ref, InputStream inputStream) throws IOException {
        this.repository.putContentToFile(ref, inputStream);
    }

    @Override
    public void putDefinition(RepositoryFileReference ref, TDefinitions content) throws IOException {
        this.repository.putDefinition(ref, content);
    }

    @Override
    public TDefinitions definitionsFromRef(RepositoryFileReference ref) throws IOException {
        return this.repository.definitionsFromRef(ref);
    }

    @Override
    public <T extends DefinitionsChildId> SortedSet<T> getDefinitionsChildIds(Class<T> idClass, boolean omitDevelopmentVersions) {
        return this.repository.getDefinitionsChildIds(idClass, omitDevelopmentVersions);
    }
}
