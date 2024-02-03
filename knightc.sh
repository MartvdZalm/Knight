#!/bin/bash

# Get the directory of the script
SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"

# Set the CLASSPATH relative to the script's location
CLASSPATH="$SCRIPT_DIR/target/classes"

# Specify the main class
MAIN_CLASS=knight.Main

# Run the Java program
java -cp "$CLASSPATH" "$MAIN_CLASS" "$@"
