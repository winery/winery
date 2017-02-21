/**
 * Copyright 2017 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.winery.model.tosca.yaml;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Huabing Zhao
 *
 */
public class ArtifactDefinitionTest {

  @Test
  public void testArtifactDefinition() {
    final String deploy_path = "test_deploy_path";
    final String description = "test_description";
    final String file = "test_file";
    final String repository = "test_repository";
    final String type = "test_type";
    
    ArtifactDefinition artifactDefinition = new ArtifactDefinition();
    artifactDefinition.setDeploy_path(deploy_path);
    assertEquals(artifactDefinition.getDeploy_path(), "test_deploy_path");
    
    artifactDefinition.setDescription(description);
    assertEquals(artifactDefinition.getDescription(), "test_description");
    
    artifactDefinition.setFile(file);
    assertEquals(artifactDefinition.getFile(), "test_file");
    
    artifactDefinition.setRepository(repository);
    assertEquals(artifactDefinition.getRepository(), "test_repository");
    
    artifactDefinition.setType(type);
    assertEquals(artifactDefinition.getType(), "test_type");
    
    assertEquals(artifactDefinition, artifactDefinition);
  }

}
