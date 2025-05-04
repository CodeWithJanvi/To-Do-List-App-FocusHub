# 📱 FocusHub/To-Do-List-App

**FocusHub/To-Do-List-App** is a simple and intuitive task management Android application built with **Kotlin**, designed especially with older adults in mind. It helps users stay focused, organized, and on top of their tasks with features like adding, editing, deleting, completing tasks, and viewing them by date on a calendar.

---

## 🌟 Features

- ✅ Add, edit, delete tasks easily.
- 🗂 View tasks as **Pending** or **Completed**.
- 🗓 Calendar-wise task filtering.
- ⭐ Mark important tasks as Starred.
- ✔️ Mark tasks as complete with a single tap.
- 🔄 Automatically move completed tasks to a separate view.
- 👵 User-friendly interface optimized for older adults.

---

## 📂 Project Structure

FocusHubToDo/
├── app/
│ ├── src/
│ │ ├── main/
│ │ │ ├── java/com/example/focushub/
│ │ │ │ ├── activities/ # All Activity files (MainActivity, CalendarActivity, etc.)
│ │ │ │ ├── adapters/ # TaskAdapter and other custom adapters
│ │ │ │ ├── database/ # SQLite DatabaseHelper
│ │ │ │ ├── models/ # Task and User data classes
│ │ │ │ └── utils/ # Utility classes and constants
│ │ │ ├── res/
│ │ │ │ ├── layout/ # XML layout files
│ │ │ │ ├── drawable/ # Images and shape drawables
│ │ │ │ └── values/ # Strings, styles, themes
│ │ │ └── AndroidManifest.xml
├── build.gradle
└── README.md

---

## 🛠 Built With

- **Kotlin** – Modern Android development language
- **SQLite** – Local database for task storage
- **ConstraintLayout & CardView** – UI building blocks
- **Android CalendarView** – For date-based task filtering

---

## 📸 Screenshots

| Home (Pending Tasks,edit /delete task) | Completed Tasks | Calendar View | About Us |
|----------------------|------------------|----------------|
| ![screenshot1](screenshots/home.png) | ![screenshot2](screenshots/completed.png) | ![screenshot3](screenshots/calendar.png) |

> 📌 Place your actual screenshots in a `screenshots/` folder inside the project directory.

---

## 🚀 Getting Started

To run this project locally:

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/FocusHubToDo.git

