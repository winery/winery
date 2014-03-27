/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.repository.backend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.IdUtil;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.definitions.EntityTypeId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.common.ids.elements.PlansId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKV;
import org.eclipse.winery.common.propertydefinitionkv.PropertyDefinitionKVList;
import org.eclipse.winery.common.propertydefinitionkv.WinerysPropertiesDefinition;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.model.tosca.ObjectFactory;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TEntityType.PropertiesDefinition;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.JAXBSupport;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.constants.Filename;
import org.eclipse.winery.repository.datatypes.ids.admin.AdminId;
import org.eclipse.winery.repository.datatypes.ids.elements.VisualAppearanceId;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;
import org.eclipse.winery.repository.resources.IHasTypeReference;
import org.eclipse.winery.repository.resources._support.IPersistable;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.eclipse.winery.repository.resources.entitytypes.TopologyGraphElementEntityTypeResource;
import org.eclipse.winery.repository.resources.imports.xsdimports.XSDImportsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;

/**
 * Contains generic utility functions for the Backend
 * 
 * Contains everything that is useful for our ids etc. Does <em>not</em> contain
 * anything that has to do with resources
 */
public class BackendUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(BackendUtils.class);
	
	
	/**
	 * Deletes given file/dir and returns appropriate response code
	 */
	public static Response delete(GenericId id) {
		if (!Repository.INSTANCE.exists(id)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			Repository.INSTANCE.forceDelete(id);
		} catch (IOException e) {
			BackendUtils.logger.error(e.getMessage(), e);
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.noContent().build();
	}
	
	/**
	 * Deletes given file and returns appropriate response code
	 */
	public static Response delete(RepositoryFileReference ref) {
		if (!Repository.INSTANCE.exists(ref)) {
			return Response.status(Status.NOT_FOUND).build();
		}
		try {
			Repository.INSTANCE.forceDelete(ref);
		} catch (IOException e) {
			BackendUtils.logger.error(e.getMessage(), e);
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.ok().build();
	}
	
	/**
	 * Generates given TOSCA element and returns appropriate response code <br  />
	 * 
	 * In the case of an existing resource, the other possible return code is
	 * 302. This code has no Status constant, therefore we use Status.CONFLICT,
	 * which is also possible.
	 * 
	 * @return <ul>
	 *         <li>
	 *         <ul>
	 *         <li>Status.CREATED (201) if the resource has been created,</li>
	 *         <li>Status.CONFLICT if the resource already exists,</li>
	 *         <li>Status.INTERNAL_SERVER_ERROR (500) if something went wrong</li>
	 *         </ul>
	 *         </li>
	 *         <li>URI: the absolute URI of the newly created resource</li>
	 *         </ul>
	 */
	public static ResourceCreationResult create(GenericId id) {
		ResourceCreationResult res = new ResourceCreationResult();
		if (Repository.INSTANCE.exists(id)) {
			// res.setStatus(302);
			res.setStatus(Status.CONFLICT);
		} else {
			if (Repository.INSTANCE.flagAsExisting(id)) {
				res.setStatus(Status.CREATED);
				// @formatter:off
				// This method is a generic method
				// We cannot return an "absolute" URL as the URL is always
				// relative to the caller
				// Does not work: String path = Prefs.INSTANCE.getResourcePath()
				// + "/" +
				// Utils.getURLforPathInsideRepo(id.getPathInsideRepo());
				// We distinguish between two cases: TOSCAcomponentId and
				// TOSCAelementId
				// @formatter:on
				String path;
				if (id instanceof TOSCAComponentId) {
					// here, we return namespace + id, as it is only possible to
					// post on the TOSCA component*s* resource to create an
					// instance of a TOSCA component
					TOSCAComponentId tcId = (TOSCAComponentId) id;
					path = tcId.getNamespace().getEncoded() + "/" + tcId.getXmlId().getEncoded() + "/";
				} else {
					assert (id instanceof TOSCAElementId);
					// We just return the id as we assume that only the parent
					// of this id may create sub elements
					path = id.getXmlId().getEncoded() + "/";
				}
				// we have to encode it twice to get correct URIs
				path = Utils.getURLforPathInsideRepo(path);
				URI uri = Utils.createURI(path);
				res.setUri(uri);
				res.setId(id);
			} else {
				res.setStatus(Status.INTERNAL_SERVER_ERROR);
			}
		}
		return res;
	}
	
	/**
	 * 
	 * Sends the file if modified and "not modified" if not modified future work
	 * may put each file with a unique id in a separate folder in tomcat * use
	 * that static URL for each file * if file is modified, URL of file changes
	 * * -> client always fetches correct file
	 * 
	 * additionally "Vary: Accept" header is added (enables caching of the
	 * response)
	 * 
	 * method header for calling method public <br />
	 * <code>Response getXY(@HeaderParam("If-Modified-Since") String modified) {...}</code>
	 * 
	 * 
	 * @param ref references the file to be send
	 * @param modified - HeaderField "If-Modified-Since" - may be "null"
	 * @return Response to be sent to the client
	 */
	public static Response returnRepoPath(RepositoryFileReference ref, String modified) {
		return BackendUtils.returnRefAsResponseBuilder(ref, modified).build();
	}
	
	/**
	 * @return true if given fileDate is newer then the modified date (or
	 *         modified is null)
	 */
	public static boolean isFileNewerThanModifiedDate(long millis, String modified) {
		if (modified == null) {
			return true;
		}
		
		Date modifiedDate = null;
		
		assert (Locale.getDefault() == Locale.ENGLISH);
		try {
			modifiedDate = DateUtils.parseDate(modified, org.apache.http.impl.cookie.DateUtils.DEFAULT_PATTERNS);
		} catch (ParseException e) {
			BackendUtils.logger.error(e.getMessage(), e);
		}
		
		if (modifiedDate != null) {
			// modifiedDate does not carry milliseconds, but fileDate does
			// therefore we have to do a range-based comparison
			if ((millis - modifiedDate.getTime()) < DateUtils.MILLIS_PER_SECOND) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * This is not repository specific, but we leave it close to the only caller
	 * 
	 * If the passed ref is newer than the modified date (or the modified date
	 * is null), an OK response with an inputstream pointing to the path is
	 * returned
	 */
	private static ResponseBuilder returnRefAsResponseBuilder(RepositoryFileReference ref, String modified) {
		if (!Repository.INSTANCE.exists(ref)) {
			return Response.status(Status.NOT_FOUND);
		}
		
		FileTime lastModified;
		try {
			lastModified = Repository.INSTANCE.getLastModifiedTime(ref);
		} catch (IOException e1) {
			BackendUtils.logger.debug("Could not get lastModifiedTime", e1);
			return Response.serverError();
		}
		
		// do we really need to send the file or can send "not modified"?
		if (!BackendUtils.isFileNewerThanModifiedDate(lastModified.toMillis(), modified)) {
			return Response.status(Status.NOT_MODIFIED);
		}
		
		ResponseBuilder res;
		try {
			res = Response.ok(Repository.INSTANCE.newInputStream(ref));
		} catch (IOException e) {
			BackendUtils.logger.debug("Could not open input stream", e);
			return Response.serverError();
		}
		res = res.lastModified(new Date(lastModified.toMillis()));
		// vary:accept header is always set to be safe
		res = res.header(HttpHeaders.VARY, HttpHeaders.ACCEPT);
		// determine and set MIME content type
		try {
			res = res.header(HttpHeaders.CONTENT_TYPE, Repository.INSTANCE.getMimeType(ref));
		} catch (IOException e) {
			BackendUtils.logger.debug("Could not determine mime type", e);
			return Response.serverError();
		}
		return res;
	}
	
	/**
	 * Updates the given property in the given configuration. Currently always
	 * returns "no content", because the underlying class does not report any
	 * errors during updating. <br />
	 * 
	 * If null or "" is passed as value, the property is cleared
	 * 
	 * @return Status.NO_CONTENT
	 */
	public static Response updateProperty(Configuration configuration, String property, String val) {
		if (StringUtils.isBlank(val)) {
			configuration.clearProperty(property);
		} else {
			configuration.setProperty(property, val);
		}
		return Response.noContent().build();
	}
	
	/**
	 * Persists the resource and returns appropriate response
	 */
	public static Response persist(IPersistable res) {
		Response r;
		try {
			res.persist();
		} catch (IOException e) {
			BackendUtils.logger.debug("Could not persist resource", e);
			r = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
			return r;
		}
		r = Response.noContent().build();
		return r;
	}
	
	/**
	 * Writes data to file. Replaces the file's content with the given content.
	 * The file does not need to exist
	 * 
	 * @param ref Reference to the File to write to (overwrite)
	 * @param content the data to write
	 * @return a JAX-RS Response containing the result. NOCONTENT if successful,
	 *         InternalSeverError otherwise
	 */
	public static Response putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) {
		try {
			Repository.INSTANCE.putContentToFile(ref, content, mediaType);
		} catch (IOException e) {
			BackendUtils.logger.error(e.getMessage(), e);
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.noContent().build();
	}
	
	public static Response putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) {
		try {
			Repository.INSTANCE.putContentToFile(ref, inputStream, mediaType);
		} catch (IOException e) {
			BackendUtils.logger.error(e.getMessage(), e);
			return Response.serverError().entity(e.getMessage()).build();
		}
		return Response.noContent().build();
	}
	
	public static <T extends TOSCAComponentId> T getTOSCAcomponentId(Class<T> idClass, String qnameStr) {
		QName qname = QName.valueOf(qnameStr);
		return BackendUtils.getTOSCAcomponentId(idClass, qname.getNamespaceURI(), qname.getLocalPart(), false);
	}
	
	public static <T extends TOSCAComponentId> T getTOSCAcomponentId(Class<T> idClass, QName qname) {
		// we got two implementation possibilities: one is to directly use the
		// QName constructor,
		// the other is to use a namespace, localname, urlencoded constructor
		// we opt for the latter one, which forces the latter constructor to
		// exist at all ids
		return BackendUtils.getTOSCAcomponentId(idClass, qname.getNamespaceURI(), qname.getLocalPart(), false);
	}
	
	public static <T extends TOSCAComponentId> T getTOSCAcomponentId(Class<T> idClass, String namespace, String id, boolean URLencoded) {
		Constructor<T> constructor;
		try {
			constructor = idClass.getConstructor(String.class, String.class, boolean.class);
		} catch (NoSuchMethodException | SecurityException e) {
			BackendUtils.logger.error("Could not get constructor for id " + idClass.getName(), e);
			throw new IllegalStateException(e);
		}
		T tcId;
		try {
			tcId = constructor.newInstance(namespace, id, URLencoded);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			BackendUtils.logger.error("Could not create id instance", e);
			throw new IllegalStateException(e);
		}
		return tcId;
	}
	
	/**
	 * @param id the id to determine the namespace of the parent for
	 * @return the namespace of the first TOSCAcomponentId found in the ID
	 *         hierarchy
	 */
	public static Namespace getNamespace(TOSCAElementId id) {
		GenericId parent = id.getParent();
		while (!(parent instanceof TOSCAComponentId)) {
			parent = parent.getParent();
		}
		return ((TOSCAComponentId) parent).getNamespace();
	}
	
	public static String getName(TOSCAComponentId instanceId) {
		// TODO: Here is a performance issue as we don't use caching or a database
		// Bad, but without performance loss: Use "text = instanceId.getXmlId().getDecoded();"
		TExtensibleElements instanceElement = AbstractComponentsResource.getComponentInstaceResource(instanceId).getElement();
		return ModelUtilities.getNameWithIdFallBack(instanceElement);
	}
	
/**
	 * Do <em>not</em> use this for creating URLs. Use
	 *
	 * {@link org.eclipse.winery.repository.Utils.getURLforPathInsideRepo(String)}
	 *
	 * or
	 *
	 * {@link org.eclipse.winery.repository.Utils.getAbsoluteURL(GenericId)
	 * instead.
	 *
	 * @return the path starting from the root element to the current element.
	 *         Separated by "/", URLencoded, but <b>not</b> double encoded. With
	 *         trailing slash if sub-resources can exist
	 * @throws IllegalStateException if id is of an unknown subclass of id
	 */
	public static String getPathInsideRepo(GenericId id) {
		if (id == null) {
			throw new NullPointerException("id is null");
		}
		
		// for creating paths see also org.eclipse.winery.repository.Utils.getIntermediateLocationStringForType(String, String)
		// and org.eclipse.winery.common.Util.getRootPathFragment(Class<? extends TOSCAcomponentId>)
		if (id instanceof AdminId) {
			return "admin/" + id.getXmlId().getEncoded() + "/";
		} else if (id instanceof GenericImportId) {
			GenericImportId i = (GenericImportId) id;
			String res = "imports/";
			res = res + Util.URLencode(i.getType()) + "/";
			res = res + i.getNamespace().getEncoded() + "/";
			res = res + i.getXmlId().getEncoded() + "/";
			return res;
		} else if (id instanceof TOSCAComponentId) {
			return IdUtil.getPathFragment(id);
		} else if (id instanceof TOSCAElementId) {
			// we cannot reuse IdUtil.getPathFragment(id) as this TOSCAelementId
			// might be nested in an AdminId
			return BackendUtils.getPathInsideRepo(id.getParent()) + id.getXmlId().getEncoded() + "/";
		} else {
			throw new IllegalStateException("Unknown subclass of GenericId " + id.getClass());
		}
	}
	
/**
	 * Do <em>not</em> use this for creating URLs. Use
	 *
	 * {@link org.eclipse.winery.repository.Utils.getURLforPathInsideRepo(String)}
	 *
	 * or
	 *
	 * {@link org.eclipse.winery.repository.Utils.getAbsoluteURL(GenericId)
	 * instead.
	 *
	 * @return the path starting from the root element to the current element.
	 *         Separated by "/", parent URLencoded. Without trailing slash.
	 */
	public static String getPathInsideRepo(RepositoryFileReference ref) {
		return BackendUtils.getPathInsideRepo(ref.getParent()) + ref.getFileName();
	}
	
	/**
	 * Returns the reference to the definitions XML storing the TOSCA for the
	 * given id
	 * 
	 * @param id the id to lookup
	 * @return the reference
	 */
	public static RepositoryFileReference getRefOfDefinitions(TOSCAComponentId id) {
		String name = Util.getTypeForComponentId(id.getClass());
		name = name + Constants.SUFFIX_TOSCA_DEFINITIONS;
		RepositoryFileReference ref = new RepositoryFileReference(id, name);
		return ref;
	}
	
	/**
	 * Returns the reference to the properties file storing the TOSCA
	 * information for the given id
	 * 
	 * @param id the id to lookup
	 * @return the reference
	 */
	public static RepositoryFileReference getRefOfConfiguration(GenericId id) {
		String name;
		// Hack to determine file name
		if (id instanceof TOSCAComponentId) {
			name = Util.getTypeForComponentId(((TOSCAComponentId) id).getClass());
			name = name + Constants.SUFFIX_PROPERTIES;
		} else if (id instanceof AdminId) {
			name = Utils.getTypeForAdminId(((AdminId) id).getClass());
			name = name + Constants.SUFFIX_PROPERTIES;
		} else {
			assert (id instanceof TOSCAElementId);
			TOSCAElementId tId = (TOSCAElementId) id;
			if (tId instanceof PlansId) {
				name = Filename.FILENAME_PROPERTIES_PLANCONTAINER;
			} else if (tId instanceof VisualAppearanceId) {
				// quick hack for special name here
				name = Filename.FILENAME_PROPERTIES_VISUALAPPEARANCE;
			} else {
				name = Util.getTypeForElementId(tId.getClass()) + Constants.SUFFIX_PROPERTIES;
			}
		}
		
		RepositoryFileReference ref = new RepositoryFileReference(id, name);
		return ref;
	}
	
	/**
	 * @param qNameOfTheType the QName of the type, where all TOSCAComponentIds,
	 *            where the associated element points to the type
	 * @param clazz the Id class of the entities to discover
	 */
	public static <X extends TOSCAComponentId> Collection<X> getAllElementsRelatedWithATypeAttribute(Class<X> clazz, QName qNameOfTheType) {
		// we do not use any database system,
		// therefore we have to crawl through each node type implementation by ourselves
		SortedSet<X> allIds = Repository.INSTANCE.getAllTOSCAComponentIds(clazz);
		Collection<X> res = new HashSet<>();
		for (X id : allIds) {
			IHasTypeReference resource;
			try {
				resource = (IHasTypeReference) AbstractComponentsResource.getComponentInstaceResource(id);
			} catch (ClassCastException e) {
				String error = "Requested following the type, but the component instance does not implmenet IHasTypeReference";
				BackendUtils.logger.error(error);
				throw new IllegalStateException(error);
			}
			// The resource may have been freshly initialized due to existence of a directory
			// then it has no node type assigned leading to ntiRes.getType() being null
			// we ignore this error here
			if (qNameOfTheType.equals(resource.getType())) {
				// the component instance is an implementation of the associated node type
				res.add(id);
			}
		}
		return res;
	}
	
	/**
	 * Creates a new TDefintions element wrapping a TOSCA Component instance.
	 * The namespace of the tosca component is used as namespace and
	 * {@code winery-defs-for-} concatenated with the (unique) ns prefix and
	 * idOfContainedElement is used as id
	 * 
	 * @param toscAcomponentId the id of the element the wrapper is used for
	 * 
	 * @return a definitions element prepared for wrapping a TOSCA component
	 *         instance
	 */
	public static Definitions createWrapperDefinitions(TOSCAComponentId tcId) {
		ObjectFactory of = new ObjectFactory();
		Definitions defs = of.createDefinitions();
		
		// set target namespace
		// an internal namespace is not possible
		//   a) tPolicyTemplate and tArtfactTemplate do NOT support the "targetNamespace" attribute
		//   b) the imports statement would look bad as it always imported the artificial namespace
		defs.setTargetNamespace(tcId.getNamespace().getDecoded());
		
		// set a unique id to create a valid definitions element
		// we do not use UUID to be more human readable and deterministic (for debugging)
		String prefix = NamespacesResource.getPrefix(tcId.getNamespace());
		String elId = tcId.getXmlId().getDecoded();
		String id = "winery-defs-for_" + prefix + "-" + elId;
		defs.setId(id);
		
		return defs;
	}
	
	/**
	 * @throws IOException if content could not be updated in the repository
	 * @throws IllegalStateException if an JAXBException occurred. This should
	 *             never happen.
	 */
	public static void persist(Object o, RepositoryFileReference ref, MediaType mediaType) throws IOException {
		// We assume that the object is not too large
		// Otherwise, http://io-tools.googlecode.com/svn/www/easystream/apidocs/index.html should be used
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Marshaller m;
		try {
			m = JAXBSupport.createMarshaller(true);
			m.marshal(o, out);
		} catch (JAXBException e) {
			BackendUtils.logger.error("Could not put content to file", e);
			throw new IllegalStateException(e);
		}
		byte[] data = out.toByteArray();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		// this may throw an IOExcpetion. We propagate this exception.
		Repository.INSTANCE.putContentToFile(ref, in, mediaType);
	}
	
	/**
	 * Updates the color if the color is not yet existent
	 * 
	 * @param name the name of the component. Used as basis for a generated
	 *            color
	 * @param qname the QName of the color attribute
	 * @param otherAttributes the plain "XML" attributes. They are used to check
	 * @param res
	 */
	public static String getColorAndSetDefaultIfNotExisting(String name, QName qname, Map<QName, String> otherAttributes, TopologyGraphElementEntityTypeResource res) {
		String colorStr = otherAttributes.get(qname);
		if (colorStr == null) {
			colorStr = Util.getColor(name);
			otherAttributes.put(qname, colorStr);
			BackendUtils.persist(res);
		}
		return colorStr;
	}
	
	/**
	 * 
	 * @param tcId The element type id to get the location for
	 * @param uri uri to use if in XML export mode, null if in CSAR export mode
	 * @param wrapperElementLocalName the local name of the wrapper element
	 * @return
	 */
	public static String getImportLocationForWinerysPropertiesDefinitionXSD(EntityTypeId tcId, URI uri, String wrapperElementLocalName) {
		String loc = BackendUtils.getPathInsideRepo(tcId);
		loc = loc + "propertiesdefinition/";
		loc = Utils.getURLforPathInsideRepo(loc);
		if (uri == null) {
			loc = loc + wrapperElementLocalName + ".xsd";
			// for the import later, we need "../" in front
			loc = "../" + loc;
		} else {
			loc = uri + loc + "xsd";
		}
		return loc;
	}
	
	/**
	 * @param ref the file to read from
	 */
	public static XSModel getXSModel(final RepositoryFileReference ref) {
		if (ref == null) {
			return null;
		}
		final InputStream is;
		try {
			is = Repository.INSTANCE.newInputStream(ref);
		} catch (IOException e) {
			BackendUtils.logger.debug("Could not create input stream", e);
			return null;
		}
		
		// we rely on xerces to parse the XSD
		// idea based on http://stackoverflow.com/a/5165177/873282
		XSImplementation impl = new XSImplementationImpl();
		XSLoader schemaLoader = impl.createXSLoader(null);
		
		// minimal LSInput implementation sufficient for XSLoader in Oracle's JRE7
		LSInput input = new LSInput() {
			
			@Override
			public void setSystemId(String systemId) {
			}
			
			@Override
			public void setStringData(String stringData) {
			}
			
			@Override
			public void setPublicId(String publicId) {
			}
			
			@Override
			public void setEncoding(String encoding) {
			}
			
			@Override
			public void setCharacterStream(Reader characterStream) {
			}
			
			@Override
			public void setCertifiedText(boolean certifiedText) {
			}
			
			@Override
			public void setByteStream(InputStream byteStream) {
			}
			
			@Override
			public void setBaseURI(String baseURI) {
			}
			
			@Override
			public String getSystemId() {
				return null;
			}
			
			@Override
			public String getStringData() {
				return null;
			}
			
			@Override
			public String getPublicId() {
				return BackendUtils.getPathInsideRepo(ref);
			}
			
			@Override
			public String getEncoding() {
				return "UTF-8";
			}
			
			@Override
			public Reader getCharacterStream() {
				try {
					return new InputStreamReader(is, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					System.out.println("exeption");
					throw new IllegalStateException("UTF-8 is unkown", e);
				}
			}
			
			@Override
			public boolean getCertifiedText() {
				return false;
			}
			
			@Override
			public InputStream getByteStream() {
				return null;
			}
			
			@Override
			public String getBaseURI() {
				return null;
			}
		};
		XSModel model = schemaLoader.load(input);
		return model;
	}
	
	/**
	 * Derives Winery's Properties Definition from an existing properties
	 * definition
	 * 
	 * @param ci the entity type to try to modify the WPDs
	 * @param errors the list to add errors to
	 */
	public static void deriveWPD(TEntityType ci, List<String> errors) {
		BackendUtils.logger.trace("deriveWPD");
		PropertiesDefinition propertiesDefinition = ci.getPropertiesDefinition();
		QName element = propertiesDefinition.getElement();
		if (element == null) {
			BackendUtils.logger.debug("only works for an element definition, not for types");
		} else {
			BackendUtils.logger.debug("Looking for the definition of {" + element.getNamespaceURI() + "}" + element.getLocalPart());
			// fetch the XSD defining the element
			XSDImportsResource importsRes = new XSDImportsResource();
			Map<String, RepositoryFileReference> mapFromLocalNameToXSD = importsRes.getMapFromLocalNameToXSD(element.getNamespaceURI(), false);
			RepositoryFileReference ref = mapFromLocalNameToXSD.get(element.getLocalPart());
			if (ref == null) {
				String msg = "XSD not found for " + element.getNamespaceURI() + " / " + element.getLocalPart();
				BackendUtils.logger.debug(msg);
				errors.add(msg);
				return;
			}
			
			XSModel xsModel = BackendUtils.getXSModel(ref);
			XSElementDeclaration elementDeclaration = xsModel.getElementDeclaration(element.getLocalPart(), element.getNamespaceURI());
			if (elementDeclaration == null) {
				String msg = "XSD model claimed to contain declaration for {" + element.getNamespaceURI() + "}" + element.getLocalPart() + ", but it did not.";
				BackendUtils.logger.debug(msg);
				errors.add(msg);
				return;
			}
			
			// go through the XSD definition and
			XSTypeDefinition typeDefinition = elementDeclaration.getTypeDefinition();
			if (typeDefinition instanceof XSComplexTypeDefinition) {
				XSComplexTypeDefinition cTypeDefinition = (XSComplexTypeDefinition) typeDefinition;
				XSParticle particle = cTypeDefinition.getParticle();
				if (particle == null) {
					BackendUtils.logger.debug("XSD does not follow the requirements put by winery: Complex type does not contain particles");
				} else {
					XSTerm term = particle.getTerm();
					if (term instanceof XSModelGroup) {
						XSModelGroup modelGroup = (XSModelGroup) term;
						if (modelGroup.getCompositor() == XSModelGroup.COMPOSITOR_SEQUENCE) {
							XSObjectList particles = modelGroup.getParticles();
							int len = particles.getLength();
							boolean everyThingIsASimpleType = true;
							PropertyDefinitionKVList list = new PropertyDefinitionKVList();
							if (len != 0) {
								for (int i = 0; i < len; i++) {
									XSParticle innerParticle = (XSParticle) particles.item(i);
									XSTerm innerTerm = innerParticle.getTerm();
									if (innerTerm instanceof XSElementDeclaration) {
										XSElementDeclaration innerElementDeclaration = (XSElementDeclaration) innerTerm;
										String name = innerElementDeclaration.getName();
										XSTypeDefinition innerTypeDefinition = innerElementDeclaration.getTypeDefinition();
										if (innerTypeDefinition instanceof XSSimpleType) {
											XSSimpleType xsSimpleType = (XSSimpleType) innerTypeDefinition;
											String typeNS = xsSimpleType.getNamespace();
											String typeName = xsSimpleType.getName();
											if (typeNS.equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
												PropertyDefinitionKV def = new PropertyDefinitionKV();
												def.setKey(name);
												// convention at WPD: use "xsd" as prefix for XML Schema Definition
												def.setType("xsd:" + typeName);
												list.add(def);
											} else {
												everyThingIsASimpleType = false;
												break;
											}
										} else {
											everyThingIsASimpleType = false;
											break;
										}
									} else {
										everyThingIsASimpleType = false;
										break;
									}
								}
							}
							if (everyThingIsASimpleType) {
								// everything went allright, we can add a WPD
								WinerysPropertiesDefinition wpd = new WinerysPropertiesDefinition();
								wpd.setIsDerivedFromXSD(Boolean.TRUE);
								wpd.setElementName(element.getLocalPart());
								wpd.setNamespace(element.getNamespaceURI());
								wpd.setPropertyDefinitionKVList(list);
								ModelUtilities.replaceWinerysPropertiesDefinition(ci, wpd);
								BackendUtils.logger.debug("Successfully generated WPD");
							} else {
								BackendUtils.logger.debug("XSD does not follow the requirements put by winery: Not all types in the sequence are simple types");
							}
						} else {
							BackendUtils.logger.debug("XSD does not follow the requirements put by winery: Model group is not a sequence");
						}
					} else {
						BackendUtils.logger.debug("XSD does not follow the requirements put by winery: Not a model group");
					}
				}
			} else {
				BackendUtils.logger.debug("XSD does not follow the requirements put by winery: No Complex Type Definition");
			}
		}
	}
	
	/**
	 * Returns all components available of the given id type
	 * 
	 * Similar functionality as {@link
	 * org.eclipse.winery.repository.backend.IGenericRepository.
	 * getAllTOSCAComponentIds(Class<T>)}, but it crawls through the repository
	 * 
	 * This method is required as we do not use a database.
	 * 
	 * @param idClass class of the Ids to search for
	 * @return empty set if no ids are available
	 */
	public <T extends TOSCAElementId> SortedSet<T> getAllTOSCAElementIds(Class<T> idClass) {
		throw new IllegalStateException("Not yet implemented");
		
		/*
		 Implementation idea:
		   * switch of instance of idClass
		   * nodetemplate / relationshiptemplate -> fetch all service templates -> crawl through topology -> add all to res
		   * req/cap do as above, but inspect nodetemplate
		   * (other special handlings; check spec where each type can be linked from)
		 */
	}
	
	/**
	 * Converts the given collection of TOSCA Component Ids to a collection of
	 * QNames by using the getQName() method.
	 * 
	 * This is required for QNameChooser.tag
	 */
	public static Collection<QName> convertTOSCAComponentIdCollectionToQNameCollection(Collection<? extends TOSCAComponentId> col) {
		Collection<QName> res = new ArrayList<>();
		for (TOSCAComponentId id : col) {
			res.add(id.getQName());
		}
		return res;
	}
	
}
