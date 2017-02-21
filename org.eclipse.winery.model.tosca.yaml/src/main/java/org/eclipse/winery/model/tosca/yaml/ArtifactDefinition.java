/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

/**
 * Currently not used, but should be used in the future for type definitions!
 * @author Sebi
 */
public class ArtifactDefinition extends YAMLElement {
    private String type = "";
    private String file = "";
    private String repository = "";
    private String deploy_path = "";
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type != null) {
            this.type = type;
        }
    }

    public String getFile() {
      return file;
    }

    public void setFile(String file) {
      this.file = file;
    }

    public String getRepository() {
      return repository;
    }

    public void setRepository(String repository) {
      this.repository = repository;
    }

    public String getDeploy_path() {
      return deploy_path;
    }

    public void setDeploy_path(String deploy_path) {
      this.deploy_path = deploy_path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ArtifactDefinition that = (ArtifactDefinition) o;

        if (!type.equals(that.type)) return false;
        if (!file.equals(that.file)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + file.hashCode();
        return result;
    }
}
