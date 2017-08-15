/*******************************************************************************
 * Copyright (c) 2012-2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     Tino Stadelmaier, Philipp Meyer - rename id and/or namespace
 *     Lukas Harzentter - get namespaces for specific component
 *     Nicole Keppler - forceDelete for Namespaces
 *     Philipp Meyer - support for source directory
 *******************************************************************************/
package org.eclipse.winery.repository.backend.filebased;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.core.MediaType;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.Util;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.common.ids.Namespace;
import org.eclipse.winery.common.ids.XMLId;
import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.common.ids.elements.TOSCAElementId;
import org.eclipse.winery.model.tosca.Definitions;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.AbstractRepository;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.IRepositoryAdministration;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.backend.constants.MediaTypes;
import org.eclipse.winery.repository.exceptions.WineryRepositoryException;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResource;
import org.eclipse.winery.repository.resources.AbstractComponentsResource;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.dircache.InvalidPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * When it comes to a storage of plain files, we use Java 7's nio internally. Therefore, we intend to expose the stream
 * types offered by java.nio.Files: BufferedReader/BufferedWriter
 */
public class FilebasedRepository extends AbstractRepository implements IRepositoryAdministration {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilebasedRepository.class);

	protected final Path repositoryRoot;

	// convenience variables to have a clean code
	private final FileSystem fileSystem;

	private final FileSystemProvider provider;

	/**
	 * @param repositoryLocation a string pointing to a location on the file system. May be null.
	 */
	public FilebasedRepository(String repositoryLocation) {
		this.repositoryRoot = this.determineRepositoryPath(repositoryLocation);
		this.fileSystem = this.repositoryRoot.getFileSystem();
		this.provider = this.fileSystem.provider();
		LOGGER.debug("Repository root: {}", this.repositoryRoot);
	}

	private Path makeAbsolute(Path relativePath) {
		return this.repositoryRoot.resolve(relativePath);
	}

	@Override
	public boolean flagAsExisting(GenericId id) {
		Path path = this.id2AbsolutePath(id);
		try {
			FileUtils.createDirectory(path);
		} catch (IOException e) {
			FilebasedRepository.LOGGER.debug(e.toString());
			return false;
		}
		return true;
	}

	private Path id2AbsolutePath(GenericId id) {
		Path relativePath = this.fileSystem.getPath(BackendUtils.getPathInsideRepo(id));
		return this.makeAbsolute(relativePath);
	}

	/**
	 * Converts the given reference to an absolute path of the underlying FileSystem
	 */
	public Path ref2AbsolutePath(RepositoryFileReference ref) {
		return this.id2AbsolutePath(ref.getParent()).resolve(ref.getFileName());
	}

	protected Path determineRepositoryPath(String repositoryLocation) {
		Path repositoryPath;
		if (repositoryLocation == null) {
			if (SystemUtils.IS_OS_WINDOWS) {
				if (new File(Constants.GLOBAL_REPO_PATH_WINDOWS).exists()) {
					repositoryLocation = Constants.GLOBAL_REPO_PATH_WINDOWS;
					File repo = new File(repositoryLocation);
					try {
						org.apache.commons.io.FileUtils.forceMkdir(repo);
					} catch (IOException e) {
						FilebasedRepository.LOGGER.error("Could not create repository directory", e);
					}
					repositoryPath = repo.toPath();
				} else {
					repositoryPath = this.createDefaultRepositoryPath();
				}
			} else {
				repositoryPath = this.createDefaultRepositoryPath();
			}
		} else {
			File repo = new File(repositoryLocation);
			try {
				org.apache.commons.io.FileUtils.forceMkdir(repo);
			} catch (IOException e) {
				FilebasedRepository.LOGGER.error("Could not create repository directory", e);
			}
			repositoryPath = repo.toPath();
		}
		return repositoryPath;
	}

	public static File getDefaultRepositoryFilePath() {
		return new File(org.apache.commons.io.FileUtils.getUserDirectory(), Constants.DEFAULT_REPO_NAME);
	}

	private Path createDefaultRepositoryPath() {
		File repo = null;
		boolean operationalFileSystemAccess;
		try {
			repo = FilebasedRepository.getDefaultRepositoryFilePath();
			operationalFileSystemAccess = true;
		} catch (NullPointerException e) {
			// it seems, we run at a system, where we do not have any filesystem
			// access
			operationalFileSystemAccess = false;
		}

		// operationalFileSystemAccess = false;

		Path repositoryPath;
		if (operationalFileSystemAccess) {
			try {
				org.apache.commons.io.FileUtils.forceMkdir(repo);
			} catch (IOException e) {
				FilebasedRepository.LOGGER.error("Could not create directory", e);
			}
			repositoryPath = repo.toPath();
		} else {
			// we do not have access to the file system
			throw new IllegalStateException("No write access to file system");
		}

		return repositoryPath;
	}

	@Override
	public void forceDelete(RepositoryFileReference ref) throws IOException {
		Path relativePath = this.fileSystem.getPath(BackendUtils.getPathInsideRepo(ref));
		Path fileToDelete = this.makeAbsolute(relativePath);
		try {
			this.provider.delete(fileToDelete);
			// Quick hack for deletion of the mime type information
			// Alternative: superclass: protected void deleteMimeTypeInformation(RepositoryFileReference ref) throws IOException
			// However, this would again call this method, where we would have to check for the extension, too.
			// Therefore, we directly delete the information file
			Path mimeTypeFile = fileToDelete.getParent().resolve(ref.getFileName() + Constants.SUFFIX_MIMETYPE);
			this.provider.delete(mimeTypeFile);
		} catch (IOException e) {
			if (!(e instanceof NoSuchFileException)) {
				// only if file did exist and something else went wrong: complain :)
				// (otherwise, silently ignore the error)
				FilebasedRepository.LOGGER.debug("Could not delete file", e);
				throw e;
			}
		}
	}

	@Override
	public void forceDelete(GenericId id) {
		FileUtils.forceDelete(this.id2AbsolutePath(id));
	}

	@Override
	public void rename(TOSCAComponentId oldId, TOSCAComponentId newId) throws IOException {
		Objects.requireNonNull(oldId);
		Objects.requireNonNull(newId);

		if (oldId.equals(newId)) {
			// we do not do anything - even not throwing an error
			return;
		}

		AbstractComponentInstanceResource componentInstanceResource = AbstractComponentsResource.getComponentInstaceResource(oldId);
		//AbstractComponentInstanceResource newComponentInstanceResource = AbstractComponentsResource.getComponentInstaceResource(newId);

		RepositoryFileReference oldRef = BackendUtils.getRefOfDefinitions(oldId);
		RepositoryFileReference newRef = BackendUtils.getRefOfDefinitions(newId);

		// oldRef points to the definitions file,
		// getParent() returns the directory
		// we need toFile(), because we rely on FileUtils.moveDirectoryToDirectory
		File oldDir = this.id2AbsolutePath(oldRef.getParent()).toFile();
		File newDir = this.id2AbsolutePath(newRef.getParent()).toFile();

		org.apache.commons.io.FileUtils.moveDirectory(oldDir, newDir);

		// Update definitions and store it
		Definitions definitions = componentInstanceResource.getDefinitions();

		// This also updates the definitions of componentInstanceResource
		BackendUtils.updateWrapperDefinitions(newId, definitions);

		// This works, because the definitions object here is the same as the definitions object treated at copyIdToFields
		// newId has to be passed, because the id is final at AbstractComponentInstanceResource
		componentInstanceResource.copyIdToFields(newId);

		try {
			BackendUtils.persist(definitions, newRef, MediaTypes.MEDIATYPE_TOSCA_DEFINITIONS);
		} catch (InvalidPathException e) {
			// QUICK FIX
			// Somewhere, the first letter is deleted --> /odetypes/http%3A%2F%2Fwww.example.org%2F05/
			// We just ignore it for now
		}
	}

	public void forceDelete(Class<? extends TOSCAComponentId> toscaComponentIdClazz, Namespace namespace) {
		// instantiate new tosca component id with "ID" as id
		// this is used to get the absolute path
		TOSCAComponentId id = BackendUtils.getTOSCAcomponentId(toscaComponentIdClazz, namespace.getEncoded(), "ID", true);

		Path path = this.id2AbsolutePath(id);

		// do not delete the id, delete the complete namespace
		// patrent folder is the namespace folder
		path = path.getParent();
		FileUtils.forceDelete(path);
	}

	@Override
	public boolean exists(GenericId id) {
		Path absolutePath = this.id2AbsolutePath(id);
		return Files.exists(absolutePath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putContentToFile(RepositoryFileReference ref, String content, MediaType mediaType) throws IOException {
		if (mediaType == null) {
			// quick hack for storing mime type called this method
			assert (ref.getFileName().endsWith(Constants.SUFFIX_MIMETYPE));
			// we do not need to store the mime type of the file containing the mime type information
		} else {
			this.setMimeType(ref, mediaType);
		}
		Path path = this.ref2AbsolutePath(ref);
		FileUtils.createDirectory(path.getParent());
		Files.write(path, content.getBytes());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putContentToFile(RepositoryFileReference ref, InputStream inputStream, MediaType mediaType) throws IOException {
		if (mediaType == null) {
			// quick hack for storing mime type called this method
			assert (ref.getFileName().endsWith(Constants.SUFFIX_MIMETYPE));
			// we do not need to store the mime type of the file containing the mime type information
		} else {
			this.setMimeType(ref, mediaType);
		}
		Path targetPath = this.ref2AbsolutePath(ref);
		// ensure that parent directory exists
		FileUtils.createDirectory(targetPath.getParent());

		try {
			Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IllegalStateException e) {
			FilebasedRepository.LOGGER.debug("Guessing that stream with length 0 is to be written to a file", e);
			// copy throws an "java.lang.IllegalStateException: Stream already closed" if the InputStream contains 0 bytes
			// For instance, this case happens if SugarCE-6.4.2.zip.removed is tried to be uploaded
			// We work around the Java7 issue and create an empty file
			if (Files.exists(targetPath)) {
				// semantics of putContentToFile: existing content is replaced without notification
				Files.delete(targetPath);
			}
			Files.createFile(targetPath);
		}
	}

	@Override
	public boolean exists(RepositoryFileReference ref) {
		return Files.exists(this.ref2AbsolutePath(ref));
	}

	@Override
	public <T extends TOSCAComponentId> SortedSet<T> getAllTOSCAComponentIds(Class<T> idClass) {
		SortedSet<T> res = new TreeSet<>();
		String rootPathFragment = Util.getRootPathFragment(idClass);
		Path dir = this.repositoryRoot.resolve(rootPathFragment);
		if (!Files.exists(dir)) {
			// return empty list if no ids are available
			return res;
		}
		assert (Files.isDirectory(dir));

		final OnlyNonHiddenDirectories onhdf = new OnlyNonHiddenDirectories();

		// list all directories contained in this directory
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, onhdf)) {
			for (Path nsP : ds) {
				// the current path is the namespace
				Namespace ns = new Namespace(nsP.getFileName().toString(), true);
				try (DirectoryStream<Path> idDS = Files.newDirectoryStream(nsP, onhdf)) {
					for (Path idP : idDS) {
						XMLId xmlId = new XMLId(idP.getFileName().toString(), true);
						Constructor<T> constructor;
						try {
							constructor = idClass.getConstructor(Namespace.class, XMLId.class);
						} catch (Exception e) {
							FilebasedRepository.LOGGER.debug("Internal error at determining id constructor", e);
							// abort everything, return invalid result
							return res;
						}
						T id;
						try {
							id = constructor.newInstance(ns, xmlId);
						} catch (InstantiationException
								| IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException e) {
							FilebasedRepository.LOGGER.debug("Internal error at invocation of id constructor", e);
							// abort everything, return invalid result
							return res;
						}
						res.add(id);
					}
				}
			}
		} catch (IOException e) {
			FilebasedRepository.LOGGER.debug("Cannot close ds", e);
		}

		return res;
	}

	@Override
	public SortedSet<RepositoryFileReference> getContainedFiles(GenericId id) {
		Path dir = this.id2AbsolutePath(id);
		SortedSet<RepositoryFileReference> res = new TreeSet<>();
		if (!Files.exists(dir)) {
			return res;
		}
		assert (Files.isDirectory(dir));
		// list all directories contained in this directory
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, new OnlyNonHiddenFiles())) {
			for (Path p : ds) {
				RepositoryFileReference ref = new RepositoryFileReference(id, p.getFileName().toString());
				res.add(ref);
			}
		} catch (IOException e) {
			FilebasedRepository.LOGGER.debug("Cannot close ds", e);
		}
		return res;
	}

	@Override
	public Configuration getConfiguration(RepositoryFileReference ref) {
		Path path = this.ref2AbsolutePath(ref);

		PropertiesConfiguration configuration = new PropertiesConfiguration();
		if (Files.exists(path)) {
			try (Reader r = Files.newBufferedReader(path, Charset.defaultCharset())) {
				configuration.load(r);
			} catch (ConfigurationException | IOException e) {
				FilebasedRepository.LOGGER.error("Could not read config file", e);
				throw new IllegalStateException("Could not read config file", e);
			}
		}

		configuration.addConfigurationListener(new AutoSaveListener(path, configuration));

		// We do NOT implement reloading as the configuration is only accessed
		// in JAX-RS resources, which are created on a per-request basis

		return configuration;
	}

	/**
	 * @return null if an error occurred
	 */
	@Override
	public Date getLastUpdate(RepositoryFileReference ref) {
		Path path = this.ref2AbsolutePath(ref);
		Date res;
		if (Files.exists(path)) {
			FileTime lastModifiedTime;
			try {
				lastModifiedTime = Files.getLastModifiedTime(path);
				res = new Date(lastModifiedTime.toMillis());
			} catch (IOException e) {
				FilebasedRepository.LOGGER.debug(e.getMessage(), e);
				res = null;
			}
		} else {
			// this branch is taken if the resource directory exists, but the
			// configuration itself does not exist.
			// For instance, this happens if icons are manually put for a node
			// type, but no color configuration is made.
			res = Constants.LASTMODIFIEDDATE_FOR_404;
		}
		return res;
	}

	@Override
	public <T extends TOSCAElementId> SortedSet<T> getNestedIds(GenericId ref, Class<T> idClass) {
		Path dir = this.id2AbsolutePath(ref);
		SortedSet<T> res = new TreeSet<>();
		if (!Files.exists(dir)) {
			// the id has been generated by the exporter without existance test.
			// This test is done here.
			return res;
		}
		assert (Files.isDirectory(dir));
		// list all directories contained in this directory
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, new OnlyNonHiddenDirectories())) {
			for (Path p : ds) {
				XMLId xmlId = new XMLId(p.getFileName().toString(), true);
				@SuppressWarnings("unchecked")
				Constructor<T>[] constructors = (Constructor<T>[]) idClass.getConstructors();
				assert (constructors.length == 1);
				Constructor<T> constructor = constructors[0];
				assert (constructor.getParameterTypes().length == 2);
				T id;
				try {
					id = constructor.newInstance(ref, xmlId);
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					FilebasedRepository.LOGGER.debug("Internal error at invocation of id constructor", e);
					// abort everything, return invalid result
					return res;
				}
				res.add(id);
			}
		} catch (IOException e) {
			FilebasedRepository.LOGGER.debug("Cannot close ds", e);
		}
		return res;
	}

	@Override
	// below, toscaComponents is an array, which is used in an iterator
	// As Java does not allow generic arrays, we have to suppress the warning when fetching an element out of the list
	@SuppressWarnings("unchecked")
	public Collection<Namespace> getUsedNamespaces() {
		// @formatter:off
		@SuppressWarnings("rawtypes")
		Collection<Class<? extends TOSCAComponentId>> toscaComponentIds = Arrays.asList(
				ArtifactTemplateId.class,
				ArtifactTypeId.class,
				CapabilityTypeId.class,
				NodeTypeId.class,
				NodeTypeImplementationId.class,
				PolicyTemplateId.class,
				PolicyTypeId.class,
				RelationshipTypeId.class,
				RelationshipTypeImplementationId.class,
				RequirementTypeId.class,
				ServiceTemplateId.class
		);
		// @formatter:on

		return getNamespaces(toscaComponentIds);
	}

	@Override
	public Collection<Namespace> getComponentsNamespaces(Class<? extends TOSCAComponentId> clazz) {
		Collection<Class<? extends TOSCAComponentId>> list = new ArrayList<>();
		list.add(clazz);
		return getNamespaces(list);
	}

	private Collection<Namespace> getNamespaces(Collection<Class<? extends TOSCAComponentId>> toscaComponentIds) {
		// we use a HashSet to avoid reporting duplicate namespaces
		Collection<Namespace> res = new HashSet<>();

		for (Class<? extends TOSCAComponentId> id : toscaComponentIds) {
			String rootPathFragment = Util.getRootPathFragment(id);
			Path dir = this.repositoryRoot.resolve(rootPathFragment);
			if (!Files.exists(dir)) {
				continue;
			}
			assert (Files.isDirectory(dir));

			final OnlyNonHiddenDirectories onhdf = new OnlyNonHiddenDirectories();

			// list all directories contained in this directory
			try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, onhdf)) {
				for (Path nsP : ds) {
					// the current path is the namespace
					Namespace ns = new Namespace(nsP.getFileName().toString(), true);
					res.add(ns);
				}
			} catch (IOException e) {
				FilebasedRepository.LOGGER.debug("Cannot close ds", e);
			}
		}
		return res;
	}

	@Override
	public void doDump(OutputStream out) throws IOException {
		final ZipOutputStream zout = new ZipOutputStream(out);
		final int cutLength = this.repositoryRoot.toString().length() + 1;

		Files.walkFileTree(this.repositoryRoot, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				} else {
					return FileVisitResult.CONTINUE;
				}
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				String name = file.toString().substring(cutLength);
				ZipEntry ze = new ZipEntry(name);
				try {
					ze.setTime(Files.getLastModifiedTime(file).toMillis());
					ze.setSize(Files.size(file));
					zout.putNextEntry(ze);
					Files.copy(file, zout);
					zout.closeEntry();
				} catch (IOException e) {
					FilebasedRepository.LOGGER.debug(e.getMessage());
				}
				return FileVisitResult.CONTINUE;
			}
		});
		zout.close();
	}

	/**
	 * Removes all files and dirs except the .git directory
	 */
	@Override
	public void doClear() {
		try {
			DirectoryStream.Filter<Path> noGitDirFilter = entry -> !(entry.getFileName().toString().equals(".git"));

			DirectoryStream<Path> ds = Files.newDirectoryStream(this.repositoryRoot, noGitDirFilter);
			for (Path p : ds) {
				FileUtils.forceDelete(p);
			}
		} catch (IOException e) {
			FilebasedRepository.LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void doImport(InputStream in) {
		ZipInputStream zis = new ZipInputStream(in);
		ZipEntry entry;
		try {
			while ((entry = zis.getNextEntry()) != null) {
				if (!entry.isDirectory()) {
					Path path = this.repositoryRoot.resolve(entry.getName());
					FileUtils.createDirectory(path.getParent());
					Files.copy(zis, path);
				}
			}
		} catch (IOException e) {
			FilebasedRepository.LOGGER.error(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getSize(RepositoryFileReference ref) throws IOException {
		return Files.size(this.ref2AbsolutePath(ref));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileTime getLastModifiedTime(RepositoryFileReference ref) throws IOException {
		Path path = this.ref2AbsolutePath(ref);
		return Files.getLastModifiedTime(path);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream newInputStream(RepositoryFileReference ref) throws IOException {
		Path path = this.ref2AbsolutePath(ref);
		return Files.newInputStream(path);
	}

	@Override
	public void getZippedContents(final GenericId id, OutputStream out) throws WineryRepositoryException {
		Objects.requireNonNull(id);
		Objects.requireNonNull(out);

		SortedSet<RepositoryFileReference> containedFiles = this.getContainedFiles(id);

		try (final ArchiveOutputStream zos = new ArchiveStreamFactory().createArchiveOutputStream("zip", out)) {
			for (RepositoryFileReference ref : containedFiles) {
				zos.putArchiveEntry(new ZipArchiveEntry(ref.getFileName()));
				try (InputStream is = Repository.INSTANCE.newInputStream(ref)) {
					IOUtils.copy(is, zos);
				}
				zos.closeArchiveEntry();
			}
		} catch (ArchiveException e) {
			throw new WineryRepositoryException("Internal error while generating archive", e);
		} catch (IOException e) {
			throw new WineryRepositoryException("I/O exception during export", e);
		}
	}
}
