# 🎨 Design System & UI Guidelines

Panduan lengkap sistem desain aplikasi SI-UMKM Mobile untuk konsistensi visual dan pengalaman pengguna.

---

## 🎯 Design Principles

### 1. **Simplicity First**

- Antarmuka yang bersih dan tidak membingungkan
- Fokus pada fungsi utama di setiap screen
- Minimize cognitive load untuk pengguna UMKM

### 2. **Consistency**

- Pola interaksi yang konsisten di seluruh aplikasi
- Komponen yang dapat digunakan kembali
- Terminologi yang seragam

### 3. **Accessibility**

- Touch target minimal 48dp
- Contrast ratio minimal 4.5:1
- Dukungan untuk ukuran font sistem
- Screen reader compatibility

### 4. **Performance**

- Loading state yang jelas
- Feedback immediate untuk interaksi user
- Minimize animation untuk performa

---

## 🎨 Color System

### Primary Colors

```kotlin
val PrimaryColors = object {
    val Primary = Color(0xFF197FE6)        // Blue - Main brand color
    val PrimaryVariant = Color(0xFF1565C0) // Darker blue
    val OnPrimary = Color(0xFFFFFFFF)      // White text on primary
}
```

**Usage**:

- Headers
- Primary buttons
- Active navigation items
- Links

### Secondary Colors

```kotlin
val SecondaryColors = object {
    val Success = Color(0xFF10B981)        // Green
    val Warning = Color(0xFFF59E0B)        // Orange/Amber
    val Error = Color(0xFFEF4444)          // Red
    val Info = Color(0xFF3B82F6)           // Light Blue
}
```

**Usage**:

- **Success**: Completed actions, profit indicators, stock available
- **Warning**: Alerts, low stock, pending items
- **Error**: Errors, debt overdue, out of stock
- **Info**: Information messages, tips

### Neutral Colors

```kotlin
val NeutralColors = object {
    val Gray50 = Color(0xFFF9FAFB)
    val Gray100 = Color(0xFFF3F4F6)
    val Gray200 = Color(0xFFE5E7EB)
    val Gray300 = Color(0xFFD1D5DB)
    val Gray400 = Color(0xFF9CA3AF)
    val Gray500 = Color(0xFF6B7280)
    val Gray600 = Color(0xFF4B5563)
    val Gray700 = Color(0xFF374151)
    val Gray800 = Color(0xFF1F2937)
    val Gray900 = Color(0xFF111827)
}
```

**Usage**:

- Backgrounds
- Borders
- Disabled states
- Secondary text

### Semantic Colors

```kotlin
// Income/Expense
val IncomeGreen = Color(0xFF10B981)
val ExpenseRed = Color(0xFFEF4444)

// Stock Status
val StockCritical = Color(0xFFEF4444)
val StockLow = Color(0xFFF59E0B)
val StockSafe = Color(0xFF10B981)

// Production Status
val StatusPlanned = Color(0xFF3B82F6)
val StatusInProgress = Color(0xFFF59E0B)
val StatusCompleted = Color(0xFF10B981)
val StatusCancelled = Color(0xFF6B7280)

// Customer Segment
val VIPGold = Color(0xFFFBBF24)
val RegularPurple = Color(0xFF8B5CF6)
val NewGray = Color(0xFF9CA3AF)
```

### Color Palettes

#### Light Theme (Default)

```kotlin
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF197FE6),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E9FF),
    onPrimaryContainer = Color(0xFF001B3F),

    secondary = Color(0xFF10B981),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF064E3B),

    tertiary = Color(0xFF8B5CF6),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFEDE9FE),
    onTertiaryContainer = Color(0xFF4C1D95),

    error = Color(0xFFEF4444),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF7F1D1D),

    background = Color(0xFFFCFCFC),
    onBackground = Color(0xFF1F2937),

    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2937),
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF6B7280),

    outline = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFF3F4F6)
)
```

#### Dark Theme (Future)

```kotlin
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF001B3F),
    // ... (to be defined)
)
```

---

## 📝 Typography

### Type Scale

```kotlin
val AppTypography = Typography(
    // Display - Large headers
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),

    // Headline - Section headers
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // Title - Card titles
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body - Main content
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label - Buttons, tabs
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### Typography Usage

| Element        | Style                        | Example               |
| -------------- | ---------------------------- | --------------------- |
| Screen Title   | headlineSmall (24sp, Bold)   | "Dashboard Produksi"  |
| Section Header | titleMedium (16sp, SemiBold) | "Transaksi Terakhir"  |
| Card Title     | titleMedium (16sp, SemiBold) | "Kopi Latte"          |
| Body Text      | bodyMedium (14sp, Normal)    | "Deskripsi produk..." |
| Caption/Meta   | bodySmall (12sp, Normal)     | "31 Jan 2026, 10:30"  |
| Button Label   | labelLarge (14sp, Medium)    | "Simpan"              |
| Tab Label      | labelMedium (12sp, Medium)   | "Semua"               |
| Badge Text     | labelSmall (11sp, Medium)    | "Menipis"             |

---

## 📏 Spacing System

### Base Unit: 4dp

```kotlin
object Spacing {
    val xxs = 2.dp    // Tight spacing
    val xs = 4.dp     // Minimal spacing
    val sm = 8.dp     // Small spacing
    val md = 12.dp    // Medium spacing
    val lg = 16.dp    // Large spacing (default padding)
    val xl = 24.dp    // Extra large
    val xxl = 32.dp   // Double extra large
    val xxxl = 48.dp  // Triple extra large
}
```

### Component Spacing

| Element                    | Spacing                        |
| -------------------------- | ------------------------------ |
| Screen padding             | 16dp                           |
| Card padding               | 16dp                           |
| List item padding          | 16dp horizontal, 12dp vertical |
| Section gap                | 24dp                           |
| Component gap (vertical)   | 8dp                            |
| Component gap (horizontal) | 12dp                           |
| Button padding             | 16dp horizontal, 12dp vertical |
| Icon padding               | 8dp                            |

### Layout Examples

```kotlin
// Screen with standard padding
Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)  // lg
) {
    Text("Title")

    Spacer(Modifier.height(24.dp))  // xxl - section gap

    Card(modifier = Modifier.padding(vertical = 8.dp)) {  // sm
        Column(modifier = Modifier.padding(16.dp)) {  // lg
            Text("Content")
        }
    }
}
```

---

## 🔲 Component Specifications

### Buttons

#### Primary Button

```kotlin
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
```

**Specs**:

- Height: 48dp
- Corner radius: 12dp
- Horizontal padding: 24dp
- Text: labelLarge (14sp, Medium)

#### Secondary Button (Outlined)

```kotlin
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

**Specs**:

- Height: 48dp
- Border: 1.5dp, primary color
- Corner radius: 12dp

#### Text Button

```kotlin
@Composable
fun AppTextButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
```

### Cards

#### Standard Card

```kotlin
@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}
```

**Specs**:

- Corner radius: 12dp
- Padding: 16dp
- Elevation: 1dp
- Background: surface color

#### Elevated Card (Stats)

```kotlin
@Composable
fun StatsCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
```

**Specs**:

- Corner radius: 16dp
- Icon container: 36dp × 36dp, rounded 8dp
- Elevation: 2dp

### Text Fields

#### Standard TextField

```kotlin
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
```

**Specs**:

- Height: 56dp (default Material3)
- Corner radius: 12dp
- Border width: 1.5dp
- Padding: 16dp horizontal

#### Search Bar

```kotlin
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Cari...",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(
                placeholder,
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = Color.Gray
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
```

**Specs**:

- Height: 52dp
- Corner radius: 12dp
- Leading icon: Search (20dp)
- Border: outline color (subtle)

### Badges

#### Status Badge

```kotlin
@Composable
fun StatusBadge(
    text: String,
    type: BadgeType,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (type) {
        BadgeType.Success -> Color(0xFFD1FAE5) to Color(0xFF065F46)
        BadgeType.Warning -> Color(0xFFFEF3C7) to Color(0xFF92400E)
        BadgeType.Error -> Color(0xFFFEE2E2) to Color(0xFF991B1B)
        BadgeType.Info -> Color(0xFFDBEAFE) to Color(0xFF1E40AF)
        BadgeType.Neutral -> Color(0xFFF3F4F6) to Color(0xFF374151)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = backgroundColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

enum class BadgeType {
    Success, Warning, Error, Info, Neutral
}
```

**Specs**:

- Corner radius: 6dp
- Padding: 8dp horizontal, 4dp vertical
- Text: labelSmall (11sp, Medium)

### Icons

#### Icon Sizes

```kotlin
object IconSize {
    val Small = 16.dp
    val Medium = 20.dp
    val Large = 24.dp
    val XLarge = 32.dp
    val XXLarge = 48.dp
}
```

#### Icon Button Container

```kotlin
@Composable
fun IconButtonContainer(
    icon: ImageVector,
    onClick: () -> Unit,
    size: Dp = 36.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(size * 0.6f)
        )
    }
}
```

**Specs**:

- Default size: 36dp × 36dp
- Icon size: 60% of container (typically 20-24dp)
- Corner radius: 8dp

### Bottom Navigation

```kotlin
@Composable
fun AppBottomNavigationBar(
    items: List<NavigationItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
```

**Specs**:

- Height: 80dp (Material3 default)
- Icon size: 24dp
- Label: labelMedium (12sp, Medium)
- Elevation: 3dp

---

## 🖼️ Layout Patterns

### Screen Header

```kotlin
@Composable
fun ScreenHeader(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            onNavigateBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(Modifier.width(8.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )

            actions()
        }

        HorizontalDivider()
    }
}
```

**Specs**:

- Height: 56dp
- Horizontal padding: 16dp
- Status bar padding applied
- Divider at bottom

### List Item

```kotlin
@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val modifier = Modifier
        .fillMaxWidth()
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(horizontal = 16.dp, vertical = 12.dp)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingIcon?.let {
            it()
            Spacer(Modifier.width(16.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        trailing?.invoke()
    }
}
```

**Specs**:

- Min height: 56dp (with subtitle: 72dp)
- Horizontal padding: 16dp
- Vertical padding: 12dp
- Icon-text gap: 16dp

---

## 🎬 Animations & Transitions

### Standard Durations

```kotlin
object AnimationDuration {
    const val Fast = 150
    const val Medium = 300
    const val Slow = 500
}
```

### Fade In/Out

```kotlin
@Composable
fun <T> FadeInOut(
    targetState: T,
    content: @Composable (T) -> Unit
) {
    AnimatedContent(
        targetState = targetState,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) with
            fadeOut(animationSpec = tween(300))
        }
    ) { state ->
        content(state)
    }
}
```

### Slide In/Out

```kotlin
@Composable
fun SlideInVertically(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        content()
    }
}
```

---

## ♿ Accessibility

### Touch Targets

- Minimum size: **48dp × 48dp**
- Recommended: **56dp × 56dp** for primary actions

### Color Contrast

- Normal text: **4.5:1** minimum
- Large text (18sp+): **3:1** minimum
- UI components: **3:1** minimum

### Screen Reader Support

```kotlin
// Content description for icons
Icon(
    Icons.Default.Delete,
    contentDescription = "Hapus item"
)

// Semantic properties
Text(
    text = "Rp 1.000.000",
    modifier = Modifier.semantics {
        contentDescription = "Saldo satu juta rupiah"
    }
)

// Click labels
Button(
    onClick = { /* ... */ },
    modifier = Modifier.semantics {
        onClick(label = "Simpan transaksi") { true }
    }
) {
    Text("Simpan")
}
```

---

## 📱 Responsive Design

### Breakpoints

```kotlin
object Breakpoint {
    val Compact = 0.dp..599.dp      // Phones portrait
    val Medium = 600.dp..839.dp     // Phones landscape, tablets
    val Expanded = 840.dp..9999.dp  // Large tablets, desktop
}

@Composable
fun getWindowSizeClass(): WindowSizeClass {
    val width = LocalConfiguration.current.screenWidthDp.dp
    return when {
        width < 600.dp -> WindowSizeClass.Compact
        width < 840.dp -> WindowSizeClass.Medium
        else -> WindowSizeClass.Expanded
    }
}
```

### Adaptive Layouts

```kotlin
@Composable
fun AdaptiveGrid(items: List<Item>) {
    val windowSize = getWindowSizeClass()
    val columns = when (windowSize) {
        WindowSizeClass.Compact -> 2
        WindowSizeClass.Medium -> 3
        WindowSizeClass.Expanded -> 4
    }

    LazyVerticalGrid(columns = GridCells.Fixed(columns)) {
        items(items) { item ->
            ItemCard(item)
        }
    }
}
```

---

## 🎯 Design Tokens

### Comprehensive Token System

```kotlin
object DesignTokens {
    // Spacing
    object Space {
        val xxs = 2.dp
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 24.dp
        val xxl = 32.dp
        val xxxl = 48.dp
    }

    // Corner Radius
    object Radius {
        val sm = 4.dp
        val md = 8.dp
        val lg = 12.dp
        val xl = 16.dp
        val round = 999.dp
    }

    // Elevation
    object Elevation {
        val none = 0.dp
        val sm = 1.dp
        val md = 2.dp
        val lg = 4.dp
        val xl = 8.dp
    }

    // Border
    object Border {
        val thin = 1.dp
        val medium = 1.5.dp
        val thick = 2.dp
    }
}
```

---

## 📋 Checklist untuk Developer

### Sebelum Implement UI

- [ ] Review design tokens (colors, spacing, typography)
- [ ] Check existing components (jangan recreate)
- [ ] Verify accessibility requirements
- [ ] Plan responsive behavior

### Saat Develop

- [ ] Use design tokens (jangan hardcode values)
- [ ] Apply consistent spacing
- [ ] Set content descriptions untuk icons
- [ ] Minimum touch target 48dp
- [ ] Test dengan dark mode (future)

### Setelah Develop

- [ ] Test di berbagai screen sizes
- [ ] Verify color contrast
- [ ] Check dengan TalkBack (screen reader)
- [ ] Review performance (avoid complex animations)

---

## 📚 Resources

### Material Design 3

- [Material Design Guidelines](https://m3.material.io/)
- [Jetpack Compose Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3)

### Accessibility

- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility)
- [WCAG 2.1 Guidelines](https://www.w3.org/WAI/WCAG21/quickref/)

### Figma (Future)

- Design system library
- Component specifications
- Color palettes
