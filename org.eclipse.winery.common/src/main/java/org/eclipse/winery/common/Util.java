/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.IdUtil;
import org.eclipse.winery.common.ids.admin.AdminId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.EntityTemplateId;
import org.eclipse.winery.common.ids.definitions.EntityTypeId;
import org.eclipse.winery.common.ids.definitions.EntityTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.imports.GenericImportId;
import org.eclipse.winery.common.ids.definitions.imports.XSDImportId;
import org.eclipse.winery.common.ids.elements.ToscaElementId;
import org.eclipse.winery.model.tosca.TEntityType;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.constants.Namespaces;

import org.apache.commons.lang3.StringUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class Util {

	public static final String FORBIDDEN_CHARACTER_REPLACEMENT = "_";

	public static final String slashEncoded = Util.URLencode("/");

	private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

	/*
	* Valid chars: See
	* <ul>
	* <li>http://www.w3.org/TR/REC-xml-names/#NT-NCName</li>
	* <li>http://www.w3.org/TR/REC-xml/#NT-Name</li>
	* </ul>
	*/
	// NameCharRange \u10000-\ueffff is not supported by Java
	private static final String NCNameStartChar_RegExp = "[A-Z_a-z\u00c0-\u00d6\u00d8\u00f6\u00f8\u02ff\u0370\u037d\u037f-\u1fff\u200c-\u200d\u2070-\u218f\u2c00-\u2fef\u3001-\ud7ff\uf900-\ufdcf\ufdf0-\ufffd]";
	private static final String NCNameChar_RegExp = Util.NCNameStartChar_RegExp + "|[-\\.0-9\u00B7\u0300-\u036F\u203F-\u2040]";
	private static final Pattern NCNameStartChar_Pattern = Pattern.compile(Util.NCNameStartChar_RegExp);
	private static final Pattern NCNameChar_RegExp_Pattern = Pattern.compile(Util.NCNameChar_RegExp);

	public static String URLdecode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	public static String URLencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException();
		}
	}

	public static String DoubleURLencode(String s) {
		return Util.URLencode(Util.URLencode(s));
	}

	/**
	 * Encodes the namespace and the localname of the given qname, separated by "/"
	 *
	 * @return <double encoded namespace>"/"<double encoded localname>
	 */
	public static String DoubleURLencode(QName qname) {
		String ns = Util.DoubleURLencode(qname.getNamespaceURI());
		String localName = Util.DoubleURLencode(qname.getLocalPart());
		return ns + "/" + localName;
	}

	public static boolean isRelativeURI(String uri) {
		URI u;
		try {
			u = new URI(uri);
		} catch (URISyntaxException e) {
			Util.LOGGER.debug(e.getMessage(), e);
			// fallback
			return false;
		}
		return !u.isAbsolute();
	}

	/**
	 * Do <em>not</em> use this for creating URLs. Use {@link Util#getUrlPath(org.eclipse.winery.common.ids.GenericId)
	 * instead.
	 *
	 * @return the path starting from the root element to the current element. Separated by "/", URLencoded, but
	 * <b>not</b> double encoded. With trailing slash if sub-resources can exist
	 * @throws IllegalStateException if id is of an unknown subclass of id
	 */
	public static String getPathInsideRepo(GenericId id) {
		Objects.requireNonNull(id);

		// for creating paths see also org.eclipse.winery.repository.Utils.getIntermediateLocationStringForType(String, String)
		// and org.eclipse.winery.common.Util.getRootPathFragment(Class<? extends DefinitionsChildId>)
		if (id instanceof AdminId) {
			return "admin/" + id.getXmlId().getEncoded() + "/";
		} else if (id instanceof GenericImportId) {
			GenericImportId i = (GenericImportId) id;
			String res = "imports/";
			res = res + Util.URLencode(i.getType()) + "/";
			res = res + i.getNamespace().getEncoded() + "/";
			res = res + i.getXmlId().getEncoded() + "/";
			return res;
		} else if (id instanceof DefinitionsChildId) {
			return IdUtil.getPathFragment(id);
		} else if (id instanceof ToscaElementId) {
			// we cannot reuse IdUtil.getPathFragment(id) as this TOSCAelementId
			// might be nested in an AdminId
			return getPathInsideRepo(id.getParent()) + id.getXmlId().getEncoded() + "/";
		} else {
			throw new IllegalStateException("Unknown subclass of GenericId " + id.getClass());
		}
	}

	public static Class<? extends DefinitionsChildId> getComponentIdClassForTExtensibleElements(Class<? extends TExtensibleElements> clazz) {
		// we assume that the clazzName always starts with a T.
		// Therefore, we fetch everything after the last dot (plus offest 1)
		String idClassName = clazz.getName();
		int dotIndex = idClassName.lastIndexOf('.');
		assert (dotIndex >= 0);
		idClassName = idClassName.substring(dotIndex + 2) + "Id";

		return getComponentIdClass(idClassName);
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends DefinitionsChildId> getComponentIdClass(String idClassName) {
		String pkg = "org.eclipse.winery.common.ids.definitions.";
		if (idClassName.contains("Import")) {
			// quick hack to handle imports, which reside in their own package
			pkg = pkg + "imports.";
		}
		String fullClassName = pkg + idClassName;
		try {
			return (Class<? extends DefinitionsChildId>) Class.forName(fullClassName);
		} catch (ClassNotFoundException e) {
			// quick hack for Ids local to winery repository
			try {
				fullClassName = "org.eclipse.winery.repository.datatypes.ids.admin." + idClassName;
				return (Class<? extends DefinitionsChildId>) Class.forName(fullClassName);
			} catch (ClassNotFoundException e2) {
				String errorMsg = "Could not find id class for component container, " + fullClassName;
				LOGGER.error(errorMsg);
				throw new IllegalStateException(errorMsg);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends GenericId> getGenericIdClassForType(String typeIdType) {
		Class<? extends GenericId> res;
		// quick hack - we only need definitions right now
		String pkg = "org.eclipse.winery.common.ids.definitions.";
		String className = typeIdType;
		className = pkg + className;
		try {
			res = (Class<? extends GenericId>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Could not find id class for id type", e);
			res = null;
		}
		return res;
	}


	/**
	 * @param id the GenericId to determine the path for
	 * @return the path correctly URL encoded
	 */
	public static String getUrlPath(GenericId id) {
		String pathInsideRepo = getPathInsideRepo(id);
		// first encode the whole string
		String res = Util.URLencode(pathInsideRepo);
		// issue: "/" is also encoded. This has to be undone:
		res = res.replaceAll(slashEncoded, "/");
		return res;
	}

	public static String getUrlPath(String pathInsideRepo) {
		// first encode the whole string
		String res = Util.URLencode(pathInsideRepo);
		// issue: "/" is also encoded. This has to be undone:
		res = res.replaceAll(slashEncoded, "/");
		return res;
	}

	public static String getUrlPath(RepositoryFileReference ref) {
		return getUrlPath(ref.getParent()) + Util.URLencode(ref.getFileName());
	}

	/**
	 * @param c the element directly nested below a definitions element in XML
	 */
	public static String getURLpathFragmentForCollection(Class<? extends TExtensibleElements> c) {
		String res = c.getName().toLowerCase();
		int lastDot = res.lastIndexOf('.');
		// classname is something like <package>.T<type>. We are only interested
		// in "<type>". Therefore "+2" from the dot onwards
		res = res.substring(lastDot + 2);
		res = res + "s";
		return res;
	}

	public static String getEverythingBetweenTheLastDotAndBeforeId(Class<? extends GenericId> cls) {
		String res = cls.getName();
		// Everything between the last "." and before "Id" is the Type
		int dotIndex = res.lastIndexOf('.');
		assert (dotIndex >= 0);
		return res.substring(dotIndex + 1, res.length() - "Id".length());
	}

	public static String getTypeForElementId(Class<? extends ToscaElementId> idClass) {
		return Util.getEverythingBetweenTheLastDotAndBeforeId(idClass);
	}

	/**
	 * @return Singular type name for the given id. E.g., "ServiceTemplateId" gets "ServiceTemplate"
	 */
	public static String getTypeForComponentId(Class<? extends DefinitionsChildId> idClass) {
		return Util.getEverythingBetweenTheLastDotAndBeforeId(idClass);
	}

	/**
	 * Returns the root path fragment for the given AbstractComponentIntanceResource
	 *
	 * With trailing slash
	 *
	 * @return [ComponentName]s/
	 */
	public static String getRootPathFragment(Class<? extends DefinitionsChildId> idClass) {
		// quick handling of imports special case
		// in the package naming, all other component instances have a this intermediate location, but not in the URLs
		// The package handling is in {@link org.eclipse.winery.repository.Utils.getIntermediateLocationStringForType(String, String)}
		String res;
		if (GenericImportId.class.isAssignableFrom(idClass)) {
			// this fires if idClass is a sub class from ImportCollectionId
			// special treatment for imports
			res = "imports/";
			if (XSDImportId.class.isAssignableFrom(idClass)) {
				res = res + "http%3A%2F%2Fwww.w3.org%2F2001%2FXMLSchema/";
			} else {
				throw new IllegalStateException("Not possible to determine local storage for generic imports class");
			}
			// we have the complete root path fragment
			return res;
		} else {
			res = "";
		}
		res = res + Util.getTypeForComponentId(idClass);
		res = res.toLowerCase();
		res = res + "s";
		res = res + "/";
		return res;
	}

	/**
	 * Just calls @link{qname2href}
	 *
	 * Introduced because of JSP error "The method qname2href(String, Class<? extends TExtensibleElements>, QName) in
	 * the type Util is not applicable for the arguments (String, Class<TNodeType>, QName, String)"
	 */
	public static String qname2hrefWithName(String repositoryUiUrl, Class<? extends TExtensibleElements> element, QName qname, String name) {
		return Util.qname2href(repositoryUiUrl, element, qname, name);
	}

	/**
	 * @param repositoryUiUrl the URL to the repository UI
	 * @param element         the element directly nested below a definitions element in XML
	 * @param qname           the QName of the element
	 * @param name            (optional) if not null, the name to display as text in the reference. Default: localName
	 *                        of the QName
	 * @return an <code>a</code> HTML element pointing to the given id
	 */
	public static String qname2href(@NonNull String repositoryUiUrl, @NonNull Class<? extends TExtensibleElements> element, @Nullable QName qname, @Nullable String name) {
		if (StringUtils.isEmpty(Objects.requireNonNull(repositoryUiUrl))) {
			throw new IllegalArgumentException("Repository URL must not be empty.");
		}
		Objects.requireNonNull(element);

		if (qname == null) {
			return "(none)";
		}

		String absoluteURL = repositoryUiUrl + "/#/" + Util.getURLpathFragmentForCollection(element) + "/" + Util.DoubleURLencode(qname.getNamespaceURI()) + "/" + Util.DoubleURLencode(qname.getLocalPart());

		if (name == null) {
			// fallback if no name is given
			name = qname.getLocalPart();
		}
		// sanitize name
		name = Functions.escapeXml(name);

		return "<a target=\"_blank\" data-qname=\"" + qname + "\" href=\"" + absoluteURL + "\">" + name + "</a>";
	}

	/**
	 * @param repositoryUiUrl the URL to the repository
	 * @param element         the element directly nested below a definitions element in XML
	 * @param qname           the QName of the element
	 * @return an <code>a</code> HTML element pointing to the given id
	 */
	public static String qname2href(String repositoryUiUrl, Class<? extends TExtensibleElements> element, QName qname) {
		return Util.qname2href(repositoryUiUrl, element, qname, null);
	}

	/**
	 * Returns a visual rendering of minInstances
	 *
	 * @param minInstances the value to render
	 */
	public static String renderMinInstances(Integer minInstances) {
		if ((minInstances == null) || (minInstances == 1)) {
			// == null: default value: display nothing -- *never* happens:
			// the function *always* returns 1 even, if no explicit value is set. Therefore, we also display "" if the default value 1 is set
			return "";
		} else {
			return Integer.toString(minInstances);
		}
	}

	/**
	 * Returns a visual rendering of maxInstances
	 *
	 * @param maxInstances the value to render
	 */
	public static String renderMaxInstances(String maxInstances) {
		if ((maxInstances == null) || (maxInstances.equals("1"))) {
			// default value display nothing
			// "1" is returned even if no explicit value has been set.
			return "";
		} else if (maxInstances.equals("unbounded")) {
			return "&infin;";
		} else {
			// maxInstance is a plain integer
			// return as is
			return maxInstances;
		}
	}

	/**
	 * @return the local name of a Class representing a TOSCA element
	 */
	private static String getLocalName(@SuppressWarnings("rawtypes") Class clazz) {
		String localName = clazz.getName();
		// a class defined within another class is written as superclass$class. E.g., EntityTemplate$Properties
		// We use the real class name
		int pos = localName.lastIndexOf('$');
		if (pos == -1) {
			pos = localName.lastIndexOf('.');
		}
		localName = localName.substring(pos + 1);
		if (localName.equals("TDocumentation")) {
			// special case for documentation: the local name starts with a lower case letter
			localName = "documentation";
		} else if (localName.startsWith("T")) {
			localName = localName.substring(1);
		}
		return localName;
	}

	public static <T> JAXBElement<T> getJAXBElement(Class<T> clazz, T obj) {
		String namespace = null;
		XmlRootElement xmlRootElement = clazz.getAnnotation(XmlRootElement.class);
		if (xmlRootElement != null) {
			namespace = xmlRootElement.namespace();
			if ("##default".equals(namespace)) {
				XmlSchema xmlSchema = clazz.getPackage().getAnnotation(XmlSchema.class);
				if (xmlSchema != null) {
					namespace = xmlSchema.namespace();
				} else {
					// trigger default handling
					namespace = null;
				}
			}
		}
		if (namespace == null) {
			// fallback non-specified namespaces
			namespace = Namespaces.TOSCA_NAMESPACE;
		}
		String localName = Util.getLocalName(clazz);
		QName qname = new QName(namespace, localName);
		return new JAXBElement<T>(qname, clazz, obj);
	}

	/**
	 * Method similar to {@link org.eclipse.winery.repository.Utils#getXMLAsString(java.lang.Class, java.lang.Object,
	 * boolean)}.
	 *
	 * Differences: <ul> <li>XML processing instruction is not included in the header</li> <li>JAXBcontext is created at
	 * each call</li> </ul>
	 */
	public static <T> String getXMLAsString(Class<T> clazz, T obj) throws Exception {
		// copied from Utils java, but we create an own JAXBcontext here
		// JAXBSupport cannot be used as this relies on a MockElement, which we do not want to factor out to winery.common

		JAXBContext context;
		try {
			// For winery classes, eventually the package+jaxb.index method could be better. See http://stackoverflow.com/a/3628525/873282
			// @formatter:off
			context = JAXBContext.newInstance(
				TEntityType.class);
			// @formatter:on
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}

		JAXBElement<T> rootElement = Util.getJAXBElement(clazz, obj);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_FRAGMENT, true);
		// m.setProperty("com.sun.xml.bind.namespacePrefixMapper", JAXBSupport.prefixMapper);

		StringWriter w = new StringWriter();
		try {
			m.marshal(rootElement, w);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
		return w.toString();
	}

	public static String getXMLAsString(Element el) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t;
		try {
			t = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new IllegalStateException("Could not instantiate Transformer", e);
		}
		t.setOutputProperty(OutputKeys.INDENT, "yes");
		Source source = new DOMSource(el);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Result target = new StreamResult(os);
		try {
			t.transform(source, target);
		} catch (TransformerException e) {
			Util.LOGGER.debug(e.getMessage(), e);
			throw new IllegalStateException("Could not transform dom node to string", e);
		}
		return os.toString();
	}

	/**
	 * Determines whether the instance belonging to the given id supports the "name" attribute. This cannot be done
	 * using the super class as the TOSCA specification treats that differently in the case of EntityTemplates
	 *
	 * NOTE: The respective subclasses of AbstractComponentInstanceResource have to implement {@link
	 * org.eclipse.winery.repository.resources.IHasName}
	 *
	 * @param idClass the class of the to test
	 * @return true if the TOSCA model class belonging to the given id supports the method "getName()" in addition to
	 * "getId()"
	 */
	public static boolean instanceSupportsNameAttribute(Class<? extends DefinitionsChildId> idClass) {
		if (ServiceTemplateId.class.isAssignableFrom(idClass)) {
			return true;
		} else if ((EntityTypeId.class.isAssignableFrom(idClass)) || (EntityTypeImplementationId.class.isAssignableFrom(idClass))) {
			// name is available, but no id attribute
			return false;
		} else if (GenericImportId.class.isAssignableFrom(idClass)) {
			return false;
		} else {
			assert (EntityTemplateId.class.isAssignableFrom(idClass));
			if (ArtifactTemplateId.class.isAssignableFrom(idClass)) {
				return true;
			} else if (PolicyTemplateId.class.isAssignableFrom(idClass)) {
				return true;
			} else {
				throw new IllegalStateException("Unimplemented branch to determine if getName() exists");
			}
		}
	}

	public static String getLastURIPart(String loc) {
		int posSlash = loc.lastIndexOf('/');
		return loc.substring(posSlash + 1);
	}

	/**
	 * Determines the name of the CSS class used for relationshipTypes at nodeTemplateRenderer.tag
	 */
	public static String makeCSSName(String namespace, String localName) {
		// according to http://stackoverflow.com/a/79022/873282 everything is allowed
		// However, {namespace}id does NOT work
		String res = namespace + "_" + localName;
		res = res.replaceAll("[^\\w\\d_]", "_");
		return res;
	}

	/**
	 * @see {@link Util#makeCSSName(java.lang.String, java.lang.String)}
	 */
	public static String makeCSSName(QName qname) {
		return Util.makeCSSName(qname.getNamespaceURI(), qname.getLocalPart());
	}

	public static SortedMap<String, SortedSet<String>> convertQNameListToNamespaceToLocalNameList(List<QName> list) {
		SortedMap<String, SortedSet<String>> res = new TreeMap<>();
		for (QName qname : list) {
			SortedSet<String> localNameSet = res.computeIfAbsent(qname.getNamespaceURI(), k -> new TreeSet<>());
			localNameSet.add(qname.getLocalPart());
		}
		return res;
	}

	public static String namespaceToJavaPackage(String namespace) {
		URI uri;
		try {
			uri = new URI(namespace);
		} catch (URISyntaxException e) {
			Util.LOGGER.debug(e.getMessage(), e);
			return "uri.invalid";
		}
		StringBuilder sb = new StringBuilder();

		String host = uri.getHost();
		if (host != null) {
			Util.addReversed(sb, host, "\\.");
		}

		String path = uri.getPath();
		if (!path.equals("")) {
			if (path.startsWith("/")) {
				// remove first slash
				path = path.substring(1);
			}

			// and then handle the string
			Util.addAsIs(sb, path, "/");
		}

		// remove the final dot
		sb.replace(sb.length() - 1, sb.length(), "");

		return Util.cleanName(sb.toString());
	}

	private static String cleanName(String s) {
		// TODO: Integrate with other name cleaning functions. "." should not be replaced as it is used as separator in the java package name
		// @formatter:off
		return s.replace(":", Util.FORBIDDEN_CHARACTER_REPLACEMENT)
			.replace("/", Util.FORBIDDEN_CHARACTER_REPLACEMENT)
			.replace(" ", Util.FORBIDDEN_CHARACTER_REPLACEMENT)
			.replace("-", Util.FORBIDDEN_CHARACTER_REPLACEMENT);
		// @formatter:on
	}


	/**
	 * Removes all non-NCName characters from the given string and returns the result
	 *
	 * This function should be consistent with org.eclipse.winery.common.Util.cleanName(String)
	 *
	 * TODO: This method seems to be equal to {@link org.eclipse.winery.repository.Utils.createXMLidAsString(String)}.
	 * These methods should be merged.
	 */
	public static String makeNCName(String text) {
		if (StringUtils.isEmpty(text)) {
			return text;
		}

		StringBuilder res = new StringBuilder();

		// handle start
		String start = text.substring(0, 1);
		Matcher m = Util.NCNameStartChar_Pattern.matcher(start);
		if (m.matches()) {
			res.append(start);
		} else {
			// not a valid character
			res.append("_");
		}

		// handle remaining characters;
		for (int i = 1; i < text.length(); i++) {
			String s = text.substring(i, i + 1);
			m = Util.NCNameChar_RegExp_Pattern.matcher(s);
			if (m.matches()) {
				res.append(s);
			} else {
				// not a valid character
				res.append("_");
			}
		}

		return res.toString();
	}

	private static void addAsIs(StringBuilder sb, String s, String separator) {
		if (s.isEmpty()) {
			return;
		}
		String[] split = s.split(separator);
		for (String aSplit : split) {
			sb.append(aSplit);
			sb.append(".");
		}
	}

	private static void addReversed(StringBuilder sb, String s, String separator) {
		String[] split = s.split(separator);
		for (int i = split.length - 1; i >= 0; i--) {
			sb.append(split[i]);
			sb.append(".");
		}
	}

	/**
	 * Bridge to client.getType(). Just calls client getType(), used by functions.tld.
	 *
	 * We suppress compiler warnings as JSP 2.0 do not offer support for generics, but we're using JSP 2.0...
	 *
	 * @param client the repository client to use
	 * @param qname  the QName to resolve
	 * @param clazz  the class the QName is describing
	 * @return {@inheritDoc}
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static org.eclipse.winery.model.tosca.TEntityType getType(org.eclipse.winery.common.interfaces.IWineryRepository client, javax.xml.namespace.QName qname, java.lang.Class clazz) {
		return client.getType(qname, clazz);
	}
}
