# 🩸 Blood Link - Blood Donor Management System

![Project Status](https://img.shields.io/badge/Status-Development_Phase-blue?style=for-the-badge)
![Author](https://img.shields.io/badge/Author-Emon_Ahmed_Joy-red?style=for-the-badge)
![Language](https://img.shields.io/badge/Language-Java_Swing-orange?style=for-the-badge)

**Blood Link** is a Desktop Application designed to connect blood donors and recipients. It features a modern interface with logic for request tracking and administration.

---

## ✨ Key Features

### 🎨 UI/UX
*   **Animated Background**: Blood-cell particles moving in real-time.
*   **Custom Components**: Pill-shaped buttons with gradients and shadow-card containers.
*   **Navigation**: Tabbed login system and dashboards for all roles.

### 🔍 Search Engine
*   **Proximity Scoring**: An algorithm that prioritizes results by matching Blood Group, then City, then State.
*   **Availability**: Only shows donors who are currently available.

### 🔔 Notifications
*   **Alerts**: Popup notifications notify Donors of new requests and Users of status updates.

### 🛡️ Admin Panel
*   **Management**: Separate tabs for managing Donors and General Users.
*   **Search**: Find any account by email address.
*   **Moderation**: Block/Unblock functionality to restrict access.

---

## 🛠️ How to Run

### Prerequisites
*   Java Development Kit (JDK) 8 or higher.
*   A terminal/IDE (VS Code, IntelliJ, or Eclipse).

### Execution
1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/Emon-Ahmed-Joy/Blood-Donner-Management-System-Project.git
    ```
2.  **Compile the Source**:
    ```bash
    javac -d bin src/model/*.java src/database/*.java src/ui/*.java
    ```
3.  **Run the Application**:
    ```bash
    java -cp bin ui.LoginPage
    ```

---

## 👥 Team & Work Distribution

| Member | Role | Assigned Task |
| :--- | :--- | :--- |
| **Emon Ahmed Joy** | Manager / Lead Designer | UI/UX, Base Logic, Branding, Admin System |
| **Durjoy** | Database Engineer | MySQL Integration & Data Migration |
| **Lisan** | Backend Developer 1 | Profile Editing & Request History |
| **Nafiz** | Backend Developer 2 | System Stats & Input Validation |

---

## 📜 Author Branding
Every file in this project is officially branded and documented.
`@author Emon Ahmed Joy`

---

## 🚀 Roadmap
- [x] High-Fidelity UI/UX
- [x] Search Logic
- [x] Admin Moderation Security
- [ ] **Next Step**: MySQL Database Persistence (Member 4)
- [ ] **Next Step**: Automated Email Notifications (Members 2/3)

---
*Developed with ❤️ for a better healthcare connection.*
