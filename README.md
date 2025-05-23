# 🛠️ Task App

This Android application connects to a remote API to fetch a list of tasks, stores them locally for offline access and presents them in a user-friendly, searchable UI. It also provides QR code scanning and periodic background updates to ensure the data stays fresh.

You can read more about the [requirements needed here](https://github.com/kanake10/android-task/blob/master/docs/requirements.md).
## 🎯 Objective

The goal of this app is to:

- Connect to the [BauBuddy API](https://api.baubuddy.de/dev/index.php/v1/tasks/select) with proper authentication.
- Fetch and display a list of tasks.
- Store data locally for offline usage.
- Allow users to search tasks by any of their properties.
- Scan QR codes to initiate a search.
- Keep data up-to-date using pull-to-refresh and background syncing.

---

## ✅ Features Implemented

- **Authentication** with OAuth2 using a login POST request and Bearer token authorization for subsequent API requests.
- **Fetch tasks** from a secured remote API.
- **Local storage** of fetched data for offline availability.
- **List view** to display:
  - Task name
  - Title
  - Description
  - A color block representing the `colorCode` property.
- **Search bar** in the menu:
  - Filters tasks by any property (even non-visible ones).
- **QR Code scanning**:
  - Automatically sets the scanned text as the current search query.
- **Swipe-to-refresh** to manually reload the data.
- **Background worker** that syncs tasks from the server every 60 minutes.

---

## 🧰 Technologies Used

- [**Kotlin**](https://kotlinlang.org/) – Modern, expressive, statically typed language for Android development.
- [**Jetpack Compose**](https://developer.android.com/jetpack/compose) – Declarative UI toolkit for building native Android apps.
- [**Retrofit**](https://square.github.io/retrofit/) – Type-safe HTTP client for Android and Java.
- [**Room**](https://developer.android.com/training/data-storage/room) – Persistence library for SQLite database access using an abstraction layer.
- [**Kotlin Coroutines**](https://kotlinlang.org/docs/coroutines-overview.html) – Asynchronous programming with support for structured concurrency.
- [**Firebase Crashlytics**](https://firebase.google.com/docs/crashlytics) – Real-time crash reporting and diagnostics.
- [**Firebase Analytics**](https://firebase.google.com/docs/analytics) – Tracks user behavior and events for performance and marketing insights.
- [**Hilt**](https://developer.android.com/training/dependency-injection/hilt-android) – Dependency injection framework built on top of Dagger for Android.
- [**WorkManager**](https://developer.android.com/topic/libraries/architecture/workmanager) – API for deferrable, asynchronous background tasks with guaranteed execution.
- [**Gson**](https://github.com/google/gson) – Library to convert Java/Kotlin objects to/from JSON.
- [**OkHttp3**](https://square.github.io/okhttp/) – HTTP client for making network requests with features like connection pooling and interceptors.
- [**DataStore**](https://developer.android.com/topic/libraries/architecture/datastore) – Modern data storage solution to store key-value pairs or typed objects.
- [**Material 3 (Jetpack Compose)**](https://developer.android.com/jetpack/compose/themes/material3) – Modern design system for building beautiful, accessible UIs.
- [**GitHub Actions**](https://docs.github.com/en/actions) – Automation platform for CI/CD directly within GitHub.
- [**LeakCanary**](https://square.github.io/leakcanary/) – Memory leak detection library for Android.
- [**KSP (Kotlin Symbol Processing)**](https://github.com/google/ksp) – Kotlin compiler plugin for annotation processing.
- [**CameraX**](https://developer.android.com/training/camerax) – Jetpack library for accessing and managing the device’s camera.
- [**ML Kit**](https://developers.google.com/ml-kit) – Mobile SDK for on-device machine learning including barcode scanning.
- [**JUnit**](https://junit.org/junit5/) – Framework for writing and running unit tests.
- [**Turbine**](https://github.com/cashapp/turbine) – Tool for testing Kotlin Flow emissions.
- [**Robolectric**](http://robolectric.org/) – Testing framework that runs Android tests on the JVM.
- [**kotlinx-coroutines-test**](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/) – Coroutine testing support for Kotlin.
- [**Timber**](https://github.com/JakeWharton/timber) – Logging library that wraps the Android Log class with a tree-based API.
- [**MockK**](https://mockk.io/) – Kotlin-specific mocking library for unit tests.
- [**MockWebServer**](https://github.com/square/okhttp/tree/master/mockwebserver) – Web server for testing Retrofit and OkHttp clients.

## 🧪 Testing Approach

This project uses a layered and modular testing strategy to ensure reliability, correctness and maintainability across the app’s architecture. Here’s how different components are tested:

- **ViewModels**  
  Tested in isolation using **fake repositories** and **JUnit** for assertions. This ensures business logic and state management behave as expected.

- **Repositories**  
  **MockK** is used to mock dependencies and test repository logic independently of data sources.

- **DAO Layer**  
  **Robolectric** enables running Room database tests directly on the JVM, ensuring fast and reliable database testing without the need for an emulator or device.

- **API Layer**  
  **MockWebServer** simulates API responses, allowing for testing Retrofit service interfaces and network behavior without hitting the real API.

- **UI Components (Jetpack Compose)**  
  UI elements such as the **Search Bar**, **Top App Bar**, and **Task List Item** are tested using `ComposeTestRule` to validate their behavior, state changes and interaction flows.

## 📲 UI Automation with Maestro

To enhance UI reliability and simulate real-world usage scenarios, this project uses [**Maestro**](https://maestro.mobile.dev/) for end-to-end testing.

Two YAML-based flow tests are included under the `.maestro/` directory:

1. **Offline Test:**  
   **File:** `.maestro/task_app_offline.yaml`  
   - Simulates app launch with Airplane Mode **enabled**.  
   - Verifies appropriate error messages are shown and that no task data is loaded.

2. **Online Test:**  
   **File:** `.maestro/task_app_flow.yaml`  
   - Simulates normal app behavior with internet access.  
   - Validates UI interactions such as:
     - Viewing and filtering task lists

NB: I need to research whether there is a way to streamline this with a single test suite if Maestro gains support for toggling connectivity states.

## 📷 App's Preview

Here is how the app look like in action:
Task List | Search Task | Camera
--- | --- | ---
<img src="https://github.com/kanake10/android-task/blob/master/screenshots/task-list.png" width="280"/> | <img src="https://github.com/kanake10/android-task/blob/master/screenshots/search-color.png" width="280"/> | <img src="https://github.com/kanake10/android-task/blob/master/screenshots/camera.png" width="280"/>
