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
package org.eclipse.winery.repository.rest.datatypes;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.backend.RepositoryFactory;
import org.eclipse.winery.repository.configuration.Environment;
import org.eclipse.winery.repository.rest.RestUtils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * based on
 * https://github.com/blueimp/jQuery-File-Upload/wiki/Google-App-Engine-Java
 *
 * The getters are named according to the requirements of the template in
 * jquery-file-upload-full.jsp
 */
@XmlRootElement
public class FileMeta {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileMeta.class);
	private static final String deleteType = "DELETE";

	private String name;
	private long size;
	private String url;
	private String deleteUrl;
	private String thumbnailUrl;

	public FileMeta(String filename, long size, String url, String thumbnailUrl) {
		this.name = filename;
		this.size = size;
		this.url = url;
		this.thumbnailUrl = thumbnailUrl;
		this.deleteUrl = url;
	}

	public FileMeta(RepositoryFileReference ref) {
		this.name = ref.getFileName();
		try {
			this.size = RepositoryFactory.getRepository().getSize(ref);
		} catch (IOException e) {
			FileMeta.LOGGER.error(e.getMessage(), e);
			this.size = 0;
		}
		this.url = RestUtils.getAbsoluteURL(ref);
		this.deleteUrl = this.url;
		this.thumbnailUrl = Environment.getUrlConfiguration().getRepositoryApiUrl() + Constants.PATH_MIMETYPEIMAGES + FilenameUtils.getExtension(this.name) + Constants.SUFFIX_MIMETYPEIMAGES;
	}

	/**
	 * @param ref the reference to get information from
	 * @param URLprefix the string which should be prepended the actual URL.
	 *            Including the "/"
	 */
	public FileMeta(RepositoryFileReference ref, String URLprefix) {
		this(ref);
		this.url = URLprefix + this.url;
	}

	/**
	 * The constructor is used for JAX-B only. Therefore, the warning "unused"
	 * is suppressed
	 */
	@SuppressWarnings("unused")
	private FileMeta() {
	}

	public String getName() {
		return this.name;
	}

	public long getSize() {
		return this.size;
	}

	public String getUrl() {
		return this.url;
	}

	public String getDeleteUrl() {
		return this.deleteUrl;
	}

	public String getDeleteType() {
		return deleteType;
	}

	public String getThumbnailUrl() {
		return this.thumbnailUrl;
	}

}
