#!/usr/bin/env bash
set -euo pipefail

if ! command -v keytool >/dev/null 2>&1; then
  echo "keytool not found. Install a JDK and ensure keytool is on PATH."
  exit 1
fi

keystore_path="${1:-keystore/1-release-key.jks}"
key_alias="${KEY_ALIAS:-calendar}"
dname="${DNAME:-CN=Calendar, OU=Mobile, O=CoCode, L=City, ST=State, C=US}"

store_pass="${STORE_PASS:-}"
if [ -z "$store_pass" ]; then
  read -r -s -p "Keystore password: " store_pass
  echo
fi

key_pass="${KEY_PASS:-}"
if [ -z "$key_pass" ]; then
  read -r -s -p "Key password (leave empty to reuse keystore password): " key_pass
  echo
fi
if [ -z "$key_pass" ]; then
  key_pass="$store_pass"
fi

keytool -genkeypair -v \
  -keystore "$keystore_path" \
  -alias "$key_alias" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "$store_pass" \
  -keypass "$key_pass" \
  -dname "$dname"

echo "Keystore created at $keystore_path"
