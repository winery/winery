#!/bin/bash

# Adapted based on https://gist.github.com/lukewpatterson/4242707
base64 --decode --ignore-garbage ~/.ssh/id_rsa_base64 > ~/.ssh/id_rsa
chmod 600 ~/.ssh/id_rsa
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_rsa
# Prevent "Are you sure you want to continue connecting (yes/no)? " prompt
printf "Host builds.opentosca.org\n\tStrictHostKeyChecking no\n" >> ~/.ssh/config

command="cd www/winery/\n"

# change into dir and delete old snapshots
command="${command}mkdir $TRAVIS_BRANCH\ncd $TRAVIS_BRANCH\nrm *.war\n"

command="${command}mput org.eclipse.winery.repository/target/*.war\n"
command="${command}mput org.eclipse.winery.topologymodeler/target/*.war\n"
command="${command}exit\n"

# now $command is complete

printf "$command" | sftp -P 443 builds_opentosca_org@builds.opentosca.org
