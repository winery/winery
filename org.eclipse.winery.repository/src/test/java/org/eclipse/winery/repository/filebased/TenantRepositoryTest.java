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

package org.eclipse.winery.repository.filebased;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.configuration.Environments;
import org.eclipse.winery.model.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.RepositoryFactory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TenantRepositoryTest extends RepositoryTest {

    @Test
    public void testTenantRepository() throws Exception {
        Path repositoryRoot = Paths.get(Environments.getInstance().getRepositoryConfig().getRepositoryRoot());
        Environments.getInstance().getRepositoryConfig().setTenantRepository(true);
        RepositoryFactory.reconfigure();
        IRepository repository = RepositoryFactory.getRepository(repositoryRoot);
        assertTrue(repository instanceof TenantRepository);
        Files.createDirectory(repositoryRoot.resolve("tenant_1"));
        Files.createDirectory(repositoryRoot.resolve("tenant_2"));
        Files.createDirectory(repositoryRoot.resolve("tenant_3"));

        ((TenantRepository) repository).useTenant("tenant_1");
        repository.setElement(
            new ServiceTemplateId(QName.valueOf("{namespace}test1")),
            new TServiceTemplate.Builder("test1").build()
        );

        ((TenantRepository) repository).useTenant("tenant_3");
        repository.setElement(
            new ServiceTemplateId(QName.valueOf("{namespace}test53")),
            new TServiceTemplate.Builder("test53").build()
        );
        repository.setElement(
            new ServiceTemplateId(QName.valueOf("{namespace}test3")),
            new TServiceTemplate.Builder("test3").build()
        );

        SortedSet<ServiceTemplateId>[] serviceTemplates = new SortedSet[3];

        Thread tenant1Thread = new Thread(() -> {
            ((TenantRepository) repository).useTenant("tenant_1");
            serviceTemplates[0] = repository.getAllDefinitionsChildIds(ServiceTemplateId.class);
        });

        Thread tenant2Thread = new Thread(() -> {
            ((TenantRepository) repository).useTenant("tenant_2");
            serviceTemplates[1] = repository.getAllDefinitionsChildIds(ServiceTemplateId.class);
        });

        Thread tenant3Thread = new Thread(() -> {
            ((TenantRepository) repository).useTenant("tenant_3");
            serviceTemplates[2] = repository.getAllDefinitionsChildIds(ServiceTemplateId.class);
        });

        tenant1Thread.start();
        tenant2Thread.start();
        tenant3Thread.start();

        tenant1Thread.join();
        tenant2Thread.join();
        tenant3Thread.join();

        assertEquals(1, serviceTemplates[0].size());
        assertEquals(
            QName.valueOf("{namespace}test1"),
            serviceTemplates[0].first().getQName()
        );

        assertEquals(0, serviceTemplates[1].size());

        assertEquals(2, serviceTemplates[2].size());
        assertTrue(
            serviceTemplates[2].removeIf(serviceTemplateId -> serviceTemplateId.getQName().equals(QName.valueOf("{namespace}test53")))
        );
        assertTrue(
            serviceTemplates[2].removeIf(serviceTemplateId -> serviceTemplateId.getQName().equals(QName.valueOf("{namespace}test3")))
        );
        assertEquals(0, serviceTemplates[2].size());
    }
}
