#!/bin/sh

set -o errexit
# Set pipefail if it works in a subshell, disregard if unsupported
# shellcheck disable=SC3040
(set -o pipefail 2> /dev/null) && set -o pipefail

if [ "$CI" = "true" ]; then
    git config --global --add safe.directory "${PWD}"

    curl -fLo ./glab.tar.gz "https://gitlab.com/gitlab-org/cli/-/releases/v${GLAB_VERSION}/downloads/glab_${GLAB_VERSION}_linux_amd64.tar.gz"
    tar -zxf ./glab.tar.gz bin/glab
    PATH="${PATH}:${PWD}/bin"

    GLAB_SEND_TELEMETRY=0 glab config set telemetry 0
    glab auth login --hostname "$CI_SERVER_HOST" --token "CI_JOB_TOKEN"
    glab config set -g host "$CI_SERVER_HOST"
fi

glab release upload "${CI_COMMIT_TAG}" time_*_linux_amd64.tar.gz
