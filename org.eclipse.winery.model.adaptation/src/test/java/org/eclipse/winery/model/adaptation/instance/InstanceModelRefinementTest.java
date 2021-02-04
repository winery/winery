/*******************************************************************************
 * Copyright (c) 2021 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.model.adaptation.instance;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TEntityTemplate;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.constants.OpenToscaBaseTypes;
import org.eclipse.winery.repository.TestWithGitRepoAndSshServer;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceModelRefinementTest extends TestWithGitRepoAndSshServer {

    private final static Logger logger = LoggerFactory.getLogger(InstanceModelRefinementTest.class);

    @BeforeAll
    static void beforeAll() throws IOException, URISyntaxException {
        setUp();
    }

    @AfterAll
    static void afterAll() throws IOException {
        shutDown();
    }

    @Test
    void refineApplication() throws Exception {
        this.setRevisionTo("origin/plain");

        TNodeTemplate mySpecialNode = new TNodeTemplate.Builder("mySpecialNode", OpenToscaBaseTypes.OperatingSystem)
            .build();
        InstanceModelRefinement modelRefinement = new InstanceModelRefinement((template, plugins) ->
            template.getNodeTemplate("mySpecialNode") != null
                ? null
                : new InstanceModelRefinementPlugin("noop") {
                @Override
                public TTopologyTemplate apply(TTopologyTemplate template) {
                    template.addNodeTemplate(mySpecialNode);
                    return template;
                }

                @Override
                public Set<String> determineAdditionalInputs(TTopologyTemplate template, ArrayList<String> nodeIdsToBeReplaced) {
                    return null;
                }

                @Override
                protected List<TTopologyTemplate> getDetectorGraphs() {
                    return null;
                }
            });

        TTopologyTemplate topologyTemplate = modelRefinement.refine(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "SshTest_w1-wip1", false)
        );

        assertNotNull(topologyTemplate);
        assertEquals(2, topologyTemplate.getNodeTemplates().size());
        assertTrue(
            topologyTemplate.getNodeTemplates().remove(mySpecialNode)
        );
    }

    @Test
    void refineEmpty() {
        InstanceModelRefinement modelRefinement = new InstanceModelRefinement((template, plugins) -> null);

        TTopologyTemplate topologyTemplate = modelRefinement.refine(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "myCoolNotExistingServiceTemplate", false)
        );

        assertNull(topologyTemplate);
    }

    @Test
    void completeRoundTripTest() throws Exception {
        this.setRevisionTo("origin/plain");

        // region ***** setup *****
        InstanceModelRefinement modelRefinement = new InstanceModelRefinement(
            // simply iterate over all plugins and execute it once
            new InstanceModelPluginChooser() {

                private final String[] pluginOrder = {"Tomcat", "SpringWebApplication", "MySQL-DB", "MySQL-DBMS", "PetClinic"};
                private int nextPlugin = 0;

                @Override
                public InstanceModelRefinementPlugin selectPlugin(TTopologyTemplate template, List<InstanceModelRefinementPlugin> plugins) {
                    if (nextPlugin < pluginOrder.length) {
                        String pluginId = pluginOrder[nextPlugin++];
                        Optional<InstanceModelRefinementPlugin> first = plugins.stream()
                            .filter(plugin -> plugin.getId().equals(pluginId))
                            .findFirst();

                        if (first.isPresent()) {
                            InstanceModelRefinementPlugin plugin = first.get();
                            plugin.setSelectedMatchId(0);

                            if (plugin.getSubGraphs() != null && plugin.getSubGraphs().size() > 0
                                && plugin.getSubGraphs().get(0).additionalInputs != null) {
                                Map<String, String> inputs = new HashMap<>();
                                plugin.getSubGraphs().get(0).additionalInputs
                                    .forEach(input -> {
                                        if (input.equals(InstanceModelUtils.vmIP)) {
                                            inputs.put(InstanceModelUtils.vmIP, "localhost");
                                        } else if (input.equals(InstanceModelUtils.vmPrivateKey)) {
                                            InputStream resourceAsStream = Objects.requireNonNull(
                                                ClassLoader.getSystemClassLoader().getResourceAsStream("winery.test")
                                            );
                                            try {
                                                inputs.put(InstanceModelUtils.vmPrivateKey, IOUtils.toString(resourceAsStream));
                                            } catch (IOException e) {
                                                logger.error("Error while retrieving private key", e);
                                            }
                                        } else if (input.equals(InstanceModelUtils.vmUser)) {
                                            inputs.put(InstanceModelUtils.vmUser, "test");
                                        } else if (input.equals(InstanceModelUtils.vmSshPort)) {
                                            inputs.put(InstanceModelUtils.vmSshPort, Integer.toString(sshPort));
                                        }
                                    });
                                inputs.put(InstanceModelUtils.vmSshPort, Integer.toString(sshPort));
                                plugin.setUserInputs(inputs, template, 0);
                            }

                            return plugin;
                        }
                    }

                    return null;
                }
            });

        String expectedMySqlPort = "3306";
        String expectedTomcatPort = "8080";
        String expectedDatabaseUser = "dbTestUser";
        String expectedDatabaseName = "testDatabase";
        String expectedPetClinicContext = "petClinicTest";

        sshd.setCommandFactory((channel, command) -> new SupportSuccessCommand(command) {
            @Override
            public String getReturnValue() {
                if (this.getCommand().startsWith("sudo /usr/bin/mysql --help | grep Distrib")) {
                    return "5.7";
                }
                if (this.getCommand().startsWith("sudo netstat -tulpen | grep mysqld")) {
                    return expectedMySqlPort;
                }
                if (this.getCommand().startsWith("sudo mysql -sN -e \"SELECT schema_name from INFORMATION_SCHEMA.SCHEMATA")
                    || (this.getCommand().startsWith("sudo cat /opt/tomcat/latest/webapps/")
                    && this.getCommand().endsWith("| sed -r 's/USE (.*);$/\\1/'"))) {
                    return expectedDatabaseName;
                }
                if (this.getCommand().startsWith("sudo find /opt/tomcat/latest/webapps/")
                    && this.getCommand().endsWith("| sed -r 's/database=(.*)$/\\1/'")) {
                    return "mysql";
                }
                if (this.getCommand().startsWith("sudo cat /opt/tomcat/latest/webapps/")
                    && this.getCommand().endsWith("IDENTIFIED BY (.*);$/\\2/'")) {
                    return expectedDatabaseUser;
                }
                if (this.getCommand().startsWith("sudo find /opt/tomcat/latest/webapps -name *.war")) {
                    return expectedPetClinicContext;
                }
                if (this.getCommand().startsWith("sudo cat /opt/tomcat/latest/RELEASE-NOTES | grep 'Apache Tomcat")) {
                    return "9";
                }
                if (this.getCommand().startsWith("sudo cat /opt/tomcat/latest/conf/server.xml | grep '<Connector port=\".*\"")) {
                    return expectedTomcatPort;
                }

                return null;
            }
        });
        // endregion

        TTopologyTemplate topologyTemplate = modelRefinement.refine(
            new ServiceTemplateId("http://opentosca.org/servicetemplates", "InstancePluginsTest_w1-wip1", false)
        );

        // region ***** assertions *****
        assertNotNull(topologyTemplate);
        assertEquals(5, topologyTemplate.getNodeTemplates().size());

        TNodeTemplate petClinic = topologyTemplate.getNodeTemplate("Pet_Clinic_w1_0");
        assertNotNull(petClinic);
        TEntityTemplate.Properties petClinicProperties = petClinic.getProperties();
        assertNotNull(petClinicProperties);
        assertTrue(petClinicProperties instanceof TEntityTemplate.WineryKVProperties);
        assertEquals(
            expectedPetClinicContext,
            ((TEntityTemplate.WineryKVProperties) petClinicProperties).getKVProperties().get("context")
        );

        TNodeTemplate mySqlDb = topologyTemplate.getNodeTemplate("MySQL-DB_0");
        assertNotNull(mySqlDb);
        TEntityTemplate.Properties mySqlDbProperties = mySqlDb.getProperties();
        assertNotNull(mySqlDbProperties);
        assertTrue(mySqlDbProperties instanceof TEntityTemplate.WineryKVProperties);
        assertEquals(
            expectedDatabaseName,
            ((TEntityTemplate.WineryKVProperties) mySqlDbProperties).getKVProperties().get("DBName")
        );
        assertEquals(
            expectedDatabaseUser,
            ((TEntityTemplate.WineryKVProperties) mySqlDbProperties).getKVProperties().get("DBUser")
        );

        TNodeTemplate tomcat = topologyTemplate.getNodeTemplate("Tomcat_0");
        assertNotNull(tomcat);
        TEntityTemplate.Properties tomcatProperties = tomcat.getProperties();
        assertNotNull(tomcatProperties);
        assertEquals(QName.valueOf("{http://opentosca.org/nodetypes}Tomcat_9-w1"), tomcat.getType());
        assertTrue(tomcatProperties instanceof TEntityTemplate.WineryKVProperties);
        assertEquals(
            expectedTomcatPort,
            ((TEntityTemplate.WineryKVProperties) tomcatProperties).getKVProperties().get("Port")
        );

        TNodeTemplate dbms = topologyTemplate.getNodeTemplate("MySQL-DBMS_0");
        assertNotNull(dbms);
        TEntityTemplate.Properties dbmsProperties = dbms.getProperties();
        assertNotNull(dbmsProperties);
        assertEquals(QName.valueOf("{http://opentosca.org/nodetypes}MySQL-DBMS_5.7-w1"), dbms.getType());
        assertTrue(dbmsProperties instanceof TEntityTemplate.WineryKVProperties);
        assertEquals(
            expectedMySqlPort,
            ((TEntityTemplate.WineryKVProperties) dbmsProperties).getKVProperties().get("DBMSPort")
        );
        // endregion
    }
}
