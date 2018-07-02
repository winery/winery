/********************************************************************************
 * Copyright (c) 2017-2018 Contributors to the Eclipse Foundation
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
package org.eclipse.winery.model.tosca.yaml.tosca.datatypes;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Map;
import java.util.Objects;

@XmlType(name = "Credential", namespace = " http://docs.oasis-open.org/tosca/ns/simple/yaml/1.0", propOrder = {
    "protocol",
    "tokenType",
    "token",
    "keys",
    "user"
})
public class Credential {

    @XmlElement
    private String protocol;

    @XmlElement(name = "token_type", required = true, defaultValue = "password")
    private String tokenType;

    @XmlElement(required = true)
    private String token;

    @XmlElement
    private Map<String, String> keys;

    @XmlElement
    private String user;

    public Credential() {
        this.tokenType = "password";
    }

    public Credential(Builder builder) {
        this.protocol = builder.protocol;
        this.tokenType = builder.tokenType;
        this.token = builder.token;
        this.keys = builder.keys;
        this.user = builder.user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Credential)) return false;
        Credential that = (Credential) o;
        return Objects.equals(getProtocol(), that.getProtocol()) &&
            Objects.equals(getTokenType(), that.getTokenType()) &&
            Objects.equals(getToken(), that.getToken()) &&
            Objects.equals(getKeys(), that.getKeys()) &&
            Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProtocol(), getTokenType(), getToken(), getKeys(), getUser());
    }

    @Override
    public String toString() {
        return "Credential{" +
            "protocol='" + getProtocol() + '\'' +
            ", tokenType='" + getTokenType() + '\'' +
            ", token='" + getToken() + '\'' +
            ", keys=" + getKeys() +
            ", user='" + getUser() + '\'' +
            '}';
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, String> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public static class Builder {
        private final String tokenType;
        private String protocol;
        private String token;
        private Map<String, String> keys;
        private String user;

        public Builder() {
            this.tokenType = "password";
        }

        public Builder(String tokenType) {
            this.tokenType = Objects.nonNull(tokenType) ? tokenType : "password";
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setKeys(Map<String, String> keys) {
            this.keys = keys;
            return this;
        }

        public Builder setUser(String user) {
            this.user = user;
            return this;
        }

        public Credential build() {
            return new Credential(this);
        }
    }
}
