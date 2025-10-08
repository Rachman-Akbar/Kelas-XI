# ListDemoApp - Jetpack Compose Demo

Aplikasi demo untuk mempelajari Jetpack Compose, khususnya implementasi LazyColumn dan komponen UI dasar.

## 🚀 Fitur Utama

### 1. **List Demo (Tab 1)**

- Implementasi `LazyColumn` dengan `itemsIndexed()`
- Data class `User` dengan properti id, name, email, role
- State management untuk selected item
- Card design dengan animation dan elevation
- Click handling dengan visual feedback

### 2. **LazyColumn Examples (Tab 2)**

- **Simple Count List**: Menggunakan `items(count = n)`
- **Product List**: Menggunakan `items(list)` dengan data class `Product`
- **Message List**: Menggunakan `itemsIndexed(list)` dengan data class `Message`
- **Mixed Content List**: Kombinasi `item`, `items(count)`, dan `itemsIndexed()`

### 3. **State Management (Tab 3)**

- Counter example dengan `remember` dan `mutableIntStateOf`
- Text input dengan `mutableStateOf`
- Dynamic list management
- State lifting dan unidirectional data flow

### 4. **Layout Examples (Tab 4)**

- `Column` dengan berbagai alignment dan arrangement
- `Row` dengan spacing dan distribution
- `Box` dengan stacked elements dan positioning
- Complex layout dengan nested components
- Modifier examples: `padding`, `size`, `clip`, `background`, dll

## 🛠️ Konfigurasi Proyek

### Dependencies (sudah dikonfigurasi)

- **Kotlin**: 2.0.21
- **Compose BOM**: 2024.09.00
- **Android Gradle Plugin**: 8.11.2
- **Minimum SDK**: 24
- **Target SDK**: 36

### Gradle Configuration

```kotlin
android {
    compileSdk = 36
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
```

## 📝 Komponen Utama

### Data Classes

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val rating: Float
)

data class Message(
    val id: Int,
    val sender: String,
    val content: String,
    val timestamp: String,
    val isRead: Boolean
)
```

### LazyColumn Examples

#### 1. Simple Count List

```kotlin
LazyColumn {
    items(count = 25) { index ->
        // Item content
    }
}
```

#### 2. List with Data

```kotlin
LazyColumn {
    items(productList) { product ->
        ProductItem(product = product)
    }
}
```

#### 3. Indexed List

```kotlin
LazyColumn {
    itemsIndexed(messageList) { index, message ->
        MessageItem(
            message = message,
            index = index,
            onClick = { /* Handle click */ }
        )
    }
}
```

### State Management Examples

```kotlin
@Composable
fun StateExample() {
    // Simple state
    var counter by remember { mutableIntStateOf(0) }

    // List state
    var todoList by remember { mutableStateOf(listOf<String>()) }

    // State dengan complex object
    var selectedUser by remember { mutableStateOf<User?>(null) }
}
```

### Modifier Examples

```kotlin
// Basic modifiers
.fillMaxSize()
.fillMaxWidth()
.padding(16.dp)
.size(48.dp)
.clip(RoundedCornerShape(8.dp))
.background(color = MaterialTheme.colorScheme.primary)
.clickable { /* Handle click */ }

// Layout modifiers
.weight(1f) // in Row/Column
.align(Alignment.Center) // in Box
.offset(x = 8.dp, y = (-8).dp)
```

## 🎨 UI Design Patterns

### Material 3 Design

- Menggunakan `MaterialTheme.colorScheme` untuk consistent colors
- `Card` components dengan elevation dan shape
- `TopAppBar` dengan Material 3 styling
- `NavigationBar` untuk bottom navigation

### Layout Patterns

- **Column**: Vertical arrangement dengan spacing
- **Row**: Horizontal arrangement dengan weight distribution
- **Box**: Overlay dan absolute positioning
- **LazyColumn**: Efficient scrollable lists
- **Scaffold**: Screen structure dengan TopBar dan BottomBar

### Interactive Elements

- Click handling dengan visual feedback
- State-driven UI changes
- Selection states dengan color changes
- Input handling dengan validation

## 🔧 Cara Menjalankan

1. **Clone atau download project**
2. **Buka di Android Studio**
3. **Sync Gradle** (otomatis)
4. **Run aplikasi** pada device/emulator

## 📱 Preview Screenshots

Aplikasi memiliki 4 tab utama:

- **List Demo**: User list dengan selection
- **LazyColumn Examples**: Berbagai implementasi LazyColumn
- **State Management**: Counter, input, dan dynamic list
- **Layout Examples**: Column, Row, Box, dan complex layouts

## 🎯 Pembelajaran Key Points

1. **LazyColumn Performance**: Efficient rendering untuk large lists
2. **State Management**: `remember`, `mutableStateOf`, state lifting
3. **Modifier Chains**: Composable customization dan styling
4. **Material 3**: Modern Android design system
5. **Preview**: `@Preview` untuk development dan testing
6. **Data Flow**: Unidirectional data flow pattern
7. **Composition**: Building UI dengan reusable components

## 💡 Best Practices Implemented

- ✅ Proper state management dengan `remember`
- ✅ Modifier chains untuk consistent styling
- ✅ Reusable composable functions
- ✅ Preview functions untuk development
- ✅ Material 3 design guidelines
- ✅ Proper data class usage
- ✅ Performance optimization dengan LazyColumn
- ✅ Accessibility considerations

---

**Happy Coding with Jetpack Compose! 🚀**
