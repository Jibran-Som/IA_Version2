# 🧭 Disaster Victim Management System (DVMS)

### 📘 Overview
The **Disaster Victim Management System (DVMS)** is a **Java application** designed to manage information about disaster victims, their locations, medical records, supplies, and inquiries.  
It provides a comprehensive interface for tracking and managing all aspects of disaster relief operations.

---

### 🚀 Features

#### 👤 Person Management
- Add, view, update, and manage personal information  
- Convert regular persons into disaster victims  
- Manage family groups  

#### 📍 Location Management
- Track shelter locations and their details  
- Manage occupants at each location  
- View and update location information  

#### 📦 Supply Management
- Track different types of supplies (water, cots, blankets, etc.)  
- Allocate supplies to individuals or locations  
- View supply inventories and allocations  

#### 🏥 Medical Records
- Maintain detailed medical records for victims  
- Track treatments and locations  
- Update medical information as needed  

#### 🔍 Inquiry System
- Record and manage inquiries about missing persons  
- Generate detailed inquiry reports  
- Link inquiries to locations and individuals  

#### 🌐 Multilingual Support
- Supports multiple languages through translation files  
- Easy to add additional language support  

---

### 🧩 Important Information
- The **difference between a `Person` and a `DisasterVictim`** is that a **DisasterVictim has a trackable inventory**.  
- The **`fr-CA.xml` translation file** is incomplete but can be used to verify that code strings are **not hardcoded**.  
- **Inquiries** can be made **from both `Person` and `DisasterVictim`**, but only **active DisasterVictims** can be the subject of inquiries.  
- **Inquiries remain** in the system regardless of the **status of the inquirer** or the **missing person**.  
- A **Person can be added to a family group after initialization**, but **not during** creation.  
- Family groups can be created through **Person Detail**, where you are prompted to **enter multiple people’s IDs** to form a group.  
- **Water supplies** are **deleted automatically** from the system **after 1 day has passed** (including hour precision).  
- When allocating a new supply:
  1. First, **create the supply** using **Add New Supply**  
  2. Then, **allocate it** to individuals or locations  

---

### 🛠️ Technologies Used
- **Programming Language:** Java  
- **Data Management:** XML-based localization and configuration  
- **Interface:** Console or GUI (depending on implementation)  

---

### 📄 Notes
This project is intended for **educational and disaster management research purposes**.  
It can be extended with additional features such as:
- GIS integration  
- Web-based dashboards  
- Real-time alerts  
