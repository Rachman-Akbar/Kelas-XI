# Realtime Weather App 🌤️

Aplikasi cuaca real-time yang dibangun dengan **Kotlin**, **Jetpack Compose**, **Retrofit**, dan **MVVM Architecture**. Aplikasi ini menampilkan informasi cuaca terkini berdasarkan input kota yang dimasukkan pengguna.

## 🎯 Fitur

- **Real-time Weather Data**: Mendapatkan data cuaca terkini dari WeatherAPI
- **Clean Architecture**: Implementasi MVVM dengan Clean Architecture
- **Error Handling**: Penanganan error yang komprehensif dan user-friendly
- **Loading States**: Indikator loading yang informatif
- **Responsive UI**: Antarmuka yang responsif dengan Jetpack Compose
- **Input Validation**: Validasi input kota dengan feedback yang jelas
- **Secure API Key**: Penyimpanan API key yang aman menggunakan BuildConfig

## 🛠️ Tech Stack

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Modern UI toolkit untuk Android
- **Retrofit** - HTTP client untuk API calls
- **Hilt** - Dependency injection framework
- **Coroutines** - Asynchronous programming
- **StateFlow** - State management yang reactive
- **Material 3** - Design system terbaru dari Google
- **JUnit & Mockito** - Unit testing
- **Compose Testing** - UI testing untuk Compose

## 🏗️ Arsitektur

Aplikasi ini menggunakan **Clean Architecture** dengan pattern **MVVM**:

```
app/
├── data/
│   ├── model/          # Data models & DTOs
│   ├── network/        # API service interfaces
│   └── repository/     # Repository implementations
├── di/                 # Dependency injection modules
├── presentation/
│   ├── components/     # Reusable UI components
│   ├── screen/         # Screen composables
│   └── viewmodel/      # ViewModels
└── utils/             # Utility classes & extensions
```

## 🔧 Setup & Installation

### 1. Clone Repository

```bash
git clone https://github.com/yourusername/RealtimeWeatherApp.git
cd RealtimeWeatherApp
```

### 2. API Key Configuration

1. Daftar di [WeatherAPI.com](https://www.weatherapi.com/) untuk mendapatkan API key gratis
2. Buka file `local.properties` di root project
3. Tambahkan API key Anda:

```properties
WEATHER_API_KEY=your_api_key_here
```

### 3. Build & Run

```bash
./gradlew assembleDebug
```

Atau jalankan langsung dari Android Studio.

## 📱 Penggunaan

1. **Launch App**: Buka aplikasi untuk melihat halaman welcome
2. **Search City**: Masukkan nama kota di search bar
3. **Get Weather**: Tekan tombol search atau enter untuk mendapatkan data cuaca
4. **View Details**: Lihat informasi detail seperti temperature, humidity, wind speed, dan pressure

## 🎨 UI/UX Features

### Enhanced User Experience:

- **Welcome Screen**: Pesan selamat datang yang informatif
- **Loading Animations**: Indikator loading dengan animasi smooth
- **Error Handling**: Pesan error yang jelas dengan opsi retry
- **Input Validation**: Validasi real-time dengan feedback yang helpful
- **Responsive Design**: UI yang adaptif untuk berbagai ukuran layar

### Performance Optimizations:

- **@Stable Annotations**: Mengurangi unnecessary recomposition
- **StateFlow dengan Lifecycle**: Lifecycle-aware state collection
- **Remember**: Strategic caching untuk performa optimal
- **Debounced Input**: Mencegah API calls yang berlebihan

## 🔒 Keamanan

### API Key Security:

- API key disimpan dalam `local.properties` (tidak di-commit ke VCS)
- Akses melalui `BuildConfig` untuk keamanan tambahan
- HTTPS only untuk semua network calls
- Certificate pinning ready (optional enhancement)

## 🧪 Testing

### Unit Tests:

```bash
./gradlew testDebugUnitTest
```

### Instrumentation Tests:

```bash
./gradlew connectedDebugAndroidTest
```

### Test Coverage:

- **ViewModels**: Mockito + Turbine untuk testing coroutines
- **Repository**: Comprehensive error scenarios
- **UI Components**: Compose testing dengan ComposeTestRule

## 📊 Error Handling

Aplikasi menangani berbagai skenario error:

- **Network Errors**: No internet, timeout, connection failed
- **API Errors**: 404 (location not found), 401 (invalid API key), 429 (rate limit)
- **Validation Errors**: Empty input, invalid format
- **General Errors**: Unexpected exceptions dengan graceful fallback

## 🚀 Performance Features

1. **Optimized Recomposition**:

   - `@Stable` annotations pada data classes
   - Strategic use of `remember` dan `derivedStateOf`
   - Lifecycle-aware StateFlow collection

2. **Network Optimization**:

   - Request timeout configuration
   - Retry mechanism
   - Efficient error handling

3. **Memory Management**:
   - Proper coroutine scope handling
   - Job cancellation on ViewModel clear
   - Lifecycle-aware observers

## 🔮 Future Enhancements

- [ ] **Offline Support**: Cache last weather data
- [ ] **Location-based Weather**: GPS integration
- [ ] **Weather Forecast**: 7-day forecast display
- [ ] **Weather Maps**: Interactive weather maps
- [ ] **Push Notifications**: Weather alerts
- [ ] **Multiple Locations**: Save favorite cities
- [ ] **Dark Mode**: Theme switching capability
- [ ] **Weather Widgets**: Home screen widgets

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 👨‍💻 Author

**Your Name**

- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

## 🙏 Acknowledgments

- [WeatherAPI](https://www.weatherapi.com/) untuk API data cuaca gratis
- [Material Design 3](https://m3.material.io/) untuk design guidelines
- Jetpack Compose team untuk modern UI toolkit yang amazing
