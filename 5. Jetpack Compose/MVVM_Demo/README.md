# MVVM Architecture Demo with Jetpack Compose and LiveData

This Android application demonstrates the **Model-View-ViewModel (MVVM)** architecture pattern using **Jetpack Compose** and **LiveData**.

## Project Structure

```
com.komputerkit.mvvm_demo/
├── model/
│   ├── UserData.kt          # Data class for user information
│   └── UserRepository.kt    # Repository for data operations
├── viewmodel/
│   └── HomeViewModel.kt     # ViewModel managing UI state
├── view/
│   └── HomePage.kt          # Composable UI components
└── MainActivity.kt          # Main activity setup
```

## Architecture Components

### 1. Model Layer

- **`UserData`**: Data class with properties `name: String` and `age: Int`
- **`UserRepository`**: Contains `fetchUserData()` suspended function that returns hardcoded data after 2000ms delay

### 2. ViewModel Layer

- **`HomeViewModel`**: Extends `ViewModel` and manages:
  - `userData: LiveData<UserData?>` - Holds user data state
  - `isLoading: LiveData<Boolean>` - Manages loading state
  - `getUserData()` - Public function that fetches data using coroutines

### 3. View Layer

- **`HomePage`**: Composable function that:
  - Observes `userData` and `isLoading` using `observeAsState()`
  - Displays `CircularProgressIndicator` when loading
  - Shows "Get Data" button and user information when not loading
  - Uses centered `Column` layout

### 4. MainActivity

- Sets up the UI using Jetpack Compose
- Creates `HomeViewModel` instance using `viewModel()`
- Passes ViewModel to `HomePage` composable

## Key Features

- ✅ **MVVM Architecture**: Clear separation of concerns
- ✅ **LiveData**: Reactive UI updates
- ✅ **Jetpack Compose**: Modern declarative UI
- ✅ **Coroutines**: Asynchronous data operations
- ✅ **ViewModelScope**: Proper lifecycle management
- ✅ **Mock Repository**: Simulated network delay

## Dependencies Added

```kotlin
// ViewModel and LiveData
implementation(libs.androidx.lifecycle.viewmodel.ktx)
implementation(libs.androidx.lifecycle.livedata.ktx)
implementation(libs.androidx.compose.runtime.livedata)
implementation(libs.androidx.lifecycle.viewmodel.compose)
```

## How It Works

1. User taps "Get Data" button
2. `HomeViewModel.getUserData()` is called
3. Loading state is set to `true`
4. Repository fetches data with 2-second delay
5. Data is returned and loading state is set to `false`
6. UI automatically updates to display the user data

The application successfully demonstrates the MVVM pattern with proper state management and reactive UI updates using Jetpack Compose and LiveData.
