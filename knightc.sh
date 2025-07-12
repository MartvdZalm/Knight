#!/bin/bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"
CLASSPATH="$SCRIPT_DIR/target/classes"
MAIN_CLASS=knight.Main

java -cp "$CLASSPATH" "$MAIN_CLASS" "$@"
