uChattin – A Non-Verbal Communication App
**Capstone Project – CSC311: Advanced Programming**  
**Farmingdale State College**

Overview
**uChattin** is a desktop-based communication application built to support non-verbal individuals in expressing themselves effectively. 
The app features a customizable image grid interface, enabling users to construct sentences through image selection. 
These sentences are then spoken aloud using ARAsaac Text to Speech technology, giving a voice to those who need it.

This project was developed by a team of five students following Agile development practices. It emphasizes accessibility, usability, and personalization.

Goals
- Deliver a simple, intuitive interface for non-verbal users.
- Enable sentence creation via image selection.
- Vocalize messages using built-in, offline text-to-speech.
- Allow caregivers, educators, and family members to customize the experience.
- Store user preferences and data securely using local or cloud-based storage (excluding Firebase).

Key Features

Picture Grid Interface
- Dynamic `GridPane` layout using JavaFX, categorized by context (e.g., food, actions, emotions).
- Users construct sentences by clicking images.
- Category navigation via tabs or sidebar.

Text-to-Speech
- Converts image selections into spoken sentences.
- Uses ARAsaac Text to Speech

User Personalization
- Customizable image sets per user.
- Admin interface for creating and managing categories, images, and phrases.

Data Storage & Security
- Secure user data handling via encrypted local or Azure-hosted database solutions.
- Azure Key Vault removed; sensitive info is handled securely using local encryption protocols.

Technologies Used
- **Frontend**: JavaFX / FXML / CSS
- **Backend**: Java 23
- **Text-to-Speech**: ARAsaac Text to Speech
- **Data Storage**: Azure SQL 
- **Build Tool**: Maven
- **IDE**: IntelliJ IDEA

Team Members
- **Joseph Nunez** – Developer, Project Manager  
- **Nadeige Eugene** – Developer, UX/UI  
- **Eduardo Escobar** – Fullstack Developer, GitHub Administrator
- **Khayalamakhosi Dlamini** – Fullstack Developer, Style & Theme Engineer
- **Ahnaf Sindid** – Fullstack Developer, Database Admin

Current Status
The project is in active development (Spring 2025). The team is currently:
- Integrating ARAsaac Text to Speech
- Refining UI based on feedback from accessibility specialists
- Enhancing the admin customization interface

Future Plans
- Export/share sentence history for caregiver insights.
- Implement predictive phrase suggestions using NLP.
- Add cross-platform (Android/iOS) support.
- Support for multiple voices and languages.

Repository
GitHub: [EduardoEsc0bar/UChattinCapstoneProject](https://github.com/EduardoEsc0bar/UChattinCapstoneProject)
