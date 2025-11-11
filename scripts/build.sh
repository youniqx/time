#!/bin/sh

set -o errexit
# Set pipefail if it works in a subshell, disregard if unsupported
# shellcheck disable=SC3040
(set -o pipefail 2> /dev/null) && set -o pipefail

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

buildAndPackage() {
  getVersionName

  ./gradlew composeApp:packageAppImage
  cd composeApp/build/compose/binaries/main/app/ || exit 1
  tar -czf "time_${PKG_ORIGIN}${PKG_VERSION}.tar.gz" com.youniqx.time
}

buildAndPackage
