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
/**
 * This package contains the REST resources
 *
 * Mostly, they produces Viewables, where a JSP and the current resource is
 * passed As the JSP itself handles plain Java objects and not Responses, the
 * resources have also methods returning POJOs. This might be ugly design, but
 * was quick to implement.
 *
 * The package structure is mirrored in src/main/webapp/jsp to ease finding the
 * JSPs belonging to a resource.
 *
 * The resources are <em>not</em> in line with the resource model of the TOSCA
 * container. Especially, we do not employ HATEOAS here.
 */
package org.eclipse.winery.repository.rest.resources;

