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

package org.eclipse.winery.edmm;

import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.support.DefaultKeys;

public class EdmmTypeProperties {

    public static String BASE = "base";

    public static void getDefaultConfiguration(EdmmType edmmType, EntityGraph entityGraph) {
        switch (edmmType) {
            case COMPUTE:
                EntityId computeId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.COMPUTE.getName());
                entityGraph.addEntity(new MappingEntity(computeId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, computeId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case DATABASE:
                EntityId databaseId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.DATABASE.getName());
                entityGraph.addEntity(new MappingEntity(databaseId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, databaseId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case DBMS:
                EntityId dbmsId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.DBMS.getName());
                entityGraph.addEntity(new MappingEntity(dbmsId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.SOFTWARE_COMPONENT.getName(), dbmsId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.SOFTWARE_COMPONENT, entityGraph);
                break;
            case MYSQL_DATABASE:
                EntityId mySqlDatabaseId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.MYSQL_DATABASE.getName());
                entityGraph.addEntity(new MappingEntity(mySqlDatabaseId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.DATABASE.getName(), mySqlDatabaseId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.DATABASE, entityGraph);
                break;
            case MYSQL_DBMS:
                EntityId mySqlDbmsId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.MYSQL_DBMS.getName());
                entityGraph.addEntity(new MappingEntity(mySqlDbmsId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.MYSQL_DBMS.getName(), mySqlDbmsId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.DBMS, entityGraph);
                break;
            case SOFTWARE_COMPONENT:
                EntityId softwareComponent = EntityGraph.COMPONENT_TYPES.extend(EdmmType.SOFTWARE_COMPONENT.getName());
                entityGraph.addEntity(new MappingEntity(softwareComponent, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, softwareComponent.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case TOMCAT:
                EntityId tomcatId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.TOMCAT.getName());
                entityGraph.addEntity(new MappingEntity(tomcatId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.WEB_SERVER.getName(), tomcatId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.WEB_SERVER, entityGraph);
                break;
            case WEB_APPLICATION:
                EntityId webApplication = EntityGraph.COMPONENT_TYPES.extend(EdmmType.WEB_APPLICATION.getName());
                entityGraph.addEntity(new MappingEntity(webApplication, entityGraph));
                entityGraph.addEntity(new ScalarEntity(BASE, webApplication.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case WEB_SERVER:
                EntityId webServerId = EntityGraph.COMPONENT_TYPES.extend(EdmmType.WEB_SERVER.getName());
                entityGraph.addEntity(new MappingEntity(webServerId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.SOFTWARE_COMPONENT.getName(), webServerId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.SOFTWARE_COMPONENT, entityGraph);
                break;
            case CONNECTS_TO:
                EntityId connectsToId = EntityGraph.RELATION_TYPES.extend(EdmmType.CONNECTS_TO.getName());
                entityGraph.addEntity(new MappingEntity(connectsToId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.DEPENDS_ON.getName(), connectsToId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.DEPENDS_ON, entityGraph);
                break;
            case DEPENDS_ON:
                EntityId dependsOnId = EntityGraph.RELATION_TYPES.extend(EdmmType.DEPENDS_ON.getName());
                entityGraph.addEntity(new MappingEntity(dependsOnId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(null, dependsOnId.extend(DefaultKeys.EXTENDS), entityGraph));
                break;
            case HOSTED_ON:
                EntityId hostedOnId = EntityGraph.RELATION_TYPES.extend(EdmmType.HOSTED_ON.getName());
                entityGraph.addEntity(new MappingEntity(hostedOnId, entityGraph));
                entityGraph.addEntity(new ScalarEntity(EdmmType.DEPENDS_ON.getName(), hostedOnId.extend(DefaultKeys.EXTENDS), entityGraph));
                getDefaultConfiguration(EdmmType.DEPENDS_ON, entityGraph);
                break;
        }
    }
}
