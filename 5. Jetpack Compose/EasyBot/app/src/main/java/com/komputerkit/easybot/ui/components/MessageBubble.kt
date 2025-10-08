package com.komputerkit.easybot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.komputerkit.easybot.data.Message
import com.komputerkit.easybot.data.MessageRole

@Composable
fun MessageBubble(
    message: Message,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.role == MessageRole.USER) {
            Arrangement.End
        } else {
            Arrangement.Start
        }
    ) {
        if (message.role == MessageRole.USER) {
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        Card(
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.role == MessageRole.USER) 16.dp else 4.dp,
                bottomEnd = if (message.role == MessageRole.USER) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = when (message.role) {
                    MessageRole.USER -> MaterialTheme.colorScheme.primary
                    MessageRole.MODEL -> MaterialTheme.colorScheme.surfaceVariant
                    MessageRole.TYPING -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = when (message.role) {
                    MessageRole.USER -> MaterialTheme.colorScheme.onPrimary
                    MessageRole.MODEL -> MaterialTheme.colorScheme.onSurfaceVariant
                    MessageRole.TYPING -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = if (message.role == MessageRole.TYPING) {
                    MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic)
                } else {
                    MaterialTheme.typography.bodyMedium
                }
            )
        }
        
        if (message.role != MessageRole.USER) {
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}