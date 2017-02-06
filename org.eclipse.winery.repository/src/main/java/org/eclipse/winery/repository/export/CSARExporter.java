/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Kálmán Képes - initial API and implementation and/or initial documentation
 *     Oliver Kopp - adapted to new storage model and to TOSCA v1.0, maintenance
 *******************************************************************************/
package org.eclipse.winery.repository.export;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.constants.MimeTypes;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.model.selfservice.Application;
import org.eclipse.winery.model.selfservice.Application.Options;
import org.eclipse.winery.model.selfservice.ApplicationOption;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.Prefs;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.ids.admin.NamespacesId;
import org.eclipse.winery.repository.datatypes.ids.elements.SelfServiceMetaDataId;
import org.eclipse.winery.repository.resources.admin.NamespacesResource;
import org.eclipse.winery.repository.resources.servicetemplates.ServiceTemplateResource;
import org.eclipse.winery.repository.resources.servicetemplates.selfserviceportal.SelfServicePortalResource;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * This class exports a CSAR crawling from the the given GenericId<br/>
 * Currently, only ServiceTemplates are supported<br />
 * commons-compress is used as an output stream should be provided. An
 * alternative implementation is to use Java7's Zip File System Provider
 */
public class CSARExporter {

	public static final String PATH_TO_NAMESPACES_PROPERTIES = "winery/Namespaces.properties";

	private static final Logger LOGGER = LoggerFactory.getLogger(CSARExporter.class);

	private static final String DEFINITONS_PATH_PREFIX = "Definitions/";


	/**
	 * Returns a unique name for the given definitions to be used as filename
	 */
	private static String getDefinitionsName(TOSCAComponentId id) {
		// the prefix is globally unique and the id locally in a namespace
		// therefore a concatenation of both is also unique
		return NamespacesResource.getPrefix(id.getNamespace()) + "__" + id.getXmlId().getEncoded();
	}

	public static String getDefinitionsFileName(TOSCAComponentId id) {
		return CSARExporter.getDefinitionsName(id) + Constants.SUFFIX_TOSCA_DEFINITIONS;
	}

	private static String getDefinitionsPathInsideCSAR(TOSCAComponentId id) {
		return CSARExporter.DEFINITONS_PATH_PREFIX + CSARExporter.getDefinitionsFileName(id);
	}

	/**
	 * Writes a complete CSAR containing all necessary things reachable from the
	 * given service template
	 *
	 * @param entryId the id of the service template to export
	 * @param out the outputstream to write to
	 * @throws JAXBException
	 */
	public void writeCSAR(TOSCAComponentId entryId, OutputStream out) throws ArchiveException, IOException, JAXBException {
		CSARExporter.LOGGER.trace("Starting CSAR export with {}", entryId.toString());

		Map<RepositoryFileReference, String> refMap = new HashMap<>();
		Collection<String> definitionNames = new ArrayList<>();

		final ArchiveOutputStream zos = new ArchiveStreamFactory().createArchiveOutputStream("zip", out);

		TOSCAExportUtil exporter = new TOSCAExportUtil();
		Map<String, Object> conf = new HashMap<>();

		ExportedState exportedState = new ExportedState();

		TOSCAComponentId currentId = entryId;
		do {
			String defName = CSARExporter.getDefinitionsPathInsideCSAR(currentId);
			definitionNames.add(defName);

			zos.putArchiveEntry(new ZipArchiveEntry(defName));
			Collection<TOSCAComponentId> referencedIds;
			try {
				referencedIds = exporter.exportTOSCA(currentId, zos, refMap, conf);
			} catch (IllegalStateException e) {
				// thrown if something went wrong inside the repo
				out.close();
				// we just rethrow as there currently is no error stream.
				throw e;
			}
			zos.closeArchiveEntry();

			exportedState.flagAsExported(currentId);
			exportedState.flagAsExportRequired(referencedIds);

			currentId = exportedState.pop();
		} while (currentId != null);

		// if we export a ServiceTemplate, data for the self-service portal might exist
		if (entryId instanceof ServiceTemplateId) {
			ServiceTemplateId serviceTemplateId = (ServiceTemplateId) entryId;
			this.addSelfServiceMetaData(serviceTemplateId, refMap, zos);
		}

		// now, refMap contains all files to be added to the CSAR

		// write manifest directly after the definitions to have it more at the beginning of the ZIP rather than having it at the very end
		this.addManifest(entryId, definitionNames, refMap, zos);

		// used for generated XSD schemas
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();
		} catch (TransformerConfigurationException e1) {
			CSARExporter.LOGGER.debug(e1.getMessage(), e1);
			throw new IllegalStateException("Could not instantiate transformer", e1);
		}

		// write all referenced files
		for (RepositoryFileReference ref : refMap.keySet()) {
			String archivePath = refMap.get(ref);
			CSARExporter.LOGGER.trace("Creating {}", archivePath);
			ArchiveEntry archiveEntry = new ZipArchiveEntry(archivePath);
			zos.putArchiveEntry(archiveEntry);
			if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
				CSARExporter.LOGGER.trace("Special treatment for generated XSDs");
				Document document = ((DummyRepositoryFileReferenceForGeneratedXSD) ref).getDocument();
				DOMSource source = new DOMSource(document);
				StreamResult result = new StreamResult(zos);
				try {
					transformer.transform(source, result);
				} catch (TransformerException e) {
					CSARExporter.LOGGER.debug("Could not serialize generated xsd", e);
				}
			} else {
				try (InputStream is = Repository.INSTANCE.newInputStream(ref)) {
					IOUtils.copy(is, zos);
				} catch (Exception e) {
					CSARExporter.LOGGER.error("Could not copy file content to ZIP outputstream", e);
				}
			}
			zos.closeArchiveEntry();
		}

		this.addNamespacePrefixes(zos);

		zos.finish();
		zos.close();
	}

	/**
	 * Writes the configured mapping namespaceprefix -> namespace to the archive
	 *
	 * This is kind of a quick hack. TODO: during the import, the prefixes
	 * should be extracted using JAXB and stored in the NamespacesResource
	 *
	 * @throws IOException
	 */
	private void addNamespacePrefixes(ArchiveOutputStream zos) throws IOException {
		Configuration configuration = Repository.INSTANCE.getConfiguration(new NamespacesId());
		if (configuration instanceof PropertiesConfiguration) {
			// Quick hack: direct serialization only works for PropertiesConfiguration
			PropertiesConfiguration pconf = (PropertiesConfiguration) configuration;
			ArchiveEntry archiveEntry = new ZipArchiveEntry(CSARExporter.PATH_TO_NAMESPACES_PROPERTIES);
			zos.putArchiveEntry(archiveEntry);
			try {
				pconf.save(zos);
			} catch (ConfigurationException e) {
				CSARExporter.LOGGER.debug(e.getMessage(), e);
				zos.write("#Could not export properties".getBytes());
				zos.write(("#" + e.getMessage()).getBytes());
			}
			zos.closeArchiveEntry();
		}
	}

	/**
	 * Adds all self service meta data to the targetDir
	 *
	 * @param targetDir the directory in the CSAR where to put the content to
	 * @param refMap is used later to create the CSAR
	 */
	private void addSelfServiceMetaData(ServiceTemplateId entryId, String targetDir, Map<RepositoryFileReference, String> refMap) {
		SelfServicePortalResource res = new SelfServicePortalResource(entryId);

		// This method is also called if the directory SELFSERVICE-Metadata exists without content and even if the directory does not exist at all,
		// but the ServiceTemplate itself exists.
		// The current assumption is that this is enough for an existence.
		// Thus, we have to take care of the case of an empty directory and add a default data.xml
		res.ensureDataXmlExists();

		refMap.put(res.data_xml_ref, targetDir + "data.xml");

		// The schema says that the images have to exist
		// However, at a quick modeling, there might be no images
		// Therefore, we check for existence
		if (Repository.INSTANCE.exists(res.icon_jpg_ref)) {
			refMap.put(res.icon_jpg_ref, targetDir + "icon.jpg");
		}
		if (Repository.INSTANCE.exists(res.image_jpg_ref)) {
			refMap.put(res.image_jpg_ref, targetDir + "image.jpg");
		}

		Application application = res.getApplication();

		// hack for the OpenTOSCA container to contain the CSAR name also in data.json
		application.setCsarName(entryId.getXmlId().getDecoded() + ".csar");

		// hack for the OpenTOSCA container to display something
		application.setVersion("1.0");
		List<String> authors = application.getAuthors();
		if (authors.isEmpty()) {
			authors.add("Winery");
		}

		// make the patches to data.xml permanent
		try {
			res.persist();
		} catch (IOException e) {
			LOGGER.error("Could not persist patches to data.xml", e);
		}

		Options options = application.getOptions();
		if (options != null) {
			SelfServiceMetaDataId id = new SelfServiceMetaDataId(entryId);
			for (ApplicationOption option : options.getOption()) {
				String url = option.getIconUrl();
				if (Util.isRelativeURI(url)) {
					putRefIntoRefMap(targetDir, refMap, id, url);
				}
				url = option.getPlanInputMessageUrl();
				if (Util.isRelativeURI(url)) {
					putRefIntoRefMap(targetDir, refMap, id, url);
				}
			}
		}
	}

	private void putRefIntoRefMap(String targetDir, Map<RepositoryFileReference, String> refMap, SelfServiceMetaDataId id, String url) {
		RepositoryFileReference ref = new RepositoryFileReference(id, url);
		if (Repository.INSTANCE.exists(ref)) {
            refMap.put(ref, targetDir + url);
        } else {
            CSARExporter.LOGGER.error("Data corrupt: pointing to non-existent file " + ref);
        }
	}

	private void addSelfServiceMetaData(ServiceTemplateId serviceTemplateId, Map<RepositoryFileReference, String> refMap, ArchiveOutputStream zos) throws IOException {
		SelfServiceMetaDataId id = new SelfServiceMetaDataId(serviceTemplateId);
		// We add the selfservice information regardless of the existance. - i.e., no "if (Repository.INSTANCE.exists(id)) {"
		// This ensures that the name of the application is
		// add everything in the root of the CSAR
		String targetDir = Constants.DIRNAME_SELF_SERVICE_METADATA + "/";
		addSelfServiceMetaData(serviceTemplateId, targetDir, refMap);
		this.addSelfServiceMetaDataAsJSON(serviceTemplateId, zos);
	}

	private void addSelfServiceMetaDataAsJSON(ServiceTemplateId serviceTemplateId, ArchiveOutputStream zos) throws IOException {
		ArchiveEntry archiveEntry = new ZipArchiveEntry(Constants.DIRNAME_SELF_SERVICE_METADATA + "/data.json");
		zos.putArchiveEntry(archiveEntry);
		ServiceTemplateResource serviceTemplateResource = new ServiceTemplateResource(serviceTemplateId);
		Application application = serviceTemplateResource.getSelfServicePortalResource().getApplication();
		ObjectMapper om = new ObjectMapper();
		// using om.writeValue(zos, application) causes trouble with the zos, so we write it into a byte array first
		byte[] bytes = om.writeValueAsBytes(application);
		zos.write(bytes);
		zos.closeArchiveEntry();
	}

	private void addManifest(TOSCAComponentId id, Collection<String> definitionNames, Map<RepositoryFileReference, String> refMap, ArchiveOutputStream out) throws IOException {
		String entryDefinitionsReference = CSARExporter.getDefinitionsPathInsideCSAR(id);

		out.putArchiveEntry(new ZipArchiveEntry("TOSCA-Metadata/TOSCA.meta"));
		PrintWriter pw = new PrintWriter(out);
		// Setting Versions
		pw.println("TOSCA-Meta-Version: 1.0");
		pw.println("CSAR-Version: 1.0");
		String versionString = "Created-By: Winery " + Prefs.INSTANCE.getVersion();
		pw.println(versionString);
		// Winery currently is unaware of tDefinitions, therefore, we use the
		// name of the service template
		pw.println("Entry-Definitions: " + entryDefinitionsReference);
		pw.println();

		assert (definitionNames.contains(entryDefinitionsReference));
		for (String name : definitionNames) {
			pw.println("Name: " + name);
			pw.println("Content-Type: " + org.eclipse.winery.common.constants.MimeTypes.MIMETYPE_TOSCA_DEFINITIONS);
			pw.println();
		}

		// Setting other files, mainly files belonging to artifacts
		for (RepositoryFileReference ref : refMap.keySet()) {
			String archivePath = refMap.get(ref);
			pw.println("Name: " + archivePath);
			String mimeType;
			if (ref instanceof DummyRepositoryFileReferenceForGeneratedXSD) {
				mimeType = MimeTypes.MIMETYPE_XSD;
			} else {
				mimeType = Repository.INSTANCE.getMimeType(ref);
			}
			pw.println("Content-Type: " + mimeType);
			pw.println();
		}
		pw.flush();
		out.closeArchiveEntry();
	}
}
