/*******************************************************************************
 * Copyright (c) 2012-2018 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.repository.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.beans.NamespaceIdOptionalName;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.IdUtil;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.interfaces.QNameAlreadyExistsException;
import org.eclipse.winery.common.interfaces.QNameWithName;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TTopologyTemplate;
import org.eclipse.winery.model.tosca.kvproperties.WinerysPropertiesDefinition;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HttpURLConnectionFactory;
import com.sun.jersey.client.urlconnection.URLConnectionClientHandler;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class WineryRepositoryClient implements IWineryRepositoryClient {

    // thread-safe JAXB as inspired by https://jaxb.java.net/guide/Performance_and_thread_safety.html
    // The other possibility: Each subclass sets JAXBContext.newInstance(theSubClass.class); in its static {} part.
    // This seems to be more complicated than listing all subclasses in initContext
    public final static JAXBContext context = WineryRepositoryClient.initContext();

    private static final Logger LOGGER = LoggerFactory.getLogger(WineryRepositoryClient.class);

    // switch off validation, currently causes more trouble than it brings
    private static final boolean VALIDATING = false;

    private static final int MAX_NAME_CACHE_SIZE = 1000;

    private final Collection<WebResource> repositoryResources = new HashSet<WebResource>();
    private final Collection<String> knownURIs = new HashSet<String>();

    private final Client client;

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Class<? extends TEntityType>, Map<QName, TEntityType>> entityTypeDataCache;

    private final Map<GenericId, String> nameCache;
    private String primaryRepository = null;

    private WebResource primaryWebResource = null;

    // schema aware document builder
    private final DocumentBuilder toscaDocumentBuilder;

    // taken from http://stackoverflow.com/a/15253142/873282
    private static class ConnectionFactory implements HttpURLConnectionFactory {

        Proxy proxy;

        private void initializeProxy() {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        }

        @Override
        public HttpURLConnection getHttpURLConnection(URL url) throws IOException {
            this.initializeProxy();
            return (HttpURLConnection) url.openConnection(this.proxy);
        }
    }

    /**
     * Creates the client without the use of any proxy
     */
    public WineryRepositoryClient() {
        this(false);
    }

    /**
     * @param useProxy if a debugging proxy should be used
     * @throws IllegalStateException if DOM parser could not be created
     */
    public WineryRepositoryClient(boolean useProxy) {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        if (useProxy) {
            URLConnectionClientHandler ch = new URLConnectionClientHandler(new ConnectionFactory());
            this.client = new Client(ch, clientConfig);
        } else {
            this.client = Client.create(clientConfig);
        }

        this.entityTypeDataCache = new HashMap<>();
        this.nameCache = new HashMap<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        if (WineryRepositoryClient.VALIDATING) {
            factory.setValidating(true);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema;
            URL resource = this.getClass().getResource("/TOSCA-v1.0.xsd");
            try {
                schema = schemaFactory.newSchema(resource);
            } catch (SAXException e) {
                throw new IllegalStateException("Schema could not be initalized", e);
            }
            factory.setSchema(schema);
        }
        try {
            this.toscaDocumentBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("document builder could not be initalized", e);
        }
		/*
		 TODO: include this somehow - in the case of VALIDATING

		 Does not work with TTopolgoyTemplate as this is not allowed in the root of an XML document
		this.toscaDocumentBuilder.setErrorHandler(new ErrorHandler() {

			@Override
			public void warning(SAXParseException arg0) throws SAXException {
				throw arg0;
			}

			@Override
			public void fatalError(SAXParseException arg0) throws SAXException {
				throw arg0;
			}

			@Override
			public void error(SAXParseException arg0) throws SAXException {
				throw arg0;
			}
		});
		*/
    }

    private static JAXBContext initContext() {
        // code copied+adapted from JAXBSupport

        JAXBContext context;
        try {
            // For winery classes, eventually the package+jaxb.index method could be better. See http://stackoverflow.com/a/3628525/873282
            // @formatter:off
            context = JAXBContext.newInstance(
                TDefinitions.class,
                WinerysPropertiesDefinition.class);
            // @formatter:on
        } catch (JAXBException e) {
            LOGGER.error("Could not initialize JAXBContext", e);
            throw new IllegalStateException(e);
        }
        return context;
    }

    /**
     * Creates a unmarshaller
     *
     * @throws IllegalStateException if unmarshaller could not be instantiated
     */
    private static Unmarshaller createUnmarshaller() {
        Unmarshaller um;
        try {
            um = WineryRepositoryClient.context.createUnmarshaller();
        } catch (JAXBException e) {
            LOGGER.error("Could not instantiate unmarshaller", e);
            throw new IllegalStateException(e);
        }
        return um;
    }

    /*** methods directly from IWineryRepositoryClient ***/

    @Override
    public void addRepository(String uri) {
        if (this.knownURIs.add(uri)) {
            // URI is not already known, add a new resource
            WebResource wr = this.client.resource(uri);
            this.repositoryResources.add(wr);
            if (this.primaryRepository == null) {
                this.primaryRepository = uri;
                this.primaryWebResource = wr;
            }
        }
    }

    @Override
    public String getPrimaryRepository() {
        return this.primaryRepository;
    }

    @Override
    public void setPrimaryRepository(String uri) {
        this.addRepository(uri);
        // now we are sure that a web resource for the uri exists
        this.primaryRepository = uri;
        // Update the reference to the primaryWebResource
        // The appropriate resource has been created via
        // this.addRepository(uri);
        for (WebResource wr : this.repositoryResources) {
            if (wr.getURI().equals(uri)) {
                this.primaryWebResource = wr;
                break;
            }
        }
        assert (this.primaryWebResource != null);
    }

    /*** methods directly from IWineryRepository ***/

    @Override
    public SortedSet<String> getNamespaces() {
        SortedSet<String> res = new TreeSet<String>();
        for (WebResource wr : this.repositoryResources) {
            WebResource namespacesResource = wr.path("admin").path("namespaces");

            // this could be parsed using JAXB
            // (http://jersey.java.net/nonav/documentation/latest/json.html),
            // ~but we are short in time~ but this module will be removed in near future, so we do a quick hack
            TopologyNamespace[] nsList = namespacesResource.accept(MediaType.APPLICATION_JSON).get(TopologyNamespace[].class);

            for (TopologyNamespace ns : nsList) {
                res.add(ns.namespace);
            }
        }
        return res;
    }

    /**
     * Base method for getQNameListOfAllTypes and getAllTypes.
     */
    private <T extends TExtensibleElements> Map<WebResource, List<NamespaceIdOptionalName>> getWRtoNamespaceAndIdListMapOfAllTypes(String path) {
        Map<WebResource, List<NamespaceIdOptionalName>> res = new HashMap<WebResource, List<NamespaceIdOptionalName>>();
        for (WebResource wr : this.repositoryResources) {
            WebResource componentListResource = wr.path(path);

            // this could be parsed using JAXB
            // (http://jersey.java.net/nonav/documentation/latest/json.html),
            // but we are short in time, so we do a quick hack
            // The result also contains the optional name
            String idList = componentListResource.accept(MediaType.APPLICATION_JSON).get(String.class);
            LOGGER.trace(idList);
            List<NamespaceIdOptionalName> nsAndIdList;
            try {
                nsAndIdList = this.mapper.readValue(idList, new TypeReference<List<NamespaceIdOptionalName>>() {
                });
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                continue;
            }
            res.put(wr, nsAndIdList);
        }
        return res;
    }

    @Override
    public String getName(GenericId id) {
        if (this.nameCache.containsKey(id)) {
            return this.nameCache.get(id);
        }

        String name = null;
        for (WebResource wr : this.repositoryResources) {
            String pathFragment = IdUtil.getURLPathFragment(id);
            WebResource resource = wr.path(pathFragment).path("name");
            ClientResponse response = resource.accept(MediaType.TEXT_PLAIN_TYPE).get(ClientResponse.class);
            if (response.getClientResponseStatus() == ClientResponse.Status.OK) {
                name = response.getEntity(String.class);
                // break loop as the first match is the final result
                break;
            }
        }
        // if all resources did not return "OK", "null" is returned

        if (name != null) {
            if (this.nameCache.size() > WineryRepositoryClient.MAX_NAME_CACHE_SIZE) {
                // if cache grew too large, clear it.
                this.nameCache.clear();
            }
            this.nameCache.put(id, name);
        }

        return name;
    }

    @Override
    public <T extends TExtensibleElements> List<QName> getQNameListOfAllTypes(Class<T> className) {
        String path = Util.getURLpathFragmentForCollection(className);
        Map<WebResource, List<NamespaceIdOptionalName>> wRtoNamespaceAndIdListMapOfAllTypes = this.getWRtoNamespaceAndIdListMapOfAllTypes(path);
        Collection<List<NamespaceIdOptionalName>> namespaceAndIdListCollection = wRtoNamespaceAndIdListMapOfAllTypes.values();
        List<QName> res = new ArrayList<QName>(namespaceAndIdListCollection.size());
        for (List<NamespaceIdOptionalName> namespaceAndIdList : namespaceAndIdListCollection) {
            for (NamespaceIdOptionalName namespaceAndId : namespaceAndIdList) {
                QName qname = new QName(namespaceAndId.getNamespace(), namespaceAndId.getId());
                res.add(qname);
            }
        }
        return res;
    }

    /**
     * Fetches java objects at a given URL
     *
     * @param path      the path to use. E.g., "nodetypes" for node types, ...
     * @param className the class of the expected return type. May be TDefinitions or TEntityType. TDefinitions the mode
     *                  is that the import statement are recursively resolved and added to the returned Defintitions
     *                  elment
     */
    // we convert an object to T if it T is definitions
    // does not work without compiler error
    @SuppressWarnings("unchecked")
    private <T extends TExtensibleElements> Collection<T> getAllTypes(String path, Class<T> className) {
        Map<WebResource, List<NamespaceIdOptionalName>> wrToNamespaceAndIdListMapOfAllTypes = this.getWRtoNamespaceAndIdListMapOfAllTypes(path);
        // now we now all QNames. We have to fetch the full content now

        Collection<T> res = new LinkedList<T>();
        for (WebResource wr : wrToNamespaceAndIdListMapOfAllTypes.keySet()) {
            WebResource componentListResource = wr.path(path);

            // go through all ids and fetch detailed information on each
            // type

            for (NamespaceIdOptionalName nsAndId : wrToNamespaceAndIdListMapOfAllTypes.get(wr)) {
                TDefinitions definitions = WineryRepositoryClient.getDefinitions(componentListResource, nsAndId.getNamespace(), nsAndId.getId());
                if (definitions == null) {
                    // try next one
                    continue;
                }

                T result;

                if (TDefinitions.class.equals(className)) {
                    // mode: complete definitions
                    result = (T) definitions;
                } else {
                    // mode: only the nested element
                    // convention: first element in list is the element we look for
                    if (definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().isEmpty()) {
                        result = null;
                        LOGGER.error("Type {}/{} was found, but did not return any data", nsAndId.getNamespace(), nsAndId.getId());
                    } else {
                        LOGGER.trace("Probably found valid data for {}/{}", nsAndId.getNamespace(), nsAndId.getId());
                        result = (T) definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);

                        // caching disabled as we also handle TServiceTemplates
                        //this.cache((TEntityType) result, new QName(nsAndId.getNamespace(), nsAndId.getId()));
                    }
                }

                // TODO: if multiple repositories are used, the new element
                // should be put "sorted" into the list. This could be done by
                // add(parsedResult, index), where index is calculated by
                // incrementing index as long as the current element is smaller
                // than the element to insert.
                if (result != null) {
                    res.add(result);
                }
            }
        }
        return res;
    }

    /**
     * Caches the TEntityType data of a QName to avoid multiple get requests
     * <p>
     * NOT thread safe
     */
    private void cache(TEntityType et, QName qName) {
        Map<QName, TEntityType> map;
        if ((map = this.entityTypeDataCache.get(et.getClass())) == null) {
            map = new HashMap<>();
            this.entityTypeDataCache.put(et.getClass(), map);
        } else {
            // quick hack to keep cache size small
            if (map.size() > 1000) {
                map.clear();
            }
        }
        map.put(qName, et);
    }

    private static WebResource getTopologyTemplateWebResource(WebResource base, QName serviceTemplate) {
        String nsEncoded = Util.DoubleURLencode(serviceTemplate.getNamespaceURI());
        String idEncoded = Util.DoubleURLencode(serviceTemplate.getLocalPart());
        WebResource res = base.path("servicetemplates").path(nsEncoded).path(idEncoded).path("topologytemplate");
        return res;
    }

    private static WebResource getTopologyTemplateWebResource(WebResource base, QName serviceTemplate, String parentPath, String elementPath) {
        String nsEncoded = Util.DoubleURLencode(serviceTemplate.getNamespaceURI());
        String idEncoded = Util.DoubleURLencode(serviceTemplate.getLocalPart());
        WebResource res = base.path(parentPath).path(nsEncoded).path(idEncoded).path(elementPath);
        return res;
    }

    @Override
    public Collection<QNameWithName> getListOfAllInstances(Class<? extends DefinitionsChildId> clazz) {
        // inspired by getQNameListOfAllTypes
        String path = Util.getRootPathFragment(clazz);
        Map<WebResource, List<NamespaceIdOptionalName>> wRtoNamespaceAndIdListMapOfAllTypes = this.getWRtoNamespaceAndIdListMapOfAllTypes(path);
        Collection<List<NamespaceIdOptionalName>> namespaceAndIdListCollection = wRtoNamespaceAndIdListMapOfAllTypes.values();
        List<QNameWithName> res = new ArrayList<QNameWithName>(namespaceAndIdListCollection.size());

        for (List<NamespaceIdOptionalName> namespaceAndIdList : namespaceAndIdListCollection) {
            for (NamespaceIdOptionalName namespaceAndId : namespaceAndIdList) {
                QNameWithName qn = new QNameWithName();
                qn.qname = new QName(namespaceAndId.getNamespace(), namespaceAndId.getId());
                qn.name = namespaceAndId.getName();
                res.add(qn);
            }
        }
        return res;
    }

    @Override
    public <T extends TExtensibleElements> Collection<T> getAllTypes(Class<T> c) {
        String urlPathFragment = Util.getURLpathFragmentForCollection(c);
        Collection<T> allTypes = this.getAllTypes(urlPathFragment, c);
        return allTypes;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TEntityType> T getType(QName qname, Class<T> type) {
        T res = null;
        if (this.entityTypeDataCache.containsKey(type)) {
            Map<QName, TEntityType> map = this.entityTypeDataCache.get(type);
            if (map.containsKey(qname)) {
                res = (T) map.get(qname);
            }
        }

        if (res == null) {
            // not yet seen, try to fetch resource

            for (WebResource wr : this.repositoryResources) {
                String path = Util.getURLpathFragmentForCollection(type);

                TDefinitions definitions = WineryRepositoryClient.getDefinitions(wr, path, qname.getNamespaceURI(), qname.getLocalPart());

                if (definitions == null) {
                    // in case of an error, just try the next one
                    continue;
                }

                res = (T) definitions.getServiceTemplateOrNodeTypeOrNodeTypeImplementation().get(0);
                this.cache(res, qname);
                break;
            }
        }

        return res;
    }

    @Override
    public Definitions getDefinitions(DefinitionsChildId id) {
        for (WebResource wr : this.repositoryResources) {
            String path = Util.getUrlPath(id);
            Definitions definitions = WineryRepositoryClient.getDefinitions(wr.path(path));
            if (definitions == null) {
                // in case of an error, just try the next one
                continue;
            }
            TExtensibleElements element = definitions.getElement();
            if (element instanceof TEntityType) {
                this.cache((TEntityType) element, id.getQName());
                return definitions;
            }
        }
        return new Definitions();
    }

    /**
     * Tries to retrieve a TDefinitions from the given resource / encoded(ns) /
     * encoded(localPart)
     *
     * @return null if 404 or other error
     */
    private static TDefinitions getDefinitions(WebResource wr, String path, String ns, String localPart) {
        WebResource componentListResource = wr.path(path);
        return WineryRepositoryClient.getDefinitions(componentListResource, ns, localPart);
    }

    private static Definitions getDefinitions(WebResource instanceResource) {
        // TODO: org.eclipse.winery.repository.resources.AbstractComponentInstanceResource.getDefinitionsWithAssociatedThings() could be used to do the resolving at the server

        ClientResponse response = instanceResource.accept(MimeTypes.MIMETYPE_TOSCA_DEFINITIONS).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            // also handles 404
            return null;
        }

        Definitions definitions;
        try {
            Unmarshaller um = WineryRepositoryClient.createUnmarshaller();
            definitions = (Definitions) um.unmarshal(response.getEntityInputStream());
        } catch (JAXBException e) {
            LOGGER.error("Could not unmarshal Definitions", e);
            // try next service
            return null;
        }
        return definitions;
    }

    /**
     * Tries to retrieve a TDefinitions from the given resource / encoded(ns) /
     * encoded(localPart)
     *
     * @return null if 404 or other error
     */
    private static TDefinitions getDefinitions(WebResource componentListResource, String ns, String localPart) {
        // we need double encoding as the client decodes the URL once
        String nsEncoded = Util.DoubleURLencode(ns);
        String idEncoded = Util.DoubleURLencode(localPart);

        WebResource instanceResource = componentListResource.path(nsEncoded).path(idEncoded);
        return getDefinitions(instanceResource);
    }

    @Override
    public <T extends TEntityType> Collection<TDefinitions> getAllTypesWithAssociatedElements(Class<T> c) {
        String urlPathFragment = Util.getURLpathFragmentForCollection(c);
        Collection<TDefinitions> allTypes = this.getAllTypes(urlPathFragment, TDefinitions.class);
        return allTypes;
    }

    /**
     * @param stream the stream to parse
     * @return null if document is invalid
     */
    private Document parseAndValidateTOSCAXML(InputStream stream) {
        Document document;
        try {
            document = this.toscaDocumentBuilder.parse(stream);
        } catch (SAXException | IOException e) {
            LOGGER.debug("Could not parse TOSCA file", e);
            return null;
        }
        return document;
    }

    @Override
    public TTopologyTemplate getTopologyTemplate(QName serviceTemplate) {
        // we try all repositories until the first hit
        for (WebResource wr : this.repositoryResources) {
            WebResource r = WineryRepositoryClient.getTopologyTemplateWebResource(wr, serviceTemplate);
            ClientResponse response = r.accept(MediaType.TEXT_XML).get(ClientResponse.class);
            if (response.getClientResponseStatus() == ClientResponse.Status.OK) {
                TTopologyTemplate topologyTemplate;
                Document doc = this.parseAndValidateTOSCAXML(response.getEntityInputStream());
                if (doc == null) {
                    // no valid document
                    return null;
                }
                try {
                    topologyTemplate = WineryRepositoryClient.createUnmarshaller().unmarshal(doc.getDocumentElement(), TTopologyTemplate.class).getValue();
                } catch (JAXBException e) {
                    LOGGER.debug("Could not parse topology, returning null", e);
                    return null;
                }
                // first hit: immediately stop and return result
                return topologyTemplate;
            }
        }
        // nothing found
        return null;
    }

    @Override
    public TTopologyTemplate getTopologyTemplate(QName serviceTemplate, String parentPath, String elementPath) {
        /* code copied from org.eclipse.winery.repository.client.WineryRepositoryClient.getTopologyTemplate(javax.xml.namespace.QName) and adapted to use "getTopologyTemplateWebResource" with four parameters */

        // we try all repositories until the first hit
        for (WebResource wr : this.repositoryResources) {
            WebResource r = WineryRepositoryClient.getTopologyTemplateWebResource(wr, serviceTemplate, parentPath, elementPath);
            ClientResponse response = r.accept(MediaType.TEXT_XML).get(ClientResponse.class);
            if (response.getClientResponseStatus() == ClientResponse.Status.OK) {
                TTopologyTemplate topologyTemplate;
                Document doc = this.parseAndValidateTOSCAXML(response.getEntityInputStream());
                if (doc == null) {
                    // no valid document
                    return null;
                }
                try {
                    topologyTemplate = WineryRepositoryClient.createUnmarshaller().unmarshal(doc.getDocumentElement(), TTopologyTemplate.class).getValue();
                } catch (JAXBException e) {
                    LOGGER.debug("Could not parse topology, returning null", e);
                    return null;
                }
                // first hit: immediately stop and return result
                return topologyTemplate;
            }
        }
        // nothing found
        return null;
    }

    @Override
    public void setTopologyTemplate(QName serviceTemplate, TTopologyTemplate topologyTemplate) throws Exception {
        WebResource r = WineryRepositoryClient.getTopologyTemplateWebResource(this.primaryWebResource, serviceTemplate);
        String xmlAsString = Util.getXMLAsString(TTopologyTemplate.class, topologyTemplate);
        ClientResponse response = r.type(MediaType.TEXT_XML).put(ClientResponse.class, xmlAsString);
        LOGGER.debug(response.toString());
        int status = response.getStatus();
        if ((status < 200) || (status >= 300)) {
            throw new Exception(response.toString());
        }
    }

    @Override
    public QName getArtifactTypeQNameForExtension(String extension) {
        // we try all repositories until the first hit
        for (WebResource wr : this.repositoryResources) {
            WebResource artifactTypesResource = wr.path("artifacttypes").queryParam("extension", extension);
            ClientResponse response = artifactTypesResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
            if (response.getClientResponseStatus() == ClientResponse.Status.OK) {
                QName res = QName.valueOf(response.getEntity(String.class));
                return res;
            }
        }
        return null;
    }

    /**
     * Does NOT check for global QName uniqueness, only in the scope of all
     * artifact templates
     */
    @Override
    public void createArtifactTemplate(QName qname, QName artifactType) throws QNameAlreadyExistsException {
        WebResource artifactTemplates = this.primaryWebResource.path("artifacttemplates");
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.putSingle("namespace", qname.getNamespaceURI());
        map.putSingle("name", qname.getLocalPart());
        map.putSingle("type", artifactType.toString());
        ClientResponse response = artifactTemplates.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_PLAIN).post(ClientResponse.class, map);
        if (response.getClientResponseStatus() != ClientResponse.Status.CREATED) {
            // TODO: pass ClientResponse.Status somehow
            // TODO: more fine grained checking for error message. Not all
            // failures are that the QName already exists
            LOGGER.debug(String.format("Error %d when creating id %s from URI %s", response.getStatus(), qname.toString(), this.primaryWebResource.getURI().toString()));
            throw new QNameAlreadyExistsException();
        }
        // no further return is made
    }

    @Override
    public void createComponent(QName qname, Class<? extends DefinitionsChildId> idClass) throws QNameAlreadyExistsException {
        WebResource resource = this.primaryWebResource.path(Util.getRootPathFragment(idClass));
        MultivaluedMap<String, String> map = new MultivaluedMapImpl();
        map.putSingle("namespace", qname.getNamespaceURI());
        map.putSingle("name", qname.getLocalPart());
        ClientResponse response = resource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.TEXT_PLAIN).post(ClientResponse.class, map);
        if (response.getClientResponseStatus() != ClientResponse.Status.CREATED) {
            // TODO: pass ClientResponse.Status somehow
            // TODO: more fine grained checking for error message. Not all failures are that the QName already exists
            LOGGER.debug(String.format("Error %d when creating id %s from URI %s", response.getStatus(), qname.toString(), this.primaryWebResource.getURI().toString()));
            throw new QNameAlreadyExistsException();
        }
        // no further return is made
    }

    @Override
    public void forceDelete(GenericId id) throws IOException {
        String pathFragment = IdUtil.getURLPathFragment(id);
        for (WebResource wr : this.repositoryResources) {
            ClientResponse response = wr.path(pathFragment).delete(ClientResponse.class);
            if ((response.getClientResponseStatus() != ClientResponse.Status.NO_CONTENT) || (response.getClientResponseStatus() != ClientResponse.Status.NOT_FOUND)) {
                LOGGER.debug(String.format("Error %d when deleting id %s from URI %s", response.getStatus(), id.toString(), wr.getURI().toString()));
            }
        }
    }

    private class NamespaceAndIdAsString {
        String namespace;
        String id;
    }

    /**
     * @param oldId the old id
     * @param newId the new id
     */
    @Override
    public void rename(DefinitionsChildId oldId, DefinitionsChildId newId) throws IOException {
        String pathFragment = IdUtil.getURLPathFragment(oldId);
        NamespaceAndIdAsString namespaceAndIdAsString = new NamespaceAndIdAsString();
        namespaceAndIdAsString.namespace = newId.getNamespace().getDecoded();
        namespaceAndIdAsString.id = newId.getXmlId().getDecoded();
        for (WebResource wr : this.repositoryResources) {
            // TODO: Check whether namespaceAndIdAsString is the correct data type expected at the resource
            ClientResponse response = wr.path(pathFragment).path("id").post(ClientResponse.class, namespaceAndIdAsString);
            if ((response.getClientResponseStatus() != ClientResponse.Status.NO_CONTENT) || (response.getClientResponseStatus() != ClientResponse.Status.NOT_FOUND)) {
                LOGGER.debug(String.format("Error %d when renaming DefinitionsChildId %s to %s at %s", response.getStatus(), oldId.toString(), newId.toString(), wr.getURI().toString()));
            }
        }
    }

    @Override
    public void duplicate(DefinitionsChildId from, DefinitionsChildId newId) throws IOException {
        throw new IllegalStateException("not yet implemented");
    }

    @Override
    public void forceDelete(Class<? extends DefinitionsChildId> definitionsChildIdClazz, Namespace namespace) throws IOException {
        throw new IllegalStateException("not yet implemented");
    }

    @Override
    public boolean primaryRepositoryAvailable() {
        if (this.primaryWebResource == null) {
            return false;
        }

        ClientResponse response = this.primaryWebResource.get(ClientResponse.class);
        boolean res = (response.getClientResponseStatus() == ClientResponse.Status.OK);
        return res;
    }
}
