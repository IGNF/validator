#!/usr/bin/env bash
# Checks the runtime dependencies of the project (all modules) against the OSV database
# (https://osv.dev) and fails if a known vulnerability is found.
#
# Usage : bash .ci/osv-check.sh
set -euo pipefail

cd "$(dirname "$0")/.."
tmp="$(mktemp -d)"
trap 'rm -rf "$tmp"' EXIT

echo "Listing the runtime dependencies..."
mvn -B -q dependency:list -DincludeScope=runtime -DappendOutput=true -DoutputFile="$tmp/deps.txt"

python3 -I - "$tmp/deps.txt" <<'PY'
import json
import re
import sys
import urllib.request

# "   group:artifact:jar:version:scope" (the modules of the project are ignored)
pattern = re.compile(r"^\s+([^:\s]+):([^:\s]+):[^:\s]+:([^:\s]+):[a-z]+")
dependencies = set()
for line in open(sys.argv[1], encoding="utf-8"):
    match = pattern.match(line)
    if match and match.group(1) != "fr.ign.validator":
        dependencies.add(match.groups())
dependencies = sorted(dependencies)
print("%d dependencies" % len(dependencies))


def post(url, body):
    request = urllib.request.Request(
        url, data=json.dumps(body).encode(), headers={"Content-Type": "application/json"}
    )
    with urllib.request.urlopen(request, timeout=60) as response:
        return json.load(response)


queries = [
    {"package": {"ecosystem": "Maven", "name": "%s:%s" % (group, artifact)}, "version": version}
    for group, artifact, version in dependencies
]
results = post("https://api.osv.dev/v1/querybatch", {"queries": queries})["results"]

failed = False
for (group, artifact, version), result in zip(dependencies, results):
    vulns = result.get("vulns", [])
    if vulns:
        failed = True
        ids = ", ".join(v["id"] for v in vulns)
        print("VULNERABLE %s:%s:%s : %s" % (group, artifact, version, ids))
if failed:
    sys.exit(1)
print("No known vulnerability.")
PY
