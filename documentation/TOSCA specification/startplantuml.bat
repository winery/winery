:/***********************************************************************
: * Copyright (c) 2013-2014 University of Stuttgart.
: * All rights reserved. This program and the accompanying materials
: * are made available under the terms of the Eclipse Public License v1.0
: * and Apache License v2.0 which accompanies this distribution.
: * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
: * and the Apache License v2.0 is available at
: * http://www.opensource.org/licenses/apache2.0.php.
: * You may elect to redistribute this code under either of these licenses.
: * Contributors:
: *    Oliver Kopp - initial implementation
: *************************************************************************

@echo off
echo Generating SVG...
SET PLANTUML=C:\Users\Oliver\BTSync\plantuml.jar
java -jar %PLANTUML% -tsvg TOSCA-v1.0-os-class-diagram.plantuml
echo Generating PDF...
inkscape -z -D --file=TOSCA-v1.0-os-class-diagram.svg --export-pdf=TOSCA-v1.0-os-class-diagram.pdf
echo Done
