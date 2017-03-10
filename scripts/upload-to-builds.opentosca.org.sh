#!/bin/bash

command="cd www/winery/\n"

# change into dir and delete old snapshots
command="${command}mkdir $TRAVIS_BRANCH\ncd $TRAVIS_BRANCH\nrm *.war\n"

command="${command}mput org.eclipse.winery.repository/target/*.war\n"
command="${command}mput org.eclipse.winery.topologymodeler/target/*.war\n"
command="${command}exit\n"

# now $command is complete

echo -e "$command" | sftp -P 443 builds_opentosca_org@builds.opentosca.org
