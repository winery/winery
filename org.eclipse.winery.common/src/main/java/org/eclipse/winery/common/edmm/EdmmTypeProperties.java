/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.common.edmm;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;

public class EdmmTypeProperties {

    public static String BASE = "base";

    public static void getDefaultConfiguration(EdmmType edmmType, EntityGraph entityGraph) {
        switch (edmmType) {
            case compute:
                EntityId computeId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.compute.getValue());
                entityGraph.addEntity(new MappingEntity(computeId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, computeId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case database:
                EntityId databaseId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.database.getValue());
                entityGraph.addEntity(new MappingEntity(databaseId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, databaseId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case dbms:
                EntityId dbmsId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.dbms.getValue());
                entityGraph.addEntity(new MappingEntity(dbmsId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.software_component.getValue(), dbmsId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.software_component, entityGraph);
                break;
            case mysql_database:
                EntityId mySqlDatabaseId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.mysql_database.getValue());
                entityGraph.addEntity(new MappingEntity(mySqlDatabaseId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.database.getValue(), mySqlDatabaseId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.database, entityGraph);
                break;
            case mysql_dbms:
                EntityId mySqlDbmsId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.mysql_dbms.getValue());
                entityGraph.addEntity(new MappingEntity(mySqlDbmsId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.mysql_dbms.getValue(), mySqlDbmsId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.dbms, entityGraph);
                break;
            case software_component:
                EntityId softwareComponent = EntityGraph.COMPONENT_TYPES.extend(EdmmType.software_component.getValue());
                entityGraph.addEntity(new MappingEntity(softwareComponent, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, softwareComponent.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case tomcat:
                EntityId tomcatId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.tomcat.getValue());
                entityGraph.addEntity(new MappingEntity(tomcatId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.web_server.getValue(), tomcatId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.web_server, entityGraph);
                break;
            case web_application:
                EntityId webApplication = EntityGraph.COMPONENT_TYPES.extend(EdmmType.web_application.getValue());
                entityGraph.addEntity(new MappingEntity(webApplication, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, webApplication.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case web_server:
                EntityId webServerId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.web_server.getValue());
                entityGraph.addEntity(new MappingEntity(webServerId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.software_component.getValue(), webServerId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.software_component, entityGraph);
                break;
            case connects_to:
                EntityId connectsToId = EntityGraph.RELATION_TYPES.extend(EdmmType.connects_to.getValue());
                entityGraph.addEntity(new MappingEntity(connectsToId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.depends_on.getValue(), connectsToId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.depends_on, entityGraph);
                break;
            case depends_on:
                EntityId dependsOnId = EntityGraph.RELATION_TYPES.extend(EdmmType.depends_on.getValue());
                entityGraph.addEntity(new MappingEntity(dependsOnId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(null, dependsOnId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case hosted_on:
                EntityId hostedOnId = EntityGraph.RELATION_TYPES.extend(EdmmType.hosted_on.getValue());
                entityGraph.addEntity(new MappingEntity(hostedOnId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.depends_on.getValue(), hostedOnId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.depends_on, entityGraph);
                break;
        }
    }
}
