@echo off
rem Copyright (c) 2017 Contributors to the Eclipse Foundation
rem .
rem See the NOTICE file(s) distributed with this work for additional
rem information regarding copyright ownership.
rem .
rem This program and the accompanying materials are made available under the
rem terms of the Eclipse Public License 2.0 which is available at
rem http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
rem which is available at https://www.apache.org/licenses/LICENSE-2.0.
rem .
rem SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
rem .
echo Statistics generator for Pratical Course Winery:  SS 2017
echo ==========================================================
echo .
echo Python needs to be installed.
echo This script needs to be run in the current git checkout of Winery -
echo the checkout has to be complete, i.e., no checkout with --depth 10.
echo .
echo Install gitinspector by using npm install -g gitinspector
echo .
echo It might help to execute "git config diff.renameLimit 999999" on the
echo command line
echo .
pause
mkdir statistics
gitinspector -f java,html,ts,css,json,xml,md --since=2017-07-30 --grading -x "author:^(?!((Lukas Harzenetter)|(Lukas Balzer)|(Niko Stadelmaier)|(Tino Stadelmaier)|(Philipp Meyer)))" --format=htmlembedded ./ > ./statistics/output.html
