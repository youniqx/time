#!/usr/bin/env bash

response=$(curl --fail --request POST \
--url "https://api.github.com/app/installations/$1/access_tokens" \
--header "Accept: application/vnd.github+json" \
--header "Authorization: Bearer $2" \
--header "X-GitHub-Api-Version: 2022-11-28")

echo "$response" | jq '.token'

