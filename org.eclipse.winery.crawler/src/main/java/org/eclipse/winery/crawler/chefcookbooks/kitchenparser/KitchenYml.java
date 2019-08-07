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

package org.eclipse.winery.crawler.chefcookbooks.kitchenparser;

import java.util.List;
import java.util.Map;

public class KitchenYml {
    private List<Map> driver;
    private List<Map> verifier;
    private List<Map> platforms;
    private List<Map> suites;

    public List<Map> getDriver() {
        return driver;
    }

    public void setDriver(List<Map> driver) {
        this.driver = driver;
    }

    public List<Map> getVerifier() {
        return verifier;
    }

    public void setVerifier(List<Map> verifier) {
        this.verifier = verifier;
    }

    public List<Map> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Map> platforms) {
        this.platforms = platforms;
    }

    public List<Map> getSuites() {
        return suites;
    }

    public void setSuites(List<Map> suites) {
        this.suites = suites;
    }
}

