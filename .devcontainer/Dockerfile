FROM mcr.microsoft.com/devcontainers/java:21

# Install Maven
ARG MAVEN_VERSION=3.9.6
RUN su vscode -c "umask 0002 && . /usr/local/sdkman/bin/sdkman-init.sh && sdk install maven ${MAVEN_VERSION}"

# Install additional utilities
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install curl jq httpie

# Clean up
RUN apt-get autoremove -y && apt-get clean -y
