#!/usr/bin/env bash

dir=$(dirname "$0")
jwt=$("${dir}/generateJwt.sh" "$GITHUB_APP_CLIENT_ID" "$GITHUB_APP_PRIVATE_KEY")
GITHUB_TOKEN=$("${dir}/generateInstallationAccessToken.sh" "$GITHUB_INSTALLATION_ID" "$jwt")
export GITHUB_TOKEN
