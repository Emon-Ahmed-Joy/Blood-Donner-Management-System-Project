# 🩸 Blood Donor Management System - Project Report & Team Roadmap

## 👤 Part 1: Manager's Work Progress (For Teacher Submission)
**Author: Emon Ahmed Joy**

I have designed and implemented the architecture of the Blood Donor Management System. My focus was on creating a good user experience and a working logic framework.

### Key Contributions:
1.  **UI/UX Design**:
    *   Developed a custom `GradientPanel` featuring blood-cell particles to create a modern feel.
    *   Implemented `RoundedButton` with custom gradients and interactive hover effects.
    *   Designed a `LoginPage` and unified the visual identity across all UI pages.
2.  **Logic & Features**:
    *   **Search Algorithm**: Created a scoring algorithm that prioritizes donors by Blood Group, then City, then State.
    *   **Notification System**: Built a system that alerts users and donors via UI popups when their requests are updated.
    *   **Admin Panel**: Designed an administration panel with search (by email) and role-based moderation (Block/Unblock).
3.  **Security & System Integrity**:
    *   Implemented a login restriction system that prevents blocked users from accessing the application.
    *   Structured the `DataStore` to handle multi-role interactions (User, Donor, Admin).
4.  **Code Quality**:
    *   Applied global author branding (`@author Emon Ahmed Joy`) to all Java source files.
    *   Ensured the project is stable and compilable.

---

## 👥 Part 2: Team Work Distribution

### 1. Database Member (Durjoy)
**Branch:** `Service-Database`
**Goal:** Replace the temporary `DataStore.java` lists with a real MySQL Database.

**Action Steps:**
1.  **Setup MySQL**: Create a database named `blood_management` with tables: `users`, `donors`, and `blood_requests`.
2.  **JDBC Integration**: Add the MySQL Connector JAR to the project.
3.  **DataStore Migration**: 
    *   Update `DataStore.java` to use JDBC for database connection.
    *   Change the static lists to methods that fetch/save data directly to MySQL.
    *   Ensure that when a user registers, their data is saved to the database.

---

### 2. Backend Member 1 (Lisan)
**Branch:** `Service-Backend-Logic-1`
**Goal:** Improve the Donor Profile and Request details.

**Action Steps:**
1.  **Profile Editing**: Add a "Edit Profile" button on the `DonorProfilePage`.
2.  **Field Update**: Allow donors to update their location or availability status.
3.  **Request History**: Add a "History" tab in the Donor dashboard to see all past requests.

---

### 3. Backend Member 2 (Nasir)
**Branch:** `Service-Backend-Logic-2`
**Goal:** Add visual reports and "About" information.

**Action Steps:**
1.  **System Stats**: On the `AdminPage`, add a panel that shows totals like "Total Donors" and "Pending Requests".
2.  **App Info**: Create an `AboutPage.java` that explains the purpose of the app and lists the team members.
3.  **Input Validation**: Add checks to the `RegistrationPage` (e.g., password length or email format).

---

## 🛠️ How to Start
1.  **Clone** the repository from GitHub.
2.  **Switch** to your assigned branch: `git checkout [branch-name]`.
3.  **Work** on your assigned steps.
4.  **Commit** your changes: `git add . && git commit -m "Done: [description]"`.
5.  **Push** to your branch.

---
*By Emon Ahmed Joy*
