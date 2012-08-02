#!/bin/bash
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
export SCRIPT_DIR=$DIR/../../../.open-delta/bin/
cd "$SCRIPT_DIR"
chmod u+x DeltaEditor.sh
./DeltaEditor.sh

