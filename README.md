# Task Manager Application

**Task Manager** is a Java-based application designed to manage tasks of different types:  
regular tasks, epics, and subtasks. 
<br>The system supports in-memory storage, file-based storage, and an HTTP-based key-value server for persistence.  

The application provides a foundation for building REST APIs to interact with tasks, history, and storage managers.

---

## Table of Contents

- [Features](#features)  
- [Project Structure](#project-structure)  
- [Technologies](#technologies)  
- [Getting Started](#getting-started)  
- [Running Tests](#running-tests)  
- [License](#license)  

---

## Features

- **Task Management**: Create, update, delete, and retrieve tasks, epics, and subtasks.  
- **History Tracking**: View recently accessed tasks with an in-memory history manager.  
- **Persistence**:  
  - In-memory task manager.  
  - File-backed task manager.  
  - HTTP-based task manager using a custom KV server and client.  
- **Epic Management**: Epics aggregate subtasks, automatically calculating status, start time, duration, and end time.  
- **Error Handling**: Custom exceptions for save/load operations.  
- **Time Management**: Checking the validity and non-overlap of tasks in time, taking into account the duration.

---

## Project Structure

```
TaskManager/
├── src/
│    └── com/taskmanager/
│        ├── Main.java
│        ├── api/
│        │   ├── Endpoint.java
│        │   ├── HttpTaskServer.java
│        │   ├── KVServer.java
│        │   └── KVTaskClient.java
│        ├── main_manager/
│        │   └── Managers.java
│        ├── managers/
│        │   ├── FileBackedTaskManager.java
│        │   ├── HistoryManager.java
│        │   ├── HttpTaskManager.java
│        │   ├── InMemoryHistoryManager.java
│        │   ├── InMemoryTaskManager.java
│        │   ├── ManagerSaveException.java
│        │   └── TaskManager.java
│        └── tasks/
│            ├── Epic.java
│            ├── Subtask.java
│            ├── Task.java
│            ├── TaskStatus.java
│            └── TaskType.java        
└── tests/
    └── com/taskmanager/
        ├── api/
        │   └── HttpTaskServerTest.java
        ├── managers/
        │   ├── FileBackedTaskManagerTest.java
        │   ├── HistoryManagerTest.java
        │   ├── HttpTaskManagerTest.java
        │   ├── InMemoryHistoryManagerTest.java
        │   ├── InMemoryTaskManagerTest.java
        │   └── TaskManagerTest.java
        └── tasks/
            └── EpicTest.java
```

---

## Technologies

- Java 11+ 
- Gson (JSON serialization/deserialization)  
- JUnit 5 (testing framework)  
- HTTP client/server (custom KV server implementation)  

---

## Getting Started

1. **Clone the repository**  
```bash
git clone git@github.com:Kevin1home/TaskManager.git
cd TaskManager
```

2. **Compile the project**  
Since the project does not use Maven/Gradle, compile manually:  
```bash
javac -d out src/com/taskmanager/**/*.java
```

3. **Run the application**  
```bash
java -cp out com.taskmanager.Main
```

---

## Running Tests

Compile and run JUnit 5 tests manually or using your preferred IDE.  
Example with console (requires junit-platform-console-standalone):  

```bash
java -jar junit-platform-console-standalone.jar -cp out --scan-class-path
```

---

## License

Released under the **MIT License**.
