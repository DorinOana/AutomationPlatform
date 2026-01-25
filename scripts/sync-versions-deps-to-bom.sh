#!/usr/bin/env bash
set -euo pipefail

# =========================
# Config
# =========================
DEPS_POM="automationplatform-deps/pom.xml"
BOM_POM="automationplatform-bom/pom.xml"

# =========================
# Sanity checks
# =========================
if [[ ! -f "$DEPS_POM" ]]; then
  echo "ERROR: $DEPS_POM not found" >&2
  exit 1
fi

if [[ ! -f "$BOM_POM" ]]; then
  echo "ERROR: $BOM_POM not found" >&2
  exit 1
fi

# =========================
# Python does the heavy lifting
# (multiline-safe, predictable, portable)
# =========================
python3 - <<'PY'
from pathlib import Path
import re

deps_pom = Path("automationplatform-deps/pom.xml").read_text(encoding="utf-8")
bom_pom  = Path("automationplatform-bom/pom.xml").read_text(encoding="utf-8")

def extract_block(text: str, begin: str, end: str) -> str:
    """
    Extracts text between two marker comments.
    """
    pattern = re.compile(
        rf"{re.escape(begin)}(.*?){re.escape(end)}",
        re.DOTALL
    )
    match = pattern.search(text)
    if not match:
        raise SystemExit(f"ERROR: Could not find block:\n{begin} ... {end}")
    return match.group(1).strip()

def replace_block(text: str, begin: str, end: str, new_content: str) -> str:
    """
    Replaces text between two marker comments.
    """
    pattern = re.compile(
        rf"({re.escape(begin)})(.*?)(\s*{re.escape(end)})",
        re.DOTALL
    )
    if not pattern.search(text):
        raise SystemExit(f"ERROR: Could not replace block:\n{begin} ... {end}")
    return pattern.sub(
        rf"\1\n{new_content}\n\3",
        text,
        count=1
    )

# =========================
# Markers (single source of truth)
# =========================
PROPS_BEGIN = "<!-- BEGIN BOM EXPORT PROPERTIES -->"
PROPS_END   = "<!-- END BOM EXPORT PROPERTIES -->"

DEPS_BEGIN = "<!-- BEGIN BOM EXPORT DEPENDENCIES -->"
DEPS_END   = "<!-- END BOM EXPORT DEPENDENCIES -->"

BOM_PROPS_BEGIN = "<!-- BEGIN SYNC FROM DEPS -->"
BOM_PROPS_END   = "<!-- END SYNC FROM DEPS -->"

BOM_DEPS_BEGIN = "<!-- BEGIN SYNC DEPENDENCIES FROM DEPS -->"
BOM_DEPS_END   = "<!-- END SYNC DEPENDENCIES FROM DEPS -->"

# =========================
# Extract from deps
# =========================
exported_properties = extract_block(deps_pom, PROPS_BEGIN, PROPS_END)
exported_dependencies = extract_block(deps_pom, DEPS_BEGIN, DEPS_END)

# =========================
# Replace in bom
# =========================
bom_pom = replace_block(
    bom_pom,
    BOM_PROPS_BEGIN,
    BOM_PROPS_END,
    exported_properties
)

bom_pom = replace_block(
    bom_pom,
    BOM_DEPS_BEGIN,
    BOM_DEPS_END,
    exported_dependencies
)

# =========================
# Write result
# =========================
Path("automationplatform-bom/pom.xml").write_text(bom_pom, encoding="utf-8")

print("✔ BOM synced successfully from deps")
PY
