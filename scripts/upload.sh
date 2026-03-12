#!/bin/sh

set -o errexit
# Set pipefail if it works in a subshell, disregard if unsupported
# shellcheck disable=SC3040
(set -o pipefail 2> /dev/null) && set -o pipefail

installGlab() {
    curl -fLo ./glab.tar.gz "https://gitlab.com/gitlab-org/cli/-/releases/v${GLAB_VERSION}/downloads/glab_${GLAB_VERSION}_linux_amd64.tar.gz"
    tar -zxf ./glab.tar.gz bin/glab
    PATH="${PATH}:${PWD}/bin"

    GLAB_SEND_TELEMETRY=0 glab config set telemetry 0
    glab auth login --hostname "$CI_SERVER_HOST" --token "GITLAB_TOKEN"
    glab config set -g host "$CI_SERVER_HOST"
}

installGh() {
    curl -fLo ./gh.tar.gz "https://github.com/cli/cli/releases/download/v${GH_VERSION}/gh_${GH_VERSION}_linux_amd64.tar.gz"
    tar -zxf ./gh.tar.gz "gh_${GH_VERSION}_linux_amd64/bin/gh"
    PATH="${PATH}:${PWD}/gh_${GH_VERSION}_linux_amd64/bin"

    dir=$(dirname "$0")
    eval "${dir}/github/authenticate.sh"
}

if [ "$CI" = "true" ]; then
    git config --global --add safe.directory "${PWD}"

    installGlab
    installGh
fi

glab release upload "${CI_COMMIT_TAG}" --assets-links='
  [
    {
      "name": "Time Website",
      "url": "'"$CI_PAGES_URL"'",
      "link_type": "other"
    }
  ]'

for f in time-*; do
  # path#name#type
  glab release upload "${CI_COMMIT_TAG}" "${f}#${f}#image"
done

gh release create --repo "$GITHUB_REPO" --notes-from-tag --title "${CI_COMMIT_TAG}" "${CI_COMMIT_TAG}" time-*
