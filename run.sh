#!/bin/bash
# Recompile to ensure latest changes are included
mkdir -p bin
javac -d bin -cp "lib/*" src/database/*.java src/model/*.java src/ui/*.java

# Run the app
java -cp "bin:lib/*" ui.LoginPage