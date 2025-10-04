#!/bin/bash
set -e

echo "Stopping any running Java processes..."
pkill -f 'java -jar' || true
