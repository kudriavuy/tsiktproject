# Classroom App - Learning Management System

## 📋 Project Description

**Classroom App** is a desktop learning management system similar to Google Classroom, designed to facilitate online education and collaboration between teachers and students. The application enables teachers to create groups, assign tasks, manage resources, and communicate with students, while students can view assignments, submit solutions, and receive real-time notifications.

### Target Audience
- **Teachers/Administrators**: Create and manage educational groups, assign tasks, review student submissions, and track progress
- **Students**: Join groups, view assignments, submit solutions, and interact with teachers through comments

### Problem Solved
The application addresses the need for a centralized platform for:
- Organizing educational content and assignments
- Facilitating communication between teachers and students
- Tracking student progress and engagement
- Providing real-time updates on new tasks and activities

---

## 🏗️ System Architecture

### Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         JavaFX Client                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Auth UI    │  │  Group UI    │  │  Profile UI  │         │
│  └──────────────┘  └──────────────┘  └──────────────┘         │
│         │                 │                  │                  │
│         └─────────────────┼──────────────────┘                 │
│                           │                                    │
│                    ┌──────▼──────┐                             │
│                    │  ApiClient  │                             │
│                    └──────┬──────┘                             │
│                           │                                    │
│                    ┌──────▼──────┐                             │
│                    │ WebSocket   │                             │
│                    │   Client    │                             │
│                    └──────┬──────┘                             │
└───────────────────────────┼────────────────────────────────────┘
                            │
                            │ REST API / WebSocket
                            │
┌───────────────────────────▼────────────────────────────────────┐
│                    Spring Boot Server                           │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │              REST Controllers Layer                       │  │
│  │  AuthController │ GroupController │ TaskController       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                           │                                    │
│  ┌────────────────────────▼──────────────────────────────────┐ │
│  │              Service Layer                                 │ │
│  │  AuthService │ GroupService │ TaskService │ Statistics    │ │
│  └────────────────────────┬──────────────────────────────────┘ │
│                           │                                    │
│  ┌────────────────────────▼──────────────────────────────────┐ │
│  │              Repository Layer (JPA/Hibernate)              │ │
│  │  UserRepository │ GroupRepository │ TaskRepository        │ │
│  └────────────────────────┬──────────────────────────────────┘ │
│                           │                                    │
│  ┌────────────────────────▼──────────────────────────────────┐ │
│  │              WebSocket Handler                             │ │
│  │         NotificationWebSocketHandler                       │ │
│  └────────────────────────────────────────────────────────────┘ │
└───────────────────────────┬────────────────────────────────────┘
                            │
                            │ JDBC
                            │
┌───────────────────────────▼────────────────────────────────────┐
│                      SQLite Database                            │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │  users   │  │  groups  │  │  tasks   │  │memberships│      │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘      │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                     │
│  │comments  │  │resources │  │activity_ │                     │
│  │          │  │          │  │  logs    │                     │
│  └──────────┘  └──────────┘  └──────────┘                     │
└────────────────────────────────────────────────────────────────┘
```

### Layer Description

1. **Presentation Layer (JavaFX Client)**
   - **AuthController**: Handles user authentication UI (login, registration, OAuth2)
   - **GroupController**: Main screen displaying user groups and tasks
   - **GroupDetailsController**: Detailed view of a group with tasks, members, and statistics
   - **ProfileController**: User profile management
   - **ApiClient**: HTTP client for REST API communication
   - **WebSocketClient**: Real-time notification handling

2. **Application Layer (Spring Boot Server)**
   - **Controllers**: REST endpoints for API operations
   - **Services**: Business logic and validation
   - **Repositories**: Data access layer using JPA/Hibernate
   - **Security**: JWT authentication and OAuth2 integration
   - **WebSocket**: Real-time notification broadcasting

3. **Data Layer (SQLite Database)**
   - Relational database storing all application data
   - Managed by Hibernate ORM with automatic schema generation

---

## 🗄️ Database Model (ER Diagram)

### Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│    User     │         │  Membership  │         │   Group     │
├─────────────┤         ├──────────────┤         ├─────────────┤
│ user_id (PK)│◄──┐  ┌──│membership_id│──┐  ┌──►│ group_id(PK)│
│ name        │  │  │  │ user_id (FK)│  │  │   │ name        │
│ email       │  │  │  │ group_id(FK)│  │  │   │ description │
│ password    │  │  │  │ role        │  │  │   │ created_by  │
│ photo_url   │  │  │  │ joined_at   │  │  │   │ created_at  │
│ bio         │  │  │  └──────────────┘  │  │   └─────────────┘
│ phone       │  │  │                    │  │
│ created_at  │  │  │                    │  │
│ updated_at  │  │  │                    │  │
└─────────────┘  │  │                    │  │
                 │  │                    │  │
┌─────────────┐  │  │                    │  │   ┌─────────────┐
│   Comment   │  │  │                    │  │   │   Resource  │
├─────────────┤  │  │                    │  │   ├─────────────┤
│comment_id  │  │  │                    │  │   │resource_id  │
│ task_id(FK)│──┼──┼────────────────────┼──┼──►│ group_id(FK)│
│ user_id(FK)│──┘  │                    │  │   │ task_id(FK) │
│ text       │     │                    │  │   │ uploaded_by │
│ created_at │     │                    │  │   │ title       │
└─────────────┘     │                    │  │   │ type        │
                    │                    │  │   │ path_or_url │
┌─────────────┐     │                    │  │   │ uploaded_at │
│    Task     │     │                    │  │   └─────────────┘
├─────────────┤     │                    │  │
│ task_id(PK) │     │                    │  │
│ group_id(FK)├─────┘                    │  │
│ created_by  ├──────────────────────────┘  │
│ title       │                              │
│ description │                              │
│ status      │                              │
│ deadline    │                              │
│ solution_*  │                              │
│ created_at  │                              │
└─────────────┘                              │
                                             │
┌─────────────┐                              │
│ ActivityLog │                              │
├─────────────┤                              │
│ log_id(PK)  │                              │
│ user_id(FK)├───────────────────────────────┘
│ action      │
│ details     │
│ timestamp   │
└─────────────┘
```

### Database Schema

#### Users Table
- **user_id** (PK): Unique identifier
- **name**: User's full name
- **email**: Unique email address
- **password_hash**: BCrypt hashed password
- **profile_photo_url**: URL to profile photo
- **bio**: User biography
- **phone**: Contact phone number
- **created_at**, **updated_at**: Timestamps

#### Groups Table
- **group_id** (PK): Unique identifier
- **name**: Group name
- **description**: Group description
- **created_by** (FK → users.user_id): Creator user ID
- **created_at**: Creation timestamp

#### Memberships Table
- **membership_id** (PK): Unique identifier
- **user_id** (FK → users.user_id): Member user ID
- **group_id** (FK → groups.group_id): Group ID
- **role**: TEACHER or STUDENT
- **joined_at**: Join timestamp

#### Tasks Table
- **task_id** (PK): Unique identifier
- **group_id** (FK → groups.group_id): Parent group
- **created_by** (FK → users.user_id): Creator user ID
- **title**: Task title
- **description**: Task description
- **status**: OPEN, IN_PROGRESS, or COMPLETED
- **deadline**: Task deadline
- **solution_url**: URL to solution file
- **solution_text**: Text solution
- **solution_uploaded_by** (FK → users.user_id): Student who submitted
- **solution_uploaded_at**: Submission timestamp
- **created_at**: Creation timestamp

#### Comments Table
- **comment_id** (PK): Unique identifier
- **task_id** (FK → tasks.task_id): Parent task
- **user_id** (FK → users.user_id): Comment author
- **text**: Comment content
- **created_at**: Creation timestamp

#### Resources Table
- **resource_id** (PK): Unique identifier
- **group_id** (FK → groups.group_id): Parent group
- **task_id** (FK → tasks.task_id): Optional parent task
- **uploaded_by** (FK → users.user_id): Uploader user ID
- **title**: Resource title
- **type**: FILE or LINK
- **path_or_url**: File path or URL
- **uploaded_at**: Upload timestamp

#### Activity Logs Table
- **log_id** (PK): Unique identifier
- **user_id** (FK → users.user_id): User who performed action
- **action**: Action type (e.g., "TASK_CREATED", "COMMENT_ADDED")
- **details**: Additional action details
- **timestamp**: Action timestamp

---

## 🔌 REST API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
All endpoints (except auth endpoints) require JWT token in the `Authorization` header:
```
Authorization: Bearer <token>
```

---

### Authentication Endpoints

#### POST `/api/auth/register`
Register a new user.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "userId": 1,
    "name": "John Doe",
    "email": "john@example.com"
  }
}
```

#### POST `/api/auth/login`
Authenticate user and get JWT token.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Response:** Same as register endpoint.

---

### OAuth2 Endpoints

#### GET `/api/oauth2/authorize-url`
Get Google OAuth2 authorization URL.

**Response:**
```json
{
  "url": "http://localhost:8080/oauth2/authorization/google"
}
```

#### GET `/api/oauth2/user`
Get OAuth2 authenticated user information.

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { ... }
}
```

---

### Group Endpoints

#### POST `/api/groups`
Create a new group.

**Request Body:**
```json
{
  "name": "Mathematics 101",
  "description": "Introduction to Mathematics",
  "createdBy": 1
}
```

**Response:**
```json
{
  "groupId": 1,
  "name": "Mathematics 101",
  "description": "Introduction to Mathematics",
  "createdBy": 1,
  "createdAt": "2024-01-15T10:00:00"
}
```

#### GET `/api/groups/{groupId}`
Get group by ID.

**Response:** Group object

#### GET `/api/groups/user/{userId}`
Get all groups for a user.

**Response:** Array of Group objects

#### GET `/api/groups`
Get all available groups.

**Response:** Array of Group objects

#### POST `/api/groups/{groupId}/members`
Add a member to a group.

**Request Body:**
```json
{
  "addedByUserId": 1,
  "userId": 2,
  "role": "STUDENT"
}
```

**Response:** Membership object

#### GET `/api/groups/{groupId}/members`
Get all members of a group.

**Response:** Array of Membership objects

#### PUT `/api/groups/{groupId}`
Update group information.

**Request Body:**
```json
{
  "userId": 1,
  "name": "Updated Name",
  "description": "Updated Description"
}
```

**Response:** Updated Group object

---

### Task Endpoints

#### POST `/api/tasks`
Create a new task.

**Request Body:**
```json
{
  "groupId": 1,
  "createdBy": 1,
  "title": "Homework Assignment",
  "description": "Complete exercises 1-10",
  "deadline": "2024-01-20T23:59:59"
}
```

**Response:** Task object

#### GET `/api/tasks/{taskId}`
Get task by ID.

**Response:** Task object

#### GET `/api/tasks/group/{groupId}`
Get all tasks for a group.

**Response:** Array of Task objects

#### PUT `/api/tasks/{taskId}/status`
Update task status.

**Request Body:**
```json
{
  "status": "IN_PROGRESS"
}
```

**Response:** Updated Task object

#### PUT `/api/tasks/{taskId}`
Update task information.

**Request Body:**
```json
{
  "userId": 1,
  "title": "Updated Title",
  "description": "Updated Description",
  "deadline": "2024-01-25T23:59:59"
}
```

**Response:** Updated Task object

#### DELETE `/api/tasks/{taskId}`
Delete a task.

**Response:** 200 OK

#### POST `/api/tasks/{taskId}/solution`
Submit or add a solution to a task.

**Request Body:**
```json
{
  "userId": 2,
  "solutionText": "My solution explanation",
  "solutionUrl": "https://example.com/solution.pdf"
}
```

**Response:** Updated Task object with solution

#### PUT `/api/tasks/{taskId}/solution`
Update an existing solution.

**Request Body:** Same as POST

**Response:** Updated Task object

#### POST `/api/tasks/{taskId}/solution/approve`
Approve a solution (teacher only).

**Request Body:**
```json
{
  "teacherId": 1
}
```

**Response:** Task object with status set to COMPLETED

#### GET `/api/tasks/{taskId}/is-teacher`
Check if user is a teacher for the task's group.

**Query Parameters:**
- `userId`: User ID to check

**Response:**
```json
{
  "isTeacher": true
}
```

---

### Comment Endpoints

#### POST `/api/comments`
Create a new comment.

**Request Body:**
```json
{
  "taskId": 1,
  "userId": 2,
  "text": "Great work!"
}
```

**Response:** Comment object

#### GET `/api/comments/task/{taskId}`
Get all comments for a task.

**Response:** Array of Comment objects

#### DELETE `/api/comments/{commentId}`
Delete a comment.

**Query Parameters:**
- `userId`: User ID (must be comment author or teacher)

**Response:** 200 OK

---

### Resource Endpoints

#### POST `/api/resources`
Create a new resource.

**Request Body:**
```json
{
  "groupId": 1,
  "taskId": 1,
  "uploadedBy": 1,
  "title": "Lecture Notes",
  "type": "FILE",
  "pathOrUrl": "/uploads/resources/notes.pdf"
}
```

**Response:** Resource object

#### GET `/api/resources/group/{groupId}`
Get all resources for a group.

**Response:** Array of Resource objects

#### GET `/api/resources/task/{taskId}`
Get all resources for a task.

**Response:** Array of Resource objects

#### DELETE `/api/resources/{resourceId}`
Delete a resource.

**Response:** 200 OK

---

### Profile Endpoints

#### GET `/api/users/{userId}/profile`
Get user profile.

**Response:** User object

#### PUT `/api/users/{userId}/profile`
Update user profile.

**Request Body:**
```json
{
  "name": "John Doe",
  "bio": "Mathematics teacher",
  "phone": "+1234567890"
}
```

**Response:**
```json
{
  "success": true,
  "user": { ... }
}
```

#### POST `/api/users/{userId}/profile/photo`
Upload profile photo.

**Request:** Multipart form data with `file` field

**Response:**
```json
{
  "success": true,
  "user": { ... },
  "photoUrl": "/uploads/profiles/photo.jpg"
}
```

#### DELETE `/api/users/{userId}/profile/photo`
Delete profile photo.

**Response:**
```json
{
  "success": true,
  "message": "Profile photo deleted"
}
```

#### GET `/api/users/uploads/profiles/{filename}`
Get profile photo file.

**Response:** Image file

---

### Statistics Endpoints

#### GET `/api/statistics/group/{groupId}/tasks`
Get task statistics for a group.

**Response:**
```json
{
  "totalTasks": 10,
  "openTasks": 3,
  "inProgressTasks": 2,
  "completedTasks": 5,
  "tasksByDate": { ... },
  "completedByDate": { ... }
}
```

#### GET `/api/statistics/group/{groupId}/engagement`
Get engagement statistics for a group.

**Response:**
```json
{
  "totalMembers": 25,
  "teachers": 2,
  "students": 23,
  "activeMembers": 18,
  "engagementRate": 72.0,
  "memberTaskCount": { ... }
}
```

#### GET `/api/statistics/group/{groupId}/overall`
Get overall statistics for a group.

**Response:**
```json
{
  "taskStatistics": { ... },
  "engagementStatistics": { ... }
}
```

#### GET `/api/statistics/user/{userId}/activity`
Get user activity statistics.

**Response:**
```json
{
  "totalActivities": 50,
  "activityByDate": { ... },
  "activityByType": { ... },
  "recentActivities": [ ... ]
}
```

---

### User Endpoints

#### GET `/api/users`
Get all users.

**Response:** Array of User objects

#### GET `/api/users/{userId}`
Get user by ID.

**Response:** User object

---

## 🔔 WebSocket Documentation

### Connection
```
ws://localhost:8080/notifications
```

### Message Format
Notifications are sent as JSON messages:

```json
{
  "type": "TASK_CREATED",
  "message": "New task: Homework Assignment",
  "groupId": 1,
  "userId": 1
}
```

### Notification Types
- `TASK_CREATED`: New task created in a group
- `MEMBER_ADDED`: New member added to a group
- `COMMENT_ADDED`: New comment added to a task
- `SOLUTION_ADDED`: Solution submitted for a task
- `SOLUTION_APPROVED`: Solution approved by teacher

### Client Implementation
The JavaFX client automatically connects to the WebSocket server after successful authentication and displays notifications in real-time in the notifications panel.

---

## 🖥️ User Interface Examples

### Authentication Screen
- **Minimalist design** with black and white color scheme
- **Sign Up / Sign In** toggle on the same screen
- **Google OAuth2** integration button
- Form fields: Name (sign up only), Email, Password

### Main Screen (GroupController)
- **Dark left sidebar** with navigation:
  - Tasks
  - Statistics
  - Groups
- **Light content area** displaying:
  - Task cards with status, deadline, and group name
  - Group cards with name, description, and member badge
  - Personal statistics (total, completed, open tasks)
- **Notifications panel** on the right showing real-time updates

### Group Details Screen (GroupDetailsController)
- **Top bar** with back button and group name
- **Three tabs**:
  - **Tasks**: Table view with columns (Title, Status, Deadline, Created, Actions)
  - **Members**: Square cards with user photos, names, and roles
  - **Statistics**: Charts and graphs showing group performance
- **Task details view**: Opens in the same window showing:
  - Task description
  - Resources (files and links)
  - Solution section (for students to submit)
  - Comments section (for communication)

### Profile Screen (ProfileController)
- **Centered container** with border (similar to login screen)
- **Profile photo** with placeholder icon if not set
- **Editable fields**: Name, Bio, Phone
- **Photo upload** functionality

### User Flow
1. **Registration/Login**: User creates account or logs in (traditional or OAuth2)
2. **Main Screen**: User sees their tasks and groups
3. **Group Interaction**: User can view group details, tasks, and members
4. **Task Management**: 
   - Teachers create and edit tasks
   - Students view tasks and submit solutions
   - Teachers approve solutions
5. **Communication**: Users can comment on tasks
6. **Real-time Updates**: Notifications appear automatically for new activities

---

## 🛠️ Technical Challenges and Solutions

### 1. Authentication and Authorization

**Challenge**: Implementing secure authentication with JWT tokens and OAuth2 integration.

**Solution**:
- Implemented JWT token generation and validation using `JwtUtil`
- Created `JwtAuthenticationFilter` to intercept requests and validate tokens
- Integrated Spring Security OAuth2 for Google login
- Stored tokens locally in `~/.classroom_token` for persistence
- Implemented automatic token retrieval from OAuth2 success page

**Key Code**:
```java
// JWT Token Generation
String token = jwtUtil.generateToken(user.getEmail());

// Token Validation in Filter
String token = extractToken(request);
if (token != null && jwtUtil.validateToken(token)) {
    // Set authentication
}
```

### 2. Real-time Notifications

**Challenge**: Implementing WebSocket for real-time notifications in JavaFX client.

**Solution**:
- Created custom `NotificationWebSocketHandler` extending `TextWebSocketHandler`
- Implemented `WebSocketClient` using OkHttp for JavaFX client
- Used `Platform.runLater()` to update UI from WebSocket thread
- Broadcast notifications to all connected clients when events occur

**Key Code**:
```java
// Server-side notification sending
webSocketHandler.sendNotificationToAll(notificationJson);

// Client-side WebSocket connection
WebSocketClient.connect(this::handleNotification);
```

### 3. Role-Based Access Control

**Challenge**: Ensuring only teachers can create tasks and add members, while students can submit solutions.

**Solution**:
- Implemented `Membership` entity with `TEACHER` and `STUDENT` roles
- Added authorization checks in service layer methods
- Created `isUserTeacher()` helper methods
- Threw `RuntimeException` with descriptive messages for unauthorized actions

**Key Code**:
```java
// Check if user is teacher
Optional<Membership> membership = membershipRepository
    .findByUserIdAndGroupId(userId, groupId);
if (membership.isEmpty() || 
    membership.get().getRole() != Membership.Role.TEACHER) {
    throw new RuntimeException("Only teacher can create tasks");
}
```

### 4. File Upload and Storage

**Challenge**: Handling profile photo uploads and storing them securely.

**Solution**:
- Created `uploads/profiles/` directory structure
- Generated unique filenames using UUID
- Validated file types (images only)
- Handled file deletion when updating photos
- Served files through REST endpoint with proper content types

**Key Code**:
```java
// File upload
String filename = UUID.randomUUID().toString() + extension;
Path filePath = uploadPath.resolve(filename);
Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
```

### 5. OAuth2 Integration Issues

**Challenge**: Handling OAuth2 redirect URIs and error scenarios.

**Solution**:
- Created `OAuth2SuccessController` to display token on success page
- Implemented `OAuth2ErrorController` with detailed error messages
- Used WebView in JavaFX to intercept OAuth2 callback
- Extracted token from HTML page using JavaScript execution

**Key Code**:
```java
// Extract token from OAuth2 success page
Object tokenObj = webEngine.executeScript(
    "document.getElementById('token') ? " +
    "document.getElementById('token').value : null;"
);
```

### 6. UI State Management

**Challenge**: Managing complex UI state in JavaFX with multiple screens and real-time updates.

**Solution**:
- Created `MainController` as central navigation hub
- Used `BorderPane` for layout structure
- Implemented content area swapping for different views
- Refreshed data after mutations (create, update, delete)

**Key Code**:
```java
// Update UI after task creation
loadTasks(); // Refresh task list
showTaskDetailsDialog(taskId); // Show updated details
```

### 7. Statistics Calculation

**Challenge**: Calculating complex statistics efficiently.

**Solution**:
- Created `StatisticsService` with separate methods for different statistics
- Used Java Streams API for data aggregation
- Implemented date-based filtering for time-series data
- Cached results where appropriate

**Key Code**:
```java
// Calculate task statistics
long completedTasks = tasks.stream()
    .filter(t -> t.getStatus() == Task.TaskStatus.COMPLETED)
    .count();
```

---

## 🤖 AI Assistance Evaluation

### What AI Helped With

1. **Initial Project Structure**: AI suggested the overall architecture (JavaFX client + Spring Boot server + SQLite)
2. **OAuth2 Integration**: AI provided guidance on Spring Security OAuth2 configuration
3. **WebSocket Implementation**: AI helped with WebSocket handler setup and client connection
4. **UI Design Patterns**: AI suggested minimalist design approach and component structure
5. **Error Handling**: AI provided examples of exception handling and user-friendly error messages

### What Required Manual Work

1. **Business Logic**: All service layer logic was manually implemented based on requirements
2. **Database Schema**: Entity relationships and field definitions were manually designed
3. **UI Layout**: Specific JavaFX layouts and styling were manually crafted
4. **Integration Issues**: OAuth2 redirect URI problems and WebSocket connection issues required manual debugging
5. **Code Translation**: Translation from Ukrainian to English was done manually
6. **Testing**: All functionality testing and bug fixes were done manually

### Lessons Learned

1. **AI is Great for Structure, Not Details**: AI provided good architectural guidance but specific implementation details required manual work
2. **Debugging Requires Human Insight**: Complex integration issues (OAuth2, WebSocket) required understanding the full context
3. **Code Quality**: AI-generated code often needed refactoring for maintainability and consistency
4. **Documentation**: While AI can help generate documentation, it requires human review for accuracy
5. **Best Practices**: Following Spring Boot and JavaFX best practices required manual research and implementation

### AI Tools Used
- **ChatGPT/Cursor**: For code generation, architecture suggestions, and debugging help
- **GitHub Copilot**: For code completion and suggestions

### Final Assessment
AI was extremely helpful for:
- Getting started quickly
- Understanding complex frameworks (Spring Security, OAuth2)
- Generating boilerplate code
- Providing examples and patterns

However, significant manual work was required for:
- Business logic implementation
- UI/UX design and refinement
- Integration and debugging
- Code quality and consistency
- Project-specific requirements

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- SQLite (embedded, no installation needed)

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd tsiktpr
```

2. **Configure OAuth2 (Optional)**
Edit `src/main/resources/application.properties`:
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the server**
```bash
mvn spring-boot:run
```

5. **Run the client**
```bash
mvn javafx:run
```

### Default Configuration
- Server runs on `http://localhost:8080`
- Database file: `classroom.db` (created automatically)
- JWT secret: Configured in `application.properties`

---

## 📝 License

This project is for educational purposes.

---

## 👥 Authors

Developed as a learning management system project.

---

## 🙏 Acknowledgments

- Spring Boot framework
- JavaFX for desktop UI
- SQLite for database
- Google OAuth2 for authentication
