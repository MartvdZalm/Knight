#!/bin/bash

SOURCE_DIR="$(pwd)/share"
DEST_DIR="/usr/local/include/knight"

sudo mkdir -p "$DEST_DIR"
sudo cp "$SOURCE_DIR"/* "$DEST_DIR/"
sudo chmod 644 "$DEST_DIR"/*

echo "Knight standard library installed/updated at $DEST_DIR"
