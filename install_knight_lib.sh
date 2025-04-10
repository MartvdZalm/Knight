#!/bin/bash

SOURCE_DIR="$(pwd)/src/main/java/knight/share/knight"  # Adjust this if your header file is in a different location

DEST_DIR="/usr/local/include/knight"

# Create the destination directory if it doesn't exist
sudo mkdir -p "$DEST_DIR"

# Copy the header file(s) to the include directory
sudo cp "$SOURCE_DIR/knight_std.h" "$DEST_DIR/"

# Set correct permissions
sudo chmod 644 "$DEST_DIR/knight_std.h"

echo "Knight standard library installed/updated at $DEST_DIR"
