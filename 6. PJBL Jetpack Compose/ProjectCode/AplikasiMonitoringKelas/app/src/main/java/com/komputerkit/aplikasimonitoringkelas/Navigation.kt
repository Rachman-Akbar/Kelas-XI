package com.komputerkit.aplikasimonitoringkelas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.komputerkit.aplikasimonitoringkelas.common.*
import com.komputerkit.aplikasimonitoringkelas.ui.components.AppBottomNavBar
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthViewModel
import com.komputerkit.aplikasimonitoringkelas.data.repository.AuthViewModelFactory
import com.komputerkit.aplikasimonitoringkelas.di.ServiceLocator
import com.komputerkit.aplikasimonitoringkelas.di.ViewModelFactory
import com.komputerkit.aplikasimonitoringkelas.guru.*
import com.komputerkit.aplikasimonitoringkelas.siswa.*
import com.komputerkit.aplikasimonitoringkelas.kurikulum.*
import com.komputerkit.aplikasimonitoringkelas.kepsek.*
import com.komputerkit.aplikasimonitoringkelas.ui.LoginScreen
import androidx.activity.compose.BackHandler
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(LocalContext.current))
) {
    val navController = rememberNavController()
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Provide repositories and ViewModel factory
    val attendanceRepository = ServiceLocator.provideAttendanceRepository()
    val authRepositoryForVM = ServiceLocator.provideAuthRepository(LocalContext.current)
    val vmFactory = ViewModelFactory(authRepositoryForVM, attendanceRepository)

    // Create ViewModels
    val guruViewModel: GuruViewModel = viewModel(factory = vmFactory)
    val siswaViewModel: SiswaViewModel = viewModel(factory = vmFactory)
    val kurikulumViewModel: KurikulumViewModel = viewModel(factory = vmFactory)
    val kepsekViewModel: KepsekViewModel = viewModel(factory = vmFactory)

    // Get authentication state
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState(initial = false)
    val userRole by authViewModel.userRole.collectAsState(initial = null)
    val userName by authViewModel.userName.collectAsState(initial = null)
    val isLoading by authViewModel.isLoading.collectAsState(initial = false)

    // Get user info from DataStore for consistent display
    val context = LocalContext.current
    var currentUserName by remember { mutableStateOf("") }
    var currentUserRole by remember { mutableStateOf("") }

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            try {
                val authRepo = ServiceLocator.provideAuthRepository(context)
                currentUserName = authRepo.getUserName()
                currentUserRole = authRepo.getUserRole()
            } catch (e: Exception) {
                println("Navigation - Failed to get user info: ${e.message}")
            }
        }
    }

    // Handle back button
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    BackHandler {
        when (currentRoute) {
            "guru_home", "siswa_home", "kurikulum_home", "kepsek_home" -> {
                showLogoutConfirmation = true
            }

            "login" -> {
                // Let system handle back to exit app
            }

            else -> {
                val homeRoute = when (authViewModel.userRole.value) {
                    "guru" -> "guru_home"
                    "siswa" -> "siswa_home"
                    "kurikulum" -> "kurikulum_home"
                    "kepsek", "admin" -> "kepsek_home"
                    else -> "login"
                }
                navController.navigate(homeRoute) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                }
            }
        }
    }

    // Apply custom theme with forced light colors
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
            onPrimary = androidx.compose.ui.graphics.Color.White,
            primaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
            onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF0D47A1),
            secondary = androidx.compose.ui.graphics.Color(0xFF03A9F4),
            onSecondary = androidx.compose.ui.graphics.Color.White,
            secondaryContainer = androidx.compose.ui.graphics.Color(0xFFB3E5FC),
            onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF01579B),
            tertiary = androidx.compose.ui.graphics.Color(0xFF4CAF50),
            onTertiary = androidx.compose.ui.graphics.Color.White,
            error = androidx.compose.ui.graphics.Color(0xFFE57373),
            onError = androidx.compose.ui.graphics.Color.White,
            errorContainer = androidx.compose.ui.graphics.Color(0xFFFFEBEE),
            onErrorContainer = androidx.compose.ui.graphics.Color(0xFFC62828),
            background = androidx.compose.ui.graphics.Color(0xFFF5F7FA),
            onBackground = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
            surface = androidx.compose.ui.graphics.Color.White,
            onSurface = androidx.compose.ui.graphics.Color(0xFF1A1C1E),
            surfaceVariant = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
            onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF424242),
            outline = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
            outlineVariant = androidx.compose.ui.graphics.Color(0xFFE0E0E0)
        ),
        typography = Typography(
            displayLarge = MaterialTheme.typography.displayLarge.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            displayMedium = MaterialTheme.typography.displayMedium.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            displaySmall = MaterialTheme.typography.displaySmall.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            headlineLarge = MaterialTheme.typography.headlineLarge.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            headlineMedium = MaterialTheme.typography.headlineMedium.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            headlineSmall = MaterialTheme.typography.headlineSmall.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            titleLarge = MaterialTheme.typography.titleLarge.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            titleMedium = MaterialTheme.typography.titleMedium.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            titleSmall = MaterialTheme.typography.titleSmall.copy(
                color = androidx.compose.ui.graphics.Color(0xFF1A1C1E)
            ),
            bodyLarge = MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.ui.graphics.Color(0xFF424242)
            ),
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(
                color = androidx.compose.ui.graphics.Color(0xFF424242)
            ),
            bodySmall = MaterialTheme.typography.bodySmall.copy(
                color = androidx.compose.ui.graphics.Color(0xFF616161)
            ),
            labelLarge = MaterialTheme.typography.labelLarge.copy(
                color = androidx.compose.ui.graphics.Color(0xFF424242)
            ),
            labelMedium = MaterialTheme.typography.labelMedium.copy(
                color = androidx.compose.ui.graphics.Color(0xFF424242)
            ),
            labelSmall = MaterialTheme.typography.labelSmall.copy(
                color = androidx.compose.ui.graphics.Color(0xFF616161)
            )
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // Logout confirmation dialog
            if (showLogoutConfirmation) {
                AlertDialog(
                    onDismissRequest = { showLogoutConfirmation = false },
                    title = {
                        Text(
                            "Konfirmasi Logout",
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    text = {
                        Text(
                            "Apakah Anda yakin ingin keluar dari aplikasi?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                authViewModel.logout()
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
                                }
                                showLogoutConfirmation = false
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Ya, Keluar")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showLogoutConfirmation = false }
                        ) {
                            Text("Batal")
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                )
            }

            NavHost(
                navController = navController,
                startDestination = if (isAuthenticated) {
                    when (userRole) {
                        "guru" -> "guru_home"
                        "siswa" -> "siswa_home"
                        "kurikulum" -> "kurikulum_home"
                        "kepsek", "admin" -> "kepsek_home"
                        else -> "login"
                    }
                } else {
                    "login"
                },
                modifier = modifier
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { role ->
                            // Refresh auth state to get user info
                            authViewModel.forceCheckAuth()
                            when (role) {
                                "guru" -> navController.navigate("guru_home")
                                "siswa" -> navController.navigate("siswa_home")
                                "kurikulum" -> navController.navigate("kurikulum_home")
                                "kepsek", "admin" -> navController.navigate("kepsek_home")
                                else -> navController.navigate("login")
                            }
                        }
                    )
                }

                // Guru routes
                composable("guru_home") {
                    GuruHomeScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("guru_schedule") {
                    GuruScheduleScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("guru_attendance") {
                    GuruAttendanceScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("guru_permission") {
                    GuruPermissionScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("guru_substitute") {
                    GuruSubstituteTeacherScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("guru_substitute_entry") {
                    // Fallback to the main substitute screen —
                    // there is no separate `GuruSubstituteTeacherEntryScreen` defined.
                    GuruSubstituteTeacherScreen(navController, guruViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }

                // Siswa routes
                composable("siswa_home") {
                    SiswaHomeScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("siswa_schedule") {
                    SiswaScheduleScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("siswa_student_attendance") {
                    SiswaStudentAttendanceScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("siswa_teacher_attendance") {
                    SiswaTeacherAttendanceScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("siswa_permission") {
                    SiswaTeacherPermissionListScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("siswa_substitute_teacher") {
                    SiswaSubstituteTeacherScreen(navController, siswaViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }

                // Kurikulum routes
                composable("kurikulum_home") {
                    KurikulumHomeScreen(
                        navController,
                        kurikulumViewModel,
                        onLogout = {
                            showLogoutConfirmation = true
                        })
                }
                composable("kurikulum_permission") {
                    KurikulumTeacherPermissionListScreen(
                        navController = navController,
                        kurikulumViewModel = kurikulumViewModel,
                        onLogout = { showLogoutConfirmation = true }
                    )
                }
                composable("kurikulum_absent") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Guru Tidak Hadir",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kurikulum_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KurikulumTeacherAttendanceScreen(
                                navController,
                                kurikulumViewModel,
                                onLogout = { showLogoutConfirmation = true }
                            )
                        }
                        KurikulumFooter(
                            navController = navController,
                            currentRoute = "kurikulum_absent"
                        )
                    }
                }
                composable("kurikulum_substitute_entry") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Entri Guru Pengganti",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kurikulum_home") }
                        )

                        KurikulumFooter(
                            navController = navController,
                            currentRoute = "kurikulum_substitute_entry"
                        )
                    }
                }
                composable("kurikulum_attendance") {
                    KurikulumTeacherAttendanceScreen(
                        navController = navController,
                        kurikulumViewModel = kurikulumViewModel,
                        onLogout = { showLogoutConfirmation = true }
                    )
                }
                composable("kurikulum_substitute") {
                    KurikulumSubstituteTeacherListScreen(
                        navController = navController,
                        kurikulumViewModel = kurikulumViewModel,
                        onLogout = { showLogoutConfirmation = true }
                    )
                }
                composable("kurikulum_schedule") {
                    KurikulumScheduleScreen(
                        navController = navController,
                        kurikulumViewModel = kurikulumViewModel,
                        onLogout = { showLogoutConfirmation = true }
                    )
                }

                composable("kurikulum_add_attendance") {
                    AddAttendanceScreen(
                        navController = navController,
                        kurikulumViewModel = kurikulumViewModel,
                        onLogout = { showLogoutConfirmation = true }
                    )
                }

                // Kepsek routes
                composable("kepsek_home") {
                    KepsekHomeScreen(navController, kepsekViewModel, onLogout = {
                        showLogoutConfirmation = true
                    })
                }
                composable("kepsek_schedule") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Dashboard Kepala Sekolah",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kepsek_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KepsekClassScheduleListScreen(
                                navController,
                                kepsekViewModel
                            )
                        }
                        KepsekFooter(
                            navController = navController,
                            currentRoute = "kepsek_schedule"
                        )
                    }
                }
                composable("kepsek_attendance") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Dashboard Kepala Sekolah",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kepsek_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KepsekTeacherAttendanceListScreen(
                                navController,
                                kepsekViewModel
                            )
                        }
                        KepsekFooter(
                            navController = navController,
                            currentRoute = "kepsek_attendance"
                        )
                    }
                }
                composable("kepsek_substitute") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Dashboard Kepala Sekolah",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kepsek_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KepsekSubstituteTeacherListScreen(
                                navController,
                                kepsekViewModel
                            )
                        }
                        KepsekFooter(
                            navController = navController,
                            currentRoute = "kepsek_substitute"
                        )
                    }
                }
                composable("kepsek_student_attendance") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Dashboard Kepala Sekolah",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kepsek_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KepsekStudentAttendanceListScreen(
                                navController,
                                kepsekViewModel
                            )
                        }
                        KepsekFooter(
                            navController = navController,
                            currentRoute = "kepsek_student_attendance"
                        )
                    }
                }
                composable("kepsek_permission") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Dashboard Kepala Sekolah",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true },
                            showHomeButton = true,
                            onHomeClick = { navController.navigate("kepsek_home") }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            KepsekTeacherPermissionListScreen(
                                navController,
                                kepsekViewModel
                            )
                        }
                        KepsekFooter(
                            navController = navController,
                            currentRoute = "kepsek_permission"
                        )
                    }
                }

                // Compatibility routes
                composable("Login") {
                    LaunchedEffect(Unit) {
                        navController.navigate("login") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }

                composable("home") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Home",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Text(text = "Home Screen")
                        }
                        AppBottomNavBar(navController)
                    }
                }

                composable("profile") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Profile",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Text(text = "Profile Screen")
                        }
                        AppBottomNavBar(navController)
                    }
                }

                composable("settings") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Settings",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Text(text = "Settings Screen")
                        }
                        AppBottomNavBar(navController)
                    }
                }

                composable("kelas") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AppHeader(
                            title = "Kelas",
                            userName = currentUserName.ifEmpty { currentUserRole },
                            userRole = currentUserRole,
                            navController = navController,
                            onLogout = { showLogoutConfirmation = true }
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Text(text = "Kelas Screen - Displays class information based on user role")
                        }
                        AppBottomNavBar(navController)
                    }
                }

                composable("Kelas") {
                    LaunchedEffect(Unit) {
                        navController.navigate("kelas") {
                            popUpTo("kelas") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}




