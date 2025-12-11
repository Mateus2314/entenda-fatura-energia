# Understand Your Electricity Bill ⚡

## 📌 Description
The **Understand Your Electricity Bill** project aims to help consumers better understand the charges in their electricity bills.  
The web and mobile application will allow users to input basic bill data and, by integrating with the ANEEL API, generate an explanatory PDF report.  

This report will show:
- How the charges were calculated.
- Whether they match the official tariffs.
- Practical suggestions for saving energy.

## 🚀 Technologies
- **Backend:** Java + Spring Boot  
- **Frontend:** React  
- **Database:** PostgreSQL (for future history storage)  
- **PDF:** Apache PDFBox or iText  
- **Testing:** JUnit/Mockito + Robot Framework  
- **Infrastructure:** Docker  

## 🎯 Goal
Deliver a functional MVP that allows users to:
1. Fill out a form with bill data.  
2. Query official tariffs via ANEEL API.  
3. Generate a downloadable PDF report.  

## 📂 Repository Structure
- `backend/` → Java code (Spring Boot)  
- `frontend/` → React code  
- `tests/` → Robot Framework scenarios  
- `docs/` → Documentation (scope, requirements, sprints, architecture)  