#!/bin/bash
# Script to run the game from the jlink distribution

cd "$(dirname "$0")"

if [ ! -d "target/app" ]; then
    echo "Extracting app.zip..."
    cd target
    unzip -q app.zip
    cd ..
fi

if [ -f "target/app/bin/app" ]; then
    echo "Running game from jlink distribution..."
    target/app/bin/app
elif [ -f "target/app/bin/app.sh" ]; then
    echo "Running game from jlink distribution..."
    target/app/bin/app.sh
else
    echo "Error: Could not find launcher in target/app/bin/"
    echo "Please extract app.zip first: cd target && unzip app.zip"
    exit 1
fi

