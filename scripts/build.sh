#!/bin/sh

set -o errexit
# Set pipefail if it works in a subshell, disregard if unsupported
# shellcheck disable=SC3040
(set -o pipefail 2> /dev/null) && set -o pipefail

appImage=false
dmg=false

## get arguments from script call
while true; do
  if [ "${1}" = "--appImage" ]; then
    appImage=true
    shift
  elif [ "${1}" = "--dmg" ]; then
    dmg=true
    shift
  else
    break
  fi
done

PKG_ORIGIN=""

getVersionName() {
  if [ "${CI}" != "true" ];then
    PKG_ORIGIN="local_"
    export PKG_VERSION="1.0.0"
  elif [ -n "${CI_COMMIT_TAG}" ];then
    export PKG_VERSION="${CI_COMMIT_TAG#v}"
  else
    PKG_ORIGIN="snapshot_"
    export PKG_VERSION="${CI_PIPELINE_ID}"
  fi
  echo "PKG_VERSION = ${PKG_VERSION}"
}

buildAndPackageAppImage() {
  ./gradlew composeApp:packageAppImage
  currentDir=$(pwd)
  cd composeApp/build/compose/binaries/main/app/ || exit 1
  tar -czf \
    "time-${PKG_ORIGIN}${PKG_VERSION}-linux_amd64.tar.gz" \
    com.youniqx.time
  mv "time-${PKG_ORIGIN}${PKG_VERSION}-linux_amd64.tar.gz" "${currentDir}/"
  cd "${currentDir}" || exit 1
}

buildDmg() {
  ./gradlew composeApp:packageDmg
  currentDir=$(pwd)
  mv composeApp/build/compose/binaries/main/dmg/*.dmg "./time-${PKG_ORIGIN}${PKG_VERSION}.dmg"
}

main() {
  getVersionName
  if [ "$appImage" = true ];then
    buildAndPackageAppImage
  fi
  if [ "$dmg" = true ];then
    buildDmg
  fi
}

main
