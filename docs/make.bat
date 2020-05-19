@ECHO OFF
REM Copyright (c) 2020 Contributors to the Eclipse Foundation
REM
REM See the NOTICE file(s) distributed with this work for additional
REM information regarding copyright ownership.
REM
REM This program and the accompanying materials are made available under the
REM terms of the Eclipse Public License 2.0 which is available at
REM http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
REM which is available at https://www.apache.org/licenses/LICENSE-2.0.
REM
REM SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

pushd %~dp0

REM Command file for Sphinx documentation

if "%SPHINXBUILD%" == "" (
	set SPHINXBUILD=sphinx-build
)
set SOURCEDIR=.
set BUILDDIR=_build

if "%1" == "" goto help

%SPHINXBUILD% >NUL 2>NUL
if errorlevel 9009 (
	echo.
	echo.The 'sphinx-build' command was not found. Make sure you have Sphinx
	echo.installed, then set the SPHINXBUILD environment variable to point
	echo.to the full path of the 'sphinx-build' executable. Alternatively you
	echo.may add the Sphinx directory to PATH.
	echo.
	echo.If you don't have Sphinx installed, grab it from
	echo.http://sphinx-doc.org/
	exit /b 1
)

%SPHINXBUILD% -M %1 %SOURCEDIR% %BUILDDIR% %SPHINXOPTS% %O%
goto end

:help
%SPHINXBUILD% -M help %SOURCEDIR% %BUILDDIR% %SPHINXOPTS% %O%

:end
popd
