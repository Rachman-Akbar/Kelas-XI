package com.komputerkit.easybot.ui.previews

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.komputerkit.easybot.data.Message
import com.komputerkit.easybot.data.MessageRole
import com.komputerkit.easybot.ui.components.MessageBubble
import com.komputerkit.easybot.ui.components.MessageInput
import com.komputerkit.easybot.ui.screen.ChatScreen
import com.komputerkit.easybot.ui.theme.EasyBotTheme

@Preview(showBackground = true, name = "User Message")
@Composable
fun UserMessagePreview() {
    EasyBotTheme {
        MessageBubble(
            message = Message(
                text = "Halo, apa kabar?",
                role = MessageRole.USER
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Bot Message")
@Composable
fun BotMessagePreview() {
    EasyBotTheme {
        MessageBubble(
            message = Message(
                text = "Halo! Saya baik-baik saja. Terima kasih sudah bertanya. Ada yang bisa saya bantu hari ini?",
                role = MessageRole.MODEL
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Typing Message")
@Composable
fun TypingMessagePreview() {
    EasyBotTheme {
        MessageBubble(
            message = Message(
                text = "Typing...",
                role = MessageRole.TYPING
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Message Input")
@Composable
fun MessageInputPreview() {
    EasyBotTheme {
        MessageInput(
            onSendMessage = { },
            isLoading = false,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Message Input Loading")
@Composable
fun MessageInputLoadingPreview() {
    EasyBotTheme {
        MessageInput(
            onSendMessage = { },
            isLoading = true,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Chat Screen")
@Composable
fun ChatScreenPreview() {
    EasyBotTheme {
        ChatScreen()
    }
}