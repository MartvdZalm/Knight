#!/bin/bash

SOURCE_DIR="$(pwd)/share" 

DEST_DIR="/usr/local/include/knight"

sudo mkdir -p "$DEST_DIR"

sudo cp "$SOURCE_DIR/std.h" "$DEST_DIR/"

sudo chmod 644 "$DEST_DIR/std.h"

echo "Knight standard library installed/updated at $DEST_DIR"
