#!/usr/bin/env bash
set -e
declare -A options=( [DIR]='' [prebuilt_path]='' )  # array (associative)

case $1 in
install)
  # Install the latest release (simplified).

  options['DIR']=build-auto
  PREBUILT_SERVER_URL=https://github.com/Genymobile/scrcpy/releases/download/v1.25/scrcpy-server-v1.25
  PREBUILT_SERVER_SHA256=ce0306c7bbd06ae72f6d06f7ec0ee33774995a65de71e0a83813ecb67aec9bdb

  echo "[scrcpy] Downloading prebuilt server..."
  wget "$PREBUILT_SERVER_URL" -O scrcpy-server
  echo "[scrcpy] Verifying prebuilt server..."
  echo "$PREBUILT_SERVER_SHA256  scrcpy-server" | sha256sum --check

  echo "[scrcpy] Building client..."
  rm -rf "${options['DIR']}"

  options['prebuilt_path']=scrcpy-server
;;
build)
  if [[ -n $2 ]]; then
    options['prebuilt_path']=$2  # length is non-zero
  fi
  options['DIR']='x'
  options['setup']='true'
;;
esac

# ${PARAMETER:+WORD}  If PARAMETER is null or unset, nothing is substituted,
#                     otherwise the expansion of WORD is substituted.

meson ${options['setup']:+$( \
    echo -n 'setup ' \
  )}"${options['DIR']}" --buildtype=release --strip -Db_lto=true \
  ${options['prebuilt_path']:+$( \
    echo -n "-Dprebuilt_server=${options['prebuilt_path']}" \
  )}

if [[ $1 == 'install' ]]; then
  cd "${options['DIR']}"
  ninja

  echo "[scrcpy] Installing (sudo)..."
  sudo ninja install
else
  ninja -Cx  # DO NOT RUN AS ROOT
fi