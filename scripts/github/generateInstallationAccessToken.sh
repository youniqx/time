#!/usr/bin/env bash

response=$(curl --request POST \
--url "https://api.github.com/app/installations/$1/access_tokens" \
--header "Accept: application/vnd.github+json" \
--header "Authorization: Bearer $2" \
--header "X-GitHub-Api-Version: 2022-11-28" \
-d '{"permissions":{"contents":"write"}}')

token=$(echo "$response" | jq -r '.token')

echo "$token"
