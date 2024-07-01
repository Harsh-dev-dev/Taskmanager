# Task Manager App

Task Manager is an Android application designed to help users efficiently manage their tasks. The app allows users to perform CRUD operations (Create, Read, Update, Delete) on tasks, view task statistics on the Dashboard, switch between light and dark modes in Settings, and edit tasks using the Edit screen.

## Summary

The Task Manager app provides a comprehensive solution for task management on Android devices. With features such as adding, editing, and deleting tasks, along with viewing task statistics and customizing the app's appearance, users can stay organized and productive.

## Description

Task Manager is built using modern Android development practices, incorporating components like ViewModel, LiveData, and RecyclerView. The app provides a user-friendly interface to manage tasks, offering functionalities to create new tasks, view and edit existing tasks, and delete tasks when they are no longer needed. The Dashboard offers valuable insights into task statistics, helping users track their productivity. The Settings screen allows users to switch between light and dark themes, enhancing the user experience based on their preferences.

## Features

### CRUD Operations
- **Create Task**: Add new tasks with details such as title, description, due date, and priority.
- **Read Task**: View a list of all tasks.
- **Update Task**: Edit task details.
- **Delete Task**: Remove tasks from the list.

### Dashboard
- View statistics of your tasks, including completed tasks, pending tasks, and task distribution based on priority.

### Settings
- Switch between light and dark modes to customize the appearance of the app.

### Edit Screen
- Edit task details such as title, description, due date, and priority.

## Screens

### Main Activity
- Displays a list of tasks in a RecyclerView.
- Each task card shows the task title, description, due date, and a colored dot indicating priority (red for high, yellow for medium, blue for low).
- Floating Action Button (FAB) to add new tasks.

### Add Task Dialog
- Dialog fragment to input new task details and add them to the list.

### Task Detail Screen
- Shows detailed information about a selected task.
- Provides options to edit or delete the task.

### Dashboard
- Displays various statistics about the user's tasks.

### Settings
- Allows users to switch between light and dark modes.

## Getting Started

### Prerequisites
- Android Studio
- Android device or emulator running Android 5.0 (Lollipop) or higher

### Installation
1. Clone the repository:

