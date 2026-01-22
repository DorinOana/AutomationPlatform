#!/usr/bin/env bash
set -euo pipefail

DEPS_POM="automationplatform-deps/pom.xml"
BOM_POM="automationplatform-bom/pom.xml"

if [[ ! -f "$DEPS_POM" ]]; then
  echo "ERROR: $DEPS_POM not found" >&2
  exit 1
fi

if [[ ! -f "$BOM_POM" ]]; then
  echo "ERROR: $BOM_POM not found" >&2
  exit 1
fi

# Keys we want to keep in sync (add new ones here as your platform grows).
KEYS=(
  "slf4j.version"
  "log4j2.version"
  "junit.jupiter.version"
)

# Helper: extract <key>value</key> from deps POM properties.
extract_value() {
  local key="$1"
  # Match: <key>...</key> within properties. Keep it simple and explicit.
  # Works as long as each property is on one line (which is typical and recommended).
  local line
  line="$(grep -E "^[[:space:]]*<${key}>.*</${key}>[[:space:]]*$" "$DEPS_POM" || true)"
  if [[ -z "$line" ]]; then
    echo "ERROR: Could not find <${key}>...</${key}> in $DEPS_POM" >&2
    exit 1
  fi
  # Strip tags
  echo "$line" | sed -E "s/^[[:space:]]*<${key}>(.*)<\/${key}>[[:space:]]*$/\1/"
}

# Helper: replace <key>...</key> in BOM POM.
replace_value_in_bom() {
  local key="$1"
  local value="$2"

  # macOS sed needs -i '' ; Linux sed uses -i
  if sed --version >/dev/null 2>&1; then
    # GNU sed (Linux)
    sed -i -E "s|^([[:space:]]*<${key}>).*(</${key}>[[:space:]]*)$|\1${value}\2|" "$BOM_POM"
  else
    # BSD sed (macOS)
    sed -i '' -E "s|^([[:space:]]*<${key}>).*(</${key}>[[:space:]]*)$|\1${value}\2|" "$BOM_POM"
  fi
}

echo "Syncing versions from $DEPS_POM -> $BOM_POM"
changed=0

for key in "${KEYS[@]}"; do
  deps_val="$(extract_value "$key")"

  # Read current bom value (optional, for nice logging)
  bom_line="$(grep -E "^[[:space:]]*<${key}>.*</${key}>[[:space:]]*$" "$BOM_POM" || true)"
  if [[ -z "$bom_line" ]]; then
    echo "ERROR: Could not find <${key}>...</${key}> in $BOM_POM" >&2
    echo "Tip: ensure the key exists in BOM properties before syncing." >&2
    exit 1
  fi
  bom_val="$(echo "$bom_line" | sed -E "s/^[[:space:]]*<${key}>(.*)<\/${key}>[[:space:]]*$/\1/")"

  if [[ "$deps_val" != "$bom_val" ]]; then
    echo " - ${key}: ${bom_val} -> ${deps_val}"
    replace_value_in_bom "$key" "$deps_val"
    changed=1
  else
    echo " - ${key}: unchanged (${deps_val})"
  fi
done

if [[ "$changed" -eq 1 ]]; then
  echo "Done. BOM updated."
else
  echo "Done. No changes needed."
fi
