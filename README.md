# FarmInsight - Mobile (Native Android App)

This repository contains the native Android application for the **FarmInsight** platform, a project developed for my Bachelor's Thesis. The app is designed for field workers, providing them with essential tools to perform their daily activities.

## Overview

The FarmInsight mobile app provides farm workers with quick and efficient access to their assigned tasks and relevant farm information. It facilitates seamless communication and real-time reporting from the field, bridging the gap between managers and on-the-ground personnel.

### Key Features
*   **Secure Login:** Authentication against the FarmInsight backend.
*   **Task List:** A clear, grouped list of assigned tasks (To Do, In Progress, Completed).
*   **Task Details:** View detailed information for each task, including a map showing the target plot.
*   **Status Updates:** Easily change the status of a task, which instantly notifies the manager.
*   **Problem Reporting:** Create new reports with a title, description, and an attached photo directly from the phone's camera or gallery.
*   **Real-time Notifications:** Receive push notifications for new tasks or updates on reports via SignalR.
*   **User Profile:** View personal details and role information.

### Technology Stack & Architecture

*   **Language:** **Kotlin**
*   **UI Toolkit:** **Jetpack Compose** for a fully declarative UI.
*   **Architecture:** **MVVM (Model-View-ViewModel)** to separate UI from business logic.
*   **Asynchronicity:** **Kotlin Coroutines** and **StateFlow** for managing asynchronous operations and UI state.
*   **Navigation:** **Jetpack Navigation Compose** for handling in-app navigation.
*   **Networking:** **Ktor Client** for making REST API calls to the backend.
*   **Real-time Communication:** **SignalR** Java Client.
*   **Dependency Injection:** **Hilt** for managing dependencies.

## Getting Started

### Prerequisites
*   Android Studio (latest version)
*   An Android Virtual Device (AVD) or a physical Android device.

### Installation & Running
1.  **Clone the repository:**
    ```sh
    git clone https://github.com/ccaesar26/FarmMobileApp
    ```
2.  **Open in Android Studio:**
    Open the cloned project folder in Android Studio. It will automatically sync the Gradle files.
3.  **Configure Backend URL:**
    You will need to configure the base URL for the backend API Gateway. This is typically located in a constants file or a network configuration module within the source code.
4.  **Build and Run:**
    Select a target device (emulator or physical) and click the 'Run' button in Android Studio. The app will be built and installed on the selected device.
