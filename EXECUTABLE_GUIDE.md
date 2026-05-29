# How to Create an Executable (JAR)

To run this app without an IDE, you have two options:

### **Option 1: Using the Run Scripts**
*   **Linux:** Double-click `run.sh` or run `./run.sh` in terminal.
*   **Windows:** Double-click `run.bat`.

### **Option 2: Creating a Portable JAR**
1. Open your project in IntelliJ or VS Code.
2. Use the **Export JAR** feature.
3. Ensure the **Main Class** is set to `ui.LoginPage`.
4. Ensure the **MySQL Connector** in the `lib` folder is included in the classpath.

### **Why use a JAR?**
*   **Portability:** It bundles all your classes into one file.
*   **Ease of Use:** Users don't need to see the code to run the app.
*   **Standard:** It is the professional way to distribute Java applications.