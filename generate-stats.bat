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

echo Statistics generator for Pratical Course Winery:  SS 2017
echo ==================================================================
echo .
echo Python needs to be installed.
echo This script needs to be run in the current git checkout of Winery -
echo the checkout has to be complete, i.e., no checkout with --depth 10.
echo .
echo gitinspector in the version 0.4.4 has to reside in C:\git-repos\gitinspector\gitinspector\gitinspector
echo .
echo You can clone get it from https://github.com/ejwa/gitinspector/archive/v0.4.4.zip
echo and run the build command on the setup.py file.
echo .
pause
py C:\git-repos\gitinspector\gitinspector.py -f java,html,ts,css,json,xml,md --since=2017-07-30 --grading -x "author:^(?!((Lukas Harzenetter)|(Lukas Balzer)|(Niko Stadelmaier)|(Tino Stadelmaier)|(Philipp Meyer)))" --format=htmlembedded ./ > ./statistics/output.html
