# Eclipse Winery - OpenTOSCA fork

This is a fork of [Eclipse Winery](https://github.com/eclipse/winery) and might include current research results not yet approved by Eclipse legal.
Find out more about the OpenTOSCA eco system at [www.opentosca.org](http://www.opentosca.org/).

## Running via docker

1. `docker build -t winery .`. In case, there are issues, you can also try `docker build --no-cache -t winery .`
2. `docker run -p 9999:8080 winery` to run winery on http://localhost:9999/

## Differences to Eclipse Winery

The branch `master` differs from eclipse/winery in the following files:

- [README.md](README.md) - This text file
- [.travis.yml](.travis.yml) - Different AWS S3 upload directory
- [Dockerfile](Dockerfile) - Custom Docker build for the OpenTOSCA organization
- [pom.xml](pom.xml) - Upload to opentosca/mvn-repo

One can find out the differences between the master branches of these two repositories by executing the following command:

    git fetch --all
    git difftool upstream/master origin/master

Precondition:

    git remote add upstream https://github.com/eclipse/winery.git
