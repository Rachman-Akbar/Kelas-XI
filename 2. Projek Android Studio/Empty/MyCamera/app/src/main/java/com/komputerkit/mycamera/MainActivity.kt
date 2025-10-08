package com.komputerkit.mycamera

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import coil.compose.AsyncImage
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.komputerkit.mycamera.ui.theme.ComposeCameraTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import com.komputerkit.mycamera.ui.theme.*

class MainActivity : ComponentActivity() {
    private val vm: CameraViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeCameraTheme {
                val snackbar = remember { SnackbarHostState() }
                var showImageViewer by remember { mutableStateOf(false) }
                val lastUri by vm.lastCapturedUri.collectAsStateWithLifecycle()
                
                CameraApp(vm = vm, snackbar = snackbar, showImageViewer = showImageViewer, lastUri = lastUri) { showImageViewer = it }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraApp(
    vm: CameraViewModel,
    snackbar: SnackbarHostState,
    showImageViewer: Boolean,
    lastUri: Uri?,
    onShowImageViewerChange: (Boolean) -> Unit
) {
    // Scaffold yang sama untuk kedua mode
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { 
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "MyCamera",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.8f)
                ),
                modifier = Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = 0.9f),
                            PrimaryBlueDark.copy(alpha = 0.9f)
                        )
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = Color.Black
    ) { padding ->
        var errorMessage by remember { mutableStateOf<String?>(null) }

        PermissionGate(
            modifier = Modifier.padding(padding),
            onDenied = { msg -> errorMessage = msg }
        ) {
            if (showImageViewer && lastUri != null) {
                ImageViewerScreen(uri = lastUri, vm = vm, onDismiss = { onShowImageViewerChange(false) })
            } else {
                CameraScreen(vm, onThumbnailClick = { onShowImageViewerChange(true) })
            }
        }

        errorMessage?.let { message ->
            LaunchedEffect(message) {
                snackbar.showSnackbar(message)
                errorMessage = null
            }
        }
    }
}

@Composable
fun PermissionGate(
    modifier: Modifier = Modifier,
    onDenied: (String) -> Unit,
    content: @Composable () -> Unit
) {
    var granted by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val permissions = remember {
        buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }.toTypedArray()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        granted = results.values.all { it }
        if (!granted) {
            onDenied("Izin kamera dan penyimpanan diperlukan untuk menggunakan aplikasi")
        }
    }

    LaunchedEffect(Unit) {
        val hasPermissions = permissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        granted = hasPermissions
        if (!hasPermissions) {
            launcher.launch(permissions)
        }
    }

    if (granted) {
        Box(modifier = modifier.fillMaxSize()) { content() }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            NeutralDark,
                            Color.Black
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 16.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated camera icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.2f),
                                        PrimaryBlue.copy(alpha = 0.1f)
                                    )
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PrimaryBlue
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Text(
                        text = "Izin Kamera Diperlukan",
                        style = MaterialTheme.typography.headlineSmall,
                        color = NeutralDark,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        text = "MyCamera memerlukan akses ke kamera dan penyimpanan untuk mengambil dan menyimpan foto dengan sempurna",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NeutralMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Button(
                        onClick = { launcher.launch(permissions) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Berikan Izin",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            "Data Anda aman dan terlindungi",
                            style = MaterialTheme.typography.labelMedium,
                            color = NeutralMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageViewerScreen(
    uri: Uri,
    vm: CameraViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Background dengan gradient untuk konsistensi
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black,
                            PrimaryBlueDark.copy(alpha = 0.3f),
                            Color.Black
                        )
                    )
                )
        )
        
        // Gambar utama
        AsyncImage(
            model = uri,
            contentDescription = "Captured image",
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 0.dp, bottom = 140.dp), // Gambar mepet dengan header
            contentScale = ContentScale.Fit
        )

        // Tombol kembali di kiri atas (sama seperti flash di camera)
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
                .size(48.dp)
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Action buttons di bagian bawah
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Share Button
            ActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = { vm.shareImage(context, uri) }
            )

            // Edit Button
            ActionButton(
                icon = Icons.Default.Edit,
                label = "Edit",
                onClick = { vm.editImage(context, uri) }
            )

            // Delete Button
            ActionButton(
                icon = Icons.Default.Delete,
                label = "Delete",
                onClick = {
                    vm.deleteImage(context, uri)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun ActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = PrimaryBlue,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CameraScreen(vm: CameraViewModel, onThumbnailClick: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val lastUri by vm.lastCapturedUri.collectAsStateWithLifecycle()
    val isCapturing by vm.isCapturing.collectAsStateWithLifecycle()
    val flashMode by vm.flashMode.collectAsStateWithLifecycle()
    val isFrontCamera by vm.isFrontCamera.collectAsStateWithLifecycle()
    val showSuccessMessage by vm.showSuccessMessage.collectAsStateWithLifecycle()
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    // Show success message
    showSuccessMessage?.let { message ->
        LaunchedEffect(message) {
            vm.clearSuccessMessage()
        }
    }

    LaunchedEffect(isFrontCamera, flashMode) {
        vm.bindCamera(context) { provider: ProcessCameraProvider, preview: Preview ->
            val selector = if (isFrontCamera) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            try {
                provider.unbindAll()
                preview.setSurfaceProvider(previewView?.surfaceProvider)
                
                provider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    vm.imageCapture
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera preview dengan overlay kontrol
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { pv ->
                        pv.scaleType = PreviewView.ScaleType.FILL_CENTER
                        previewView = pv
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Tombol Flash di pojok kiri atas
            IconButton(
                onClick = { vm.toggleFlash() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .size(56.dp)
                    .background(
                        when (flashMode) {
                            CameraViewModel.FlashMode.OFF -> Color.Black.copy(alpha = 0.6f)
                            CameraViewModel.FlashMode.ON -> PrimaryBlue.copy(alpha = 0.9f)
                            CameraViewModel.FlashMode.AUTO -> SecondaryOrange.copy(alpha = 0.9f)
                        },
                        CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = when (flashMode) {
                        CameraViewModel.FlashMode.OFF -> Icons.Filled.FlashOff
                        CameraViewModel.FlashMode.ON -> Icons.Filled.FlashOn
                        CameraViewModel.FlashMode.AUTO -> Icons.Filled.FlashAuto
                    },
                    contentDescription = "Flash: ${flashMode.name}",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Tombol Putar Kamera di pojok kanan atas
            IconButton(
                onClick = { vm.toggleCamera() },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(56.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        CircleShape
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Cameraswitch,
                    contentDescription = "Putar Kamera",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Bottom control bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery/Last photo
                if (lastUri != null) {
                    Card(
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onThumbnailClick() },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(lastUri),
                            contentDescription = "Last Photo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    IconButton(
                        onClick = { vm.openGallery(context) },
                        modifier = Modifier
                            .size(64.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        PrimaryBlue.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                ),
                                CircleShape
                            )
                            .border(2.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Capture button (Photo only)
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        Color.White.copy(alpha = 0.8f)
                                    )
                                ),
                                CircleShape
                            )
                            .clickable(enabled = !isCapturing) {
                                vm.takePhoto(context) { /* handle */ }
                            }
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCapturing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(52.dp),
                                color = PrimaryBlue,
                                strokeWidth = 5.dp
                            )
                        } else {
                            // Photo mode - white circle with black border
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.White, CircleShape)
                                    .border(4.dp, Color.Black, CircleShape)
                            )
                        }
                    }
                }

                // Spacer for balance (replaces timer button)
                Box(modifier = Modifier.size(64.dp))
            }
        }
    }
}
