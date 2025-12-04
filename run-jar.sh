#!/bin/bash
# Script to run the game from JAR file with JavaFX modules from Maven repository

# Find JavaFX modules in Maven repository
JAVAFX_DIR="$HOME/.m2/repository/org/openjfx"
JAVAFX_BASE=$(find "$JAVAFX_DIR" -name "javafx-base-21.0.6-linux.jar" 2>/dev/null | head -1 | xargs dirname)

if [ -z "$JAVAFX_BASE" ]; then
    echo "Error: Could not find JavaFX modules in Maven repository."
    echo "Please run: mvn dependency:resolve first"
    exit 1
fi

echo "Using JavaFX from: $JAVAFX_BASE"
echo "Running game..."

# Run with module path
java --module-path "$JAVAFX_BASE" \
     --add-modules javafx.controls,javafx.fxml,javafx.graphics \
     -jar target/OfficeEscape-1.0-SNAPSHOT.jar
