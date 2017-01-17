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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.eclipse.winery.common.RepositoryFileReference;
import org.eclipse.winery.common.ids.GenericId;
import org.eclipse.winery.repository.Constants;
import org.eclipse.winery.repository.Utils;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides basic implementations for {@link IRepository}
 */
public abstract class AbstractRepository implements IRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);


	/**
	 *
	 * @param ref the file reference to store the mime type for
	 * @return a reference to the file holding the mime type
	 */
	private RepositoryFileReference getMimeFileRef(RepositoryFileReference ref) {
		String fileName = ref.getFileName() + Constants.SUFFIX_MIMETYPE;
		return new RepositoryFileReference(ref.getParent(), fileName);
	}

	/**
	 * {@inheritDoc}
	 *
	 * This is a simple implementation using the information put by
	 * setMimeType(RepositoryFileReference ref) or determining the mime type
	 * using Utils.getMimeType. If the latter is done, the mime type is
	 * persisted using setMimeType
	 */
	@Override
	public String getMimeType(RepositoryFileReference ref) throws IOException {
		RepositoryFileReference mimeFileRef = this.getMimeFileRef(ref);
		String mimeType;
		if (this.exists(mimeFileRef)) {
			InputStream is = this.newInputStream(mimeFileRef);
			mimeType = IOUtils.toString(is, "UTF-8");
			is.close();
		} else {
			// repository has been manipulated manually,
			// create mimetype information
			try (InputStream is = this.newInputStream(ref);
					BufferedInputStream bis = new BufferedInputStream(is)) {
				mimeType = Utils.getMimeType(bis, ref.getFileName());
			}
			if (mimeType != null) {
				// successful execution
				this.setMimeType(ref, MediaType.valueOf(mimeType));
			} else {
				AbstractRepository.LOGGER.debug("Could not determine mimetype");
			}
		}
		return mimeType;
	}

	/**
	 * Stores the mime type of the given file reference in a separate file
	 *
	 * This method calls putContentToFile(), where the filename is appended with
	 * Constants.SUFFIX_MIMETYPE and a null mime type. The latter indicates that
	 * no "normal" file is stored.
	 *
	 * @param ref the file reference
	 * @param mediaType the mimeType
	 */
	protected void setMimeType(RepositoryFileReference ref, MediaType mediaType) throws IOException {
		RepositoryFileReference mimeFileRef = this.getMimeFileRef(ref);
		this.putContentToFile(mimeFileRef, mediaType.toString(), null);
	}

	@Override
	public Date getConfigurationLastUpdate(GenericId id) {
		RepositoryFileReference ref = BackendUtils.getRefOfConfiguration(id);
		return this.getLastUpdate(ref);
	}

	@Override
	public Configuration getConfiguration(GenericId id) {
		RepositoryFileReference ref = BackendUtils.getRefOfConfiguration(id);
		return this.getConfiguration(ref);
	}

}
