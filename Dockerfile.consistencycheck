FROM maven:3-jdk-8 as builder

WORKDIR /tmp/winery
COPY . /tmp/winery

# -am used to build all required other projects
RUN mvn package -pl org.eclipse.winery.cli -am -DskipTests

RUN chmod u+x /tmp/winery/org.eclipse.winery.cli/target/winery.jar


FROM openjdk:8-jre
LABEL maintainer "Oliver Kopp <kopp.dev@gmail.com>"

RUN curl -s https://packagecloud.io/install/repositories/github/git-lfs/script.deb.sh | bash
RUN apt-get update && apt-get install -y git git-lfs

COPY --from=builder /tmp/winery/org.eclipse.winery.cli/target/winery.jar /usr/local/bin
COPY winery /usr/local/bin

# currently, winery just executes the consistency check
CMD winery
