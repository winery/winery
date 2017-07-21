/*******************************************************************************
 * Copyright (c) 2013 Pascal Hirmer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Pascal Hirmer - initial API and implementation
 *******************************************************************************/

package org.eclipse.winery.topologymodeler.addons.topologycompleter.helper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.model.tosca.TTopologyTemplate;

import org.slf4j.LoggerFactory;

/**
 * This class contains helper methods to call the REST API and PUT/POST information to it.
 */
public class RESTHelper {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(RESTHelper.class.getName());

    /**
     * This method uses a REST call to save the completed {@link TTopologyTemplate} to the repository.
     *
     * @param topology
     *            the {@link TTopologyTemplate} to be saved
     * @param topologyTemplateURL
     *            the URL the {@link TTopologyTemplate} is saved to
     * @param overwriteTopology
     *               whether the topology is overwritten or a new topology shall be created
     * @param topologyName
     *               the name of the newly created topology to build the URL if a new topology shall be created
     * @param topologyNamespace
     *                the name space of the newly created topology to build the URL if a new topology shall be created
     * @param repositoryURL
     *               the URL to the repository to build the URL if a new topology shall be created
     */
    public static void saveCompleteTopology(TTopologyTemplate topology, String topologyTemplateURL, boolean overwriteTopology, String topologyName, String topologyNamespace, String repositoryURL) {
        try {

            URL url = null;

            if (overwriteTopology) {
                url = new URL(topologyTemplateURL);
            } else {
                // this is necessary to avoid encoding issues
                topologyNamespace = Util.URLencode(topologyNamespace);
                // build the URL with the repositoryURL, the topology namespace and the topology name
                url = new URL(repositoryURL + "/servicetemplates/" + topologyNamespace + "/" + topologyName + "/topologytemplate/");

                LOGGER.info("The URL the topology is saved to: " + url);
            }

            // using SSL
            System.setProperty("javax.net.ssl.trustStore", "jssecacerts.cert");

            // configure message
            HttpURLConnection urlConn;
            urlConn = (HttpURLConnection) url.openConnection();

            LOGGER.info("Sending HTTP request...");

            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("PUT");
            urlConn.setRequestProperty("Content-type", "text/xml");
            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream());

            // build the XML string to be saved
            TTopologyTemplate outputTopology = JAXBHelper.buildXML(topology);
            String outputString = JAXBHelper.getXMLAsString(outputTopology.getClass(), outputTopology);

            LOGGER.info(outputString);
            LOGGER.info("Sending output to Winery.");

            out.write(outputString);
            out.close();
            urlConn.getOutputStream().close();
            LOGGER.info("Output sent, waiting for response...");
            urlConn.getInputStream();

            LOGGER.info("HTTP Response Code is: " + urlConn.getResponseCode());

        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
}
