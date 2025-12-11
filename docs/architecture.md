\# Project Architecture



\## 🏗️ Overview

The application consists of:

\- \*\*Frontend (React):\*\* user interface and bill data input.

\- \*\*Backend (Spring Boot):\*\* processing, ANEEL API integration, and report generation.

\- \*\*ANEEL API:\*\* official source of tariffs and flags.

\- \*\*PDF Generator:\*\* backend module for report creation.

\- \*\*Database (PostgreSQL):\*\* optional in MVP, used later for report history.



\## 🔄 Data Flow

1\. User inputs bill data in frontend form.

2\. Frontend sends data to backend via REST API.

3\. Backend queries ANEEL API.

4\. Backend processes and validates data.

5\. Backend generates PDF report.

6\. Frontend provides download option to user.



\## 📐 Simplified Diagram

\[Frontend React] ---> \[Backend Spring Boot] ---> \[ANEEL API] |                       | |                       v |                  \[PDF Generator] |                       | v                       v \[User] <--- Download --- \[PDF Report]

