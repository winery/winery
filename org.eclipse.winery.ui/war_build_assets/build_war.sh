#!/usr/bin/env bash
jar cvf WineryUi.war -C ./dist/ .
mkdir -p war
mv ./WineryUi.war ./war/
