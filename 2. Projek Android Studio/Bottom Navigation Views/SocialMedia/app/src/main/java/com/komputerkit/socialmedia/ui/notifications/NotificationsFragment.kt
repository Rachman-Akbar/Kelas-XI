package com.komputerkit.socialmedia.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.komputerkit.socialmedia.data.model.Notification
import com.komputerkit.socialmedia.databinding.FragmentNotificationsBinding
import com.komputerkit.socialmedia.ui.adapter.NotificationAdapter

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            setupRecyclerView()
            loadNotifications()
        } catch (e: Exception) {
            e.printStackTrace()
            showError("Error initializing notifications: ${e.message}")
        }
    }

    private fun setupRecyclerView() {
        notificationAdapter = NotificationAdapter { notification ->
            onNotificationClick(notification)
        }
        
        binding.rvNotifications.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = notificationAdapter
        }
    }

    private fun loadNotifications() {
        // Create dummy notifications
        val notifications = listOf(
            Notification(
                id = "notif1",
                type = "like",
                fromUserId = "user2",
                fromUsername = "jane_smith",
                fromUserProfileUrl = "https://images.unsplash.com/photo-1494790108755-2616b612b47c?w=150&h=150&fit=crop&crop=face",
                message = "liked your post",
                postImageUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=100&h=100&fit=crop",
                timestamp = System.currentTimeMillis() - 1800000, // 30 min ago
                isRead = false
            ),
            Notification(
                id = "notif2",
                type = "comment",
                fromUserId = "user3",
                fromUsername = "mike_wilson",
                fromUserProfileUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face",
                message = "commented on your post: \"Amazing shot!\"",
                postImageUrl = "https://images.unsplash.com/photo-1518837695005-2083093ee35b?w=100&h=100&fit=crop",
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                isRead = false
            ),
            Notification(
                id = "notif3",
                type = "follow",
                fromUserId = "user4",
                fromUsername = "photo_enthusiast",
                fromUserProfileUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face",
                message = "started following you",
                postImageUrl = null,
                timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                isRead = false
            ),
            Notification(
                id = "notif4",
                type = "like",
                fromUserId = "user5",
                fromUsername = "travel_blogger",
                fromUserProfileUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face",
                message = "liked your post",
                postImageUrl = "https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=100&h=100&fit=crop",
                timestamp = System.currentTimeMillis() - 10800000, // 3 hours ago
                isRead = true
            ),
            Notification(
                id = "notif5",
                type = "comment",
                fromUserId = "user6", 
                fromUsername = "nature_lover",
                fromUserProfileUrl = "https://images.unsplash.com/photo-1463453091185-61582044d556?w=150&h=150&fit=crop&crop=face",
                message = "commented on your post: \"Love this perspective!\"",
                postImageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=100&h=100&fit=crop",
                timestamp = System.currentTimeMillis() - 14400000, // 4 hours ago
                isRead = true
            )
        )
        
        notificationAdapter.submitList(notifications)
    }

    private fun onNotificationClick(notification: Notification) {
        Toast.makeText(context, "Clicked: ${notification.fromUsername}", Toast.LENGTH_SHORT).show()
        // Here you would navigate to the relevant post or profile
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}