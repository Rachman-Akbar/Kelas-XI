package com.komputerkit.easybot.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.komputerkit.easybot.config.OpenAIConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    val isOverLimit = text.length > OpenAIConfig.MAX_MESSAGE_LENGTH
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Character counter
            if (text.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "${text.length}/${OpenAIConfig.MAX_MESSAGE_LENGTH}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverLimit) MaterialTheme.colorScheme.error 
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isOverLimit) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    // Batasi input hanya sampai MAX_MESSAGE_LENGTH + sedikit tolerance
                    if (newText.length <= OpenAIConfig.MAX_MESSAGE_LENGTH + 50) {
                        text = newText
                    }
                },
                placeholder = { 
                    Text("Ketik pesan Anda...") 
                },
                modifier = Modifier.weight(1f),
                enabled = !isLoading,
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                isError = isOverLimit,
                supportingText = if (isOverLimit) {
                    { Text("Pesan terlalu panjang", color = MaterialTheme.colorScheme.error) }
                } else null
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            FloatingActionButton(
                onClick = {
                    if (text.isNotBlank() && !isOverLimit) {
                        onSendMessage(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(56.dp),
                containerColor = if (isOverLimit) MaterialTheme.colorScheme.surfaceVariant 
                                else MaterialTheme.colorScheme.primary,
                contentColor = if (isOverLimit) MaterialTheme.colorScheme.onSurfaceVariant 
                              else MaterialTheme.colorScheme.onPrimary
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Kirim pesan"
                    )
                }
            }
            } // End Row
        } // End Column
    } // End Card
}