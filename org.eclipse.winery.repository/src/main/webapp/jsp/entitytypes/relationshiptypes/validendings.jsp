<%--
/*******************************************************************************
 * Copyright (c) 2012-2013 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v10.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *    Oliver Kopp - initial API and implementation and/or initial documentation
 *    Yves Schubert - switch to bootstrap 3
 *******************************************************************************/
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="rt" tagdir="/WEB-INF/tags/relationshiptype" %>

<h4>Valid Source</h4>
<input type="radio" name="SourceKind">Node Type:</input>
<rt:validnodetypeendingsselect shortName="source" currentSelection="${it.validSource}" possibleValidEndings="${it.possibleValidEndings}">
</rt:validnodetypeendingsselect>
<br/>
<input type="radio" name="SourceKind">Requirement Type:</input>
<select>
    <option>Not yet implemented. Please edit in the XML view</option>
</select>


<br/>
<br/>
<h4>Valid Target</h4>
<input type="radio" name="TargetKind">Node Type:</input>
<rt:validnodetypeendingsselect shortName="target" currentSelection="${it.validTarget}" possibleValidEndings="${it.possibleValidEndings}">
</rt:validnodetypeendingsselect>
<br/>
<input type="radio" name="TargetKind">Capability Type:</input>
<select>
    <option>Not yet implemented. Please edit in the XML view</option>
</select>
