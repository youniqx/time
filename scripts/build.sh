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

getAppImageTool() {
  if [ -e  tmp/appimagetool-x86_64.AppImage ];then
    echo "appimage tool in place"
  else
    mkdir -p tmp
    echo "no appimagetool found, download…"
    curl -Lo tmp/appimagetool-x86_64.AppImage https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage
  fi
  chmod u+x tmp/appimagetool-x86_64.AppImage
}

buildAppImage() {
  ./gradlew composeApp:packageAppImage
  getAppImageTool

  cp -r "AppImageTemplateDir" "tmp/Time.AppDir"
  mkdir -p "tmp/Time.AppDir/usr"
  cp -r composeApp/build/compose/binaries/main/app/com.youniqx.time/bin tmp/Time.AppDir/usr/bin
  cp -r composeApp/build/compose/binaries/main/app/com.youniqx.time/lib tmp/Time.AppDir/usr/lib
  cp tmp/Time.AppDir/usr/lib/com.youniqx.time.png tmp/Time.AppDir/time.png
  ARCH=x86_64 ./tmp/appimagetool-x86_64.AppImage tmp/Time.AppDir \
    "time-${PKG_ORIGIN}${PKG_VERSION}-linux_amd64.AppImage" \
    --appimage-extract-and-run
  rm -rf "tmp" || true
}

buildDmg() {
  ./gradlew composeApp:packageDmg
  mv composeApp/build/compose/binaries/main/dmg/*.dmg "./time-${PKG_ORIGIN}${PKG_VERSION}.dmg"
}

main() {
  getVersionName
  if [ "$appImage" = true ];then
    buildAppImage
  fi
  if [ "$dmg" = true ];then
    buildDmg
  fi
}

main
