const { db } = require('./firebase-config');
const fs = require('fs');
const path = require('path');

function generateChatId(userId1, userId2) {
  return userId1 < userId2 ? `${userId1}_${userId2}` : `${userId2}_${userId1}`;
}

function generateMessageId() {
  return Date.now().toString() + Math.random().toString(36).substr(2, 9);
}

async function importChats() {
  try {
    console.log('🔄 Starting chat import...');

    // Read user mapping
    const userMappingPath = path.join(__dirname, 'user-mapping.json');
    if (!fs.existsSync(userMappingPath)) {
      console.error('❌ User mapping not found. Please run import-users.js first.');
      return;
    }

    const userMapping = JSON.parse(fs.readFileSync(userMappingPath, 'utf8'));
    const emailToUidMap = {};
    userMapping.forEach((user) => {
      emailToUidMap[user.originalEmail] = user.uid;
    });

    // Read sample chats data
    const chatsData = JSON.parse(fs.readFileSync(path.join(__dirname, 'sample-chats.json'), 'utf8'));

    let totalMessages = 0;

    for (const chatData of chatsData) {
      try {
        const senderUid = emailToUidMap[chatData.senderEmail];
        const receiverUid = emailToUidMap[chatData.receiverEmail];

        if (!senderUid || !receiverUid) {
          console.log(`⚠️ Skipping chat: User not found for ${chatData.senderEmail} or ${chatData.receiverEmail}`);
          continue;
        }

        const chatId = generateChatId(senderUid, receiverUid);
        console.log(`📝 Processing chat between ${chatData.senderEmail} and ${chatData.receiverEmail}`);

        // Import messages
        for (const messageData of chatData.messages) {
          const messageId = generateMessageId();
          const isFromReceiver = messageData.fromReceiver || false;

          const message = {
            messageId: messageId,
            senderId: isFromReceiver ? receiverUid : senderUid,
            receiverId: isFromReceiver ? senderUid : receiverUid,
            messageText: messageData.messageText,
            timestamp: messageData.timestamp,
            messageType: 'text',
            isRead: false,
          };

          await db.ref(`chats/${chatId}/messages/${messageId}`).set(message);
          totalMessages++;
        }

        // Create/Update chat metadata
        const lastMessage = chatData.messages[chatData.messages.length - 1];
        const lastMessageSender = lastMessage.fromReceiver ? receiverUid : senderUid;

        const chat = {
          chatId: chatId,
          participants: [senderUid, receiverUid],
          lastMessage: lastMessage.messageText,
          lastMessageTime: lastMessage.timestamp,
          lastMessageSender: lastMessageSender,
        };

        await db.ref(`chats/${chatId}`).update(chat);
        console.log(`✅ Created chat with ${chatData.messages.length} messages`);
      } catch (error) {
        console.error(`❌ Error importing chat between ${chatData.senderEmail} and ${chatData.receiverEmail}:`, error.message);
      }
    }

    console.log(`\n🎉 Chat import completed!`);
    console.log(`📊 Imported ${chatsData.length} chats with ${totalMessages} total messages.`);
  } catch (error) {
    console.error('❌ Error importing chats:', error);
  }
}

// Run the import if this file is executed directly
if (require.main === module) {
  importChats()
    .then(() => {
      console.log('✅ Chat import process finished.');
      process.exit(0);
    })
    .catch((error) => {
      console.error('❌ Chat import process failed:', error);
      process.exit(1);
    });
}

module.exports = { importChats };
