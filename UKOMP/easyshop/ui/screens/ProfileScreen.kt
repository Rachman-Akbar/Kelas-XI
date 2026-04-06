package com.komputerkit.easyshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.komputerkit.easyshop.ui.viewmodel.AuthViewModel
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToOrders: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    // Show logout dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        authViewModel.signOut()
                        showLogoutDialog = false
                        onNavigateToAuth()
                        Toast.makeText(context, "Berhasil logout", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Ya, Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Profil Saya",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.displayName?.firstOrNull()?.toString()?.uppercase() 
                                ?: currentUser?.email?.firstOrNull()?.toString()?.uppercase() 
                                ?: "U",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name
                    Text(
                        text = currentUser?.displayName ?: "Pengguna EasyShop",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email
                    Text(
                        text = currentUser?.email ?: "No email",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // User Status Badge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.secondary
                    ) {
                        Text(
                            text = "Member Aktif",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Account Section
            ProfileSection(
                title = "Akun",
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profil",
                        subtitle = "Ubah informasi pribadi Anda",
                        onClick = { 
                            Toast.makeText(context, "Fitur Edit Profil akan segera tersedia", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    ProfileMenuItem(
                        icon = Icons.Default.Lock,
                        title = "Keamanan",
                        subtitle = "Ubah password dan pengaturan keamanan",
                        onClick = { 
                            Toast.makeText(context, "Fitur Keamanan akan segera tersedia", Toast.LENGTH_SHORT).show()
                        }
                    )
                )
            )
            
            // Orders Section
            ProfileSection(
                title = "Pesanan",
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Default.ShoppingCart,
                        title = "Riwayat Pesanan",
                        subtitle = "Lihat semua pesanan Anda",
                        onClick = onNavigateToOrders
                    ),
                    ProfileMenuItem(
                        icon = Icons.Default.LocalShipping,
                        title = "Lacak Pesanan",
                        subtitle = "Pantau status pengiriman pesanan",
                        onClick = { 
                            Toast.makeText(context, "Fitur Lacak Pesanan akan segera tersedia", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    ProfileMenuItem(
                        icon = Icons.Default.Favorite,
                        title = "Wishlist",
                        subtitle = "Produk yang Anda simpan",
                        onClick = onNavigateToFavorites
                    )
                )
            )
            
            // Settings Section
            ProfileSection(
                title = "Pengaturan",
                items = listOf(
                    ProfileMenuItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifikasi",
                        subtitle = "Atur preferensi notifikasi",
                        onClick = { 
                            Toast.makeText(context, "Fitur Notifikasi akan segera tersedia", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    ProfileMenuItem(
                        icon = Icons.Default.Help,
                        title = "Bantuan & FAQ",
                        subtitle = "Dapatkan bantuan dan jawaban atas pertanyaan",
                        onClick = { 
                            Toast.makeText(context, "Fitur Bantuan akan segera tersedia", Toast.LENGTH_SHORT).show()
                        }
                    ),
                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        subtitle = "Informasi versi dan developer",
                        onClick = { 
                            Toast.makeText(context, "EasyShop v1.0.0 - Developed with ❤️", Toast.LENGTH_LONG).show()
                        }
                    )
                )
            )
            
            // Logout Section
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                ProfileMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Logout",
                    subtitle = "Keluar dari akun Anda",
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.padding(16.dp),
                    titleColor = MaterialTheme.colorScheme.onErrorContainer,
                    subtitleColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    items: List<ProfileMenuItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    ProfileMenuItem(
                        icon = item.icon,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick,
                        modifier = Modifier.padding(16.dp)
                    )
                    
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    subtitleColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = titleColor
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Navigate",
                modifier = Modifier.size(20.dp),
                tint = subtitleColor
            )
        }
    }
}

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)