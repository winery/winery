/*******************************************************************************
 * Copyright (c) 2012-2014 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *     C. Timurhan Sungur - jClouds preferences
 *******************************************************************************/
package org.eclipse.winery.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.winery.common.TOSCADocumentBuilderFactory;
import org.eclipse.winery.repository.backend.IRepository;
import org.eclipse.winery.repository.backend.filebased.FilebasedRepository;
import org.eclipse.winery.repository.backend.filebased.GitBasedRepository;
import org.eclipse.winery.repository.runtimeintegration.OpenTOSCAContainerConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Prefs implements ServletContextListener {
	
	// set by the constructors
	// We have to do this hack as the servlet container initializes this class
	// on its own and we want to have a *single* instance of this class.
	public static Prefs INSTANCE;
	
	private static final Logger logger = LoggerFactory.getLogger(Prefs.class);
	
	protected IRepository repository = null;
	
	private ServletContext context;
	
	private Boolean isContainerLocallyAvailable = null;
	
	private Boolean isRestDocDocumentationAvailable = null;
	
	private Boolean isPlanBuilderAvailable = null;
	
	// location of the winery topology modeler
	private String wineryTopologyModelerPath = null;
	
	// the properties from winery.properties
	protected Properties properties = null;
	
	// package visibility to ease testing
	static final String PROP_JCLOUDS_CONTEXT_PROVIDER = "jclouds.context.provider";
	static final String PROP_JCLOUDS_CONTEXT_IDENTITY = "jclouds.context.identity";
	static final String PROP_JCLOUDS_CONTEXT_CREDENTIAL = "jclouds.context.credential";
	static final String PROP_JCLOUDS_BLOBSTORE_LOCATION = "jclouds.blobstore.location";
	static final String PROP_JCLOUDS_CONTAINERNAME = "jclouds.blobstore.container";
	static final String PROP_JCLOUDS_END_POINT = "jclouds.blobstore.endpoint";
	
	static final String PROP_BPMN4TOSCA_MODELER_URI = "bpmn4toscamodelerBaseURI";
	
	
	/**
	 * This constructor is called at handling at servlets, too. Therefore, we
	 * make it private. If testing is needed, an additional paramater has to be
	 * passed
	 */
	public Prefs() {
		Prefs.INSTANCE = this;
	}
	
	/**
	 * Constructor for Unit testing ONLY!
	 * 
	 * @param initializeRepository true if the repository should be initialized
	 *            as provided in winery.properties
	 * @throws IOException
	 * @warning Do not call! (except from Unit testing code)
	 */
	protected Prefs(boolean initializeRepository) throws IOException {
		this();
		
		// emulate behavior of doInitialization(Context)
		Properties p = new Properties();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("winery.properties");
		if (is != null) {
			p.load(is);
		}
		this.properties = p;
		
		if (initializeRepository) {
			this.doRepositoryInitialization();
		}
	}
	
	/**
	 * Initialization code for the repository. Should go into separate class,
	 * but being here should be OK for a prototype
	 * 
	 * Called from both the constructor for JUnit and the servlet-based
	 * initialization
	 * 
	 * Pre-Condition: this.properties is set.
	 */
	private void doRepositoryInitialization() {
		assert (this.properties != null);
		
		String provider = this.properties.getProperty(Prefs.PROP_JCLOUDS_CONTEXT_PROVIDER);
		if (provider != null) {
			// repository runs via jclouds
			// String identity = this.properties.getProperty(Prefs.PROP_JCLOUDS_CONTEXT_IDENTITY);
			// String credential = this.properties.getProperty(Prefs.PROP_JCLOUDS_CONTEXT_CREDENTIAL);
			// String location = this.properties.getProperty(Prefs.PROP_JCLOUDS_BLOBSTORE_LOCATION);
			// String containerName = this.properties.getProperty(Prefs.PROP_JCLOUDS_CONTAINERNAME);
			// String endPoint = this.properties.getProperty(Prefs.PROP_JCLOUDS_END_POINT);
			Prefs.logger.error("jClouds is currently not supported due to jClouds not yet approved by Eclipse. Falling back to local storages");
			provider = null;
			// Prefs.logger.info("Using jclouds as interface to the repository");
			// this.repository = new JCloudsBasedRepository(provider, identity, credential, location, containerName, endPoint);
		} // else {
		if (provider == null) {
			String repositoryLocation = this.properties.getProperty("repositoryPath");
			Prefs.logger.debug("Repository location: {}", repositoryLocation);
			Prefs.logger.debug("Trying git-based backend");
			try {
				this.repository = new GitBasedRepository(repositoryLocation);
				Prefs.logger.debug("git-based backend is used");
			} catch (Throwable e) {
				Prefs.logger.trace(e.getMessage());
				Prefs.logger.debug("There seems to be no git repository at the specified location. We fall back to the file-based repository");
				this.repository = new FilebasedRepository(repositoryLocation);
			}
		}
	}
	
	private void doInitialization(ServletContext ctx) {
		if (Locale.getDefault() != Locale.ENGLISH) {
			try {
				// needed for {@link
				// org.eclipse.winery.repository.filesystem.Utils.returnFile(File,
				// String)}
				Locale.setDefault(Locale.ENGLISH);
			} catch (AccessControlException e) {
				// Happens at Google App Engine
				Prefs.logger.error("Could not switch locale to English", e);
			}
		}
		
		this.context = ctx;
		
		// Reading //
		final String fn = "/WEB-INF/classes/winery.properties";
		Prefs.logger.debug("Trying to read ".concat(ctx.getRealPath(fn)));
		InputStream inStream = ctx.getResourceAsStream(fn);
		// alternative: InputStream inStream = this.getClass().getClassLoader().getResourceAsStream("winery.properties");
		Properties p = new Properties();
		if (inStream == null) {
			Prefs.logger.info(fn + " does not exist.");
			
			// We search for winery.properties on the filesystem in the repository
			
			File propFile = new File(FilebasedRepository.getDefaultRepositoryFilePath(), "winery.properties");
			Prefs.logger.info("Trying " + propFile.getAbsolutePath());
			if (propFile.exists()) {
				Prefs.logger.info("Found");
				// if winery.property exists in the root of the default repository path (~/winery-repository), load it
				try (InputStream is2 = new FileInputStream(propFile)) {
					p.load(is2);
				} catch (IOException e) {
					Prefs.logger.error("Could not load winery.properties", e);
				}
			} else {
				Prefs.logger.info("Not found");
			}
		} else {
			try {
				p.load(inStream);
				try {
					inStream.close();
				} catch (IOException e) {
					Prefs.logger.error("Could not close stream of winery.properties", e);
				}
			} catch (FileNotFoundException e) {
				// OK if file does not exist
			} catch (IOException e) {
				Prefs.logger.error("Could not load winery.properties", e);
			}
		}
		
		this.wineryTopologyModelerPath = p.getProperty("topologymodeler");
		
		// make the properties known in the class
		this.properties = p;
		
		this.doRepositoryInitialization();
		
		// Initialize XSD validation in the background. Takes up a few seconds.
		// If we do not do it here, the first save by a user takes a few seconds, which is inconvenient
		new Thread() {
			
			@Override
			public void run() {
				Prefs.logger.debug("Initializing XML validation");
				@SuppressWarnings("unused")
				TOSCADocumentBuilderFactory tdbf = TOSCADocumentBuilderFactory.INSTANCE;
				Prefs.logger.debug("Initialized XML validation");
			}
		}.start();
	}
	
	public IRepository getRepository() {
		return this.repository;
	}
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		Prefs.INSTANCE.doInitialization(arg0.getServletContext());
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// nothing to do at tear down
	}
	
	/**
	 * @return the path of the root resource
	 */
	public String getResourcePath() {
		return this.context.getContextPath();
	}
	
	/**
	 * @return the path to the winery topology modeler. Without trailing slash
	 */
	public String getWineryTopologyModelerPath() {
		if (this.wineryTopologyModelerPath == null) {
			// derive the path from the current path
			String res = this.getResourcePath();
			if (res.endsWith("/")) {
				res = res.substring(0, res.length() - 1);
			}
			int pos = res.lastIndexOf("/");
			if (pos <= 0) {
				res = "/winery-topologymodeler";
			} else {
				res = res.substring(0, pos);
				res = res + "winery-topologymodeler";
			}
			return res;
		} else {
			assert (this.wineryTopologyModelerPath != null);
			return this.wineryTopologyModelerPath;
		}
	}
	
	/**
	 * Returns the read content from winery.properties.
	 * 
	 * @return the internal object held by this class. Manipulations on this
	 *         object may cause trouble.
	 */
	public Properties getProperties() {
		return this.properties;
	}
	
	/**
	 * @return the version of winery
	 */
	public String getVersion() {
		return Version.VERSION;
	}
	
	/**
	 * @return true if the OpenTOSCA container is locally available
	 */
	public boolean isContainerLocallyAvailable() {
		if (this.isContainerLocallyAvailable == null) {
			// we initialize the variable at the first read
			// The container and Winery are started simultaneously
			// Therefore, the container might not be available if Winery is starting
			// When checking at the first read, chances are high that the container started
			this.isContainerLocallyAvailable = OpenTOSCAContainerConnection.isContainerLocallyAvailable();
		}
		return this.isContainerLocallyAvailable;
	}
	
	/**
	 * @return true if the plan generator is available
	 */
	public boolean isPlanBuilderAvailable() {
		// similar implementation as isContainerLocallyAvailable()
		if (this.isPlanBuilderAvailable == null) {
			String planBuilderURI = "http://localhost:1339/planbuilder";
			this.isPlanBuilderAvailable = Utils.isResourceAvailable(planBuilderURI);
		}
		
		if(!this.isPlanBuilderAvailable){
			String containerPlanBuilderURI = "http://localhost:1337/containerapi/planbuilder";
			this.isPlanBuilderAvailable = Utils.isResourceAvailable(containerPlanBuilderURI);
		}
		
		return this.isPlanBuilderAvailable;
	}
	
	/**
	 * Quick hack to check whether a RestDoc documentation is available at
	 * /restdoc.html. We do not deliver
	 */
	public boolean isRestDocDocumentationAvailable() {
		String path = "http://localhost:8080/restdoc.html";
		if (this.isRestDocDocumentationAvailable == null) {
			// we initialize the variable at the first read
			// The container and Winery are started simultaneously
			// Therefore, the container might not be available if Winery is starting
			// When checking at the first read, chances are high that the container started
			this.isRestDocDocumentationAvailable = Utils.isResourceAvailable(path);
		}
		return this.isRestDocDocumentationAvailable;
	}
	
	/**
	 * @return the base URL of the BPMN4TOSCA plan modeler. NULL if not
	 *         configured. May also be empty.
	 */
	public String getBPMN4TOSCABaseURL() {
		return this.properties.getProperty(Prefs.PROP_BPMN4TOSCA_MODELER_URI);
	}
	
}
