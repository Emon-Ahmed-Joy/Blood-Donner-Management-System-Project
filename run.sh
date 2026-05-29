#!/bin/bash
echo "Recompiling project..."
mkdir -p bin
javac -d bin -cp "lib/*" src/database/*.java src/model/*.java src/ui/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Starting Blood Donor Management System..."
java -cp "bin:lib/*" ui.LoginPage
