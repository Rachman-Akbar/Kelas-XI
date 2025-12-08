# Aplikasi Monitoring Kelas

A comprehensive Android application for monitoring class activities with role-based access control for different user types.

## Features

### Role-based Functionality

#### Guru (Teacher)
- **Schedule Screen**: Displays teacher schedule with class and day filters
- **Permission Screen**: Shows teacher permissions and allows requesting absence
- **Substitute Teacher Screen**: Shows substitute assignments with date, original and substitute teacher filters

#### Siswa (Student)
- **Class Schedule Screen**: Shows teacher schedules for the student's class with day filter
- **Teacher Attendance Screen**: Shows teacher attendance with add/edit functionality
- **Substitute Teacher List Screen**: Shows substitute teachers for the student's class
- **Student Attendance Screen**: Shows student attendance with ability to mark absent students

#### Kurikulum (Curriculum Administrator)
- **New Permission List Screen**: Shows latest teacher permissions with day filter (with hide option to prevent lag)
- **Substitute Teacher Entry Screen**: Allows entering substitute teachers for absent teachers
- **Absent Teacher List Screen**: Shows teacher absences with day and class filters

#### Kepsek (Principal)
- **Class Schedule List Screen**: Shows class schedules with day and class filters
- **Teacher Attendance List Screen**: Shows teacher attendance with day and class filters
- **Substitute Teacher List Screen**: Shows substitute teacher assignments with day and class filters
- **Student Attendance List Screen**: Shows student attendance with day and class filters
- **Teacher Permission List Screen**: Shows teacher permissions with day and class filters

## Technical Architecture

### Technologies Used
- **Android SDK**: Latest version with API level 36 support
- **Kotlin**: Modern programming language for Android development
- **Jetpack Compose**: Modern UI toolkit for building native Android UI
- **Navigation Component**: AndroidX navigation library for Compose
- **Material Design 3**: Modern design system implementation
- **MVVM Architecture**: Model-View-ViewModel architectural pattern

### Project Structure
```
app/src/main/java/com/komputerkit/aplikasimonitoringkelas/
├── auth/               # Authentication related components
├── common/             # Shared models and utilities
├── guru/               # Teacher role screens and ViewModels
├── siswa/              # Student role screens and ViewModels
├── kurikulum/          # Curriculum admin screens and ViewModels
├── kepsek/             # Principal role screens and ViewModels
├── ui/                 # Theme and common UI components
├── MainActivity.kt     # Main activity entry point
└── Navigation.kt       # App navigation graph
```

### Dependencies
- `androidx.activity:activity-compose`
- `androidx.compose.ui:ui`
- `androidx.compose.material3:material3`
- `androidx.navigation:navigation-compose`
- `androidx.compose.material:material-icons-extended`

## Setup Instructions

1. Clone the repository
2. Open in Android Studio
3. Sync the project to download dependencies
4. Build the project (should compile successfully)
5. Run on an emulator or physical device

## Key Features

- **Modern UI/UX**: Built with Jetpack Compose and Material Design 3
- **Role-based Navigation**: Each role has its own dashboard and navigation flow
- **Filtering System**: All screens have appropriate filters based on requirements
- **Responsive Design**: Adapts to different screen sizes
- **State Management**: Proper MVVM architecture with ViewModel and StateFlow
- **Navigation**: Comprehensive navigation system with proper back stack handling

## Build Status
The application compiles successfully with no errors. Warnings are present for deprecated icon usage but do not affect functionality.

## License
This project is created for educational purposes as part of the Aplikasi Monitoring Kelas project.