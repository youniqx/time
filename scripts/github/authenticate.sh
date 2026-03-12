#!/usr/bin/env bash

dir=$(dirname "$0")
echo "===Generate JWT===" >&2
jwt=$("${dir}/generateJwt.sh" "$GITHUB_APP_CLIENT_ID" "$GITHUB_APP_PRIVATE_KEY" || exit 1)
if [[ -z "$jwt" || "$jwt" == "null" ]]; then
    echo "Error: jwt is not set or is empty." >&2
    exit 1
fi
echo "===Generate IAT===" >&2
token=$("${dir}/generateInstallationAccessToken.sh" "${GITHUB_APP_INSTALLATION_ID}" "${jwt}" || exit 1)
if [[ -z "$token" || "$token" == "null" ]]; then
    echo "Error: token is not set or is empty." >&2
    exit 1
fi
echo $token