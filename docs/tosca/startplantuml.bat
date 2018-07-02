:: Copyright (c) 2013-2014 Contributors to the Eclipse Foundation
::
:: See the NOTICE file(s) distributed with this work for additional
:: information regarding copyright ownership.
::
:: This program and the accompanying materials are made available under the
:: terms of the Eclipse Public License 2.0 which is available at
:: http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
:: which is available at https://www.apache.org/licenses/LICENSE-2.0.
::
:: SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

@echo off
echo Generating SVG...
SET PLANTUML=C:\Users\Oliver\BTSync\plantuml.jar
java -jar %PLANTUML% -tsvg TOSCA-v1.0-os-class-diagram.plantuml
echo Generating PDF...
inkscape -z -D --file=TOSCA-v1.0-os-class-diagram.svg --export-pdf=TOSCA-v1.0-os-class-diagram.pdf
echo Done
