#!/usr/bin/env bash

dir=$(dirname "$0")
jwt=$("${dir}/generateJwt.sh" "$GITHUB_APP_CLIENT_ID" "$GITHUB_APP_PRIVATE_KEY")
export GITHUB_TOKEN=$("${dir}/generateInstallationAccessToken.sh" "$GITHUB_INSTALLATION_ID" "$jwt")
