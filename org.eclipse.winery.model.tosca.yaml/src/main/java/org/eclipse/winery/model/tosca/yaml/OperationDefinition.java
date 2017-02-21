/**
 * This file is fork from https://github.com/CloudCycle2/YAML_Transformer,which is licensed under Apache 2.0
 */
package org.eclipse.winery.model.tosca.yaml;

/**
 * @author Sebi
 */
@Deprecated
public class OperationDefinition {

    private String implementation = "";

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        if (implementation != null) {
            this.implementation = implementation;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDefinition that = (OperationDefinition) o;

        if (!implementation.equals(that.implementation)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return implementation.hashCode();
    }
}
