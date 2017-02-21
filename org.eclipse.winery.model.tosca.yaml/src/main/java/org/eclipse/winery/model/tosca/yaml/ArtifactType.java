/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArtifactType extends YAMLElement {

	private String derived_from = "";
	private String mime_type = "";
	private String[] file_ext = new String[0];
	private Map<String, PropertyDefinition> properties = new HashMap<String, PropertyDefinition>();
	private String version = "";
	private String description = "";
	
	
	public String getDerived_from() {
		return derived_from;
	}

	public void setDerived_from(String derived_from) {
		this.derived_from = derived_from;
	}

	public String getMime_type() {
		return mime_type;
	}

	public void setMime_type(String mime_type) {
		if (mime_type != null) {
			this.mime_type = mime_type;
		}
	}

	public String[] getFile_ext() {
		return file_ext;
	}

	public void setFile_ext(String[] file_ext) {
		if (file_ext != null) {
			this.file_ext = file_ext;
		}
	}

	public Map<String, PropertyDefinition> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, PropertyDefinition> properties) {
		if (properties != null) {
			this.properties = properties;
		}
	}
	
	

	public String getVersion() {
	  return version;
    }

    public void setVersion(String version) {
      this.version = version;
    }
  
    public String getDescription() {
      return description;
    }
  
    public void setDescription(String description) {
      this.description = description;
    }

  @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		ArtifactType that = (ArtifactType) o;

		if (!derived_from.equals(that.derived_from)) return false;
		if (!Arrays.equals(file_ext, that.file_ext)) return false;
		if (!mime_type.equals(that.mime_type)) return false;
		if (!properties.equals(that.properties)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + derived_from.hashCode();
		result = 31 * result + mime_type.hashCode();
		result = 31 * result + Arrays.hashCode(file_ext);
		result = 31 * result + properties.hashCode();
		return result;
	}
}