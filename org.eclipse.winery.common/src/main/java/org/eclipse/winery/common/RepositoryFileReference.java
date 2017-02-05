/*******************************************************************************
 * Copyright (c) 2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Oliver Kopp - initial API and implementation
 *******************************************************************************/
package org.eclipse.winery.common;

import org.eclipse.winery.common.ids.GenericId;

/**
 * Holds a reference to a file "object" stored in the repository
 *
 * Directories are NOT supported as we would have to reflect parent
 * relationships there, too.
 *
 * One has to create TOSCAelementId-objects for directories (e.g., scc-data)
 */
public class RepositoryFileReference implements Comparable<RepositoryFileReference> {

	protected final GenericId parent;
	protected final String fileName;


	/**
	 * @param parent the id of the toscaElement the file is nested in
	 * @param fileName the file name. <em>Must not</em> contain any illegal
	 *            characters. java.nio.Path cannot be used as Path is tied to a
	 *            FileSystem
	 */
	public RepositoryFileReference(GenericId parent, String fileName) {
		if (parent == null) {
			throw new IllegalArgumentException("Parent must not be null.");
		}
		if (fileName == null) {
			throw new IllegalArgumentException("Filename must not be null.");
		}
		this.parent = parent;
		this.fileName = fileName;
	}

	public GenericId getParent() {
		return this.parent;
	}

	public String getFileName() {
		return this.fileName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RepositoryFileReference) {
			RepositoryFileReference otherRef = (RepositoryFileReference) obj;
			return (otherRef.fileName.equals(this.fileName)) && (otherRef.getParent().equals(this.getParent()));
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.getParent().hashCode() ^ this.getFileName().hashCode();
	}

	@Override
	public int compareTo(RepositoryFileReference o) {
		int res;
		res = this.parent.compareTo(o.parent);
		if (res == 0) {
			res = this.fileName.compareTo(o.fileName);
		}
		return res;
	}

	@Override
	public String toString() {
		return this.getParent().toString() + " / " + this.getFileName();
	}
}
