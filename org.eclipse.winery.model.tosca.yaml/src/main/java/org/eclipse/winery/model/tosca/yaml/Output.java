/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

public class Output extends YAMLElement {

	private Object value;

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if (value != null) {
			this.value = value;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Output output = (Output) o;

		if (value != null ? !value.equals(output.value) : output.value != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}
}