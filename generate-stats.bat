@echo off
GOTO EndOfLicense
/**
 * Copyright (c) 2017 University of Stuttgart.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * and the Apache License 2.0 which both accompany this distribution,
 * and are available at http://www.eclipse.org/legal/epl-v20.html
 * and http://www.apache.org/licenses/LICENSE-2.0
 */
:EndOfLicense

echo Statistics generator for Winery EnPro 2016/2017
echo ================================================
echo .
echo Python 3.6 needs to be installed
echo This script needs to be run in the current git checkout of Winery.
echo The checkout has to be complete, i.e., no checkout with --depth 10
echo gitinspector has to reside in ..\..\gitinspector
echo You can clone it from https://github.com/ejwa/gitinspector
echo .
pause
py -3.6 ../../gitinspector/gitinspector.py -f java,html,ts,css --grading -x "author:^(?!((Lukas Harzenetter)|(Lukas Balzer)|(Nicole Keppler)|(Niko Stadelmaier)|(Tino Stadelmaier)|(Philipp Meyer)|(Huixin Liu)))" --format=htmlembedded> statistics/output.html
