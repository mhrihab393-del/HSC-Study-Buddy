package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.FriendLiveCard
import com.example.ui.StudyBuddyViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.components.LivePulseDot
import com.example.ui.components.LiveStatusBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LiveEmerald
import com.example.ui.theme.PauseAmber

@Composable
fun FriendsScreen(
    friendsLiveCards: List<FriendLiveCard>,
    pendingRequests: List<Pair<FriendshipEntity, UserEntity>>,
    searchResults: List<UserEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSendFriendRequest: (String) -> Unit,
    onAcceptRequest: (Long) -> Unit,
    onRejectRequest: (Long) -> Unit,
    onRemoveFriend: (String) -> Unit,
    onSendCheer: (recipientId: String, cheer: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFriendForProfile by remember { mutableStateOf<FriendLiveCard?>(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Friends (All/Live), 1 = Pending Requests

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("friends_screen")
    ) {
        // Search bar for other students
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search HSC students by username or college...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("friend_search_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Search results view if search active
            if (searchQuery.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Search Results (${searchResults.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (searchResults.isEmpty()) {
                            Text(
                                text = "No students found matching \"$searchQuery\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            searchResults.forEach { user ->
                                val isAlreadyFriend = friendsLiveCards.any { it.user.id == user.id }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        StudentAvatar(name = user.fullName, size = 36.dp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(
                                                "@${user.username} • ${user.collegeName}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    if (isAlreadyFriend) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer
                                        ) {
                                            Text(
                                                "Friends",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Button(
                                            onClick = { onSendFriendRequest(user.id) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("send_request_button_${user.username}")
                                        ) {
                                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Add", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("My Study Buddies (${friendsLiveCards.size})", fontWeight = FontWeight.SemiBold)
                    }
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Requests", fontWeight = FontWeight.SemiBold)
                        if (pendingRequests.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    "${pendingRequests.size}",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        // Content
        if (selectedTab == 0) {
            if (friendsLiveCards.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    EmptyStateView(
                        icon = Icons.Default.Groups,
                        title = "No study buddies yet",
                        message = "Search for your classmates and friends by username or college name above!"
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(friendsLiveCards) { friend ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedFriendForProfile = friend }
                                .testTag("friend_card_${friend.user.username}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StudentAvatar(
                                        name = friend.user.fullName,
                                        size = 48.dp,
                                        isLive = friend.isStudying
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = friend.user.fullName,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleSmall
                                            )
                                            if (friend.isStudying) {
                                                LivePulseDot(size = 8.dp)
                                            }
                                        }
                                        Text(
                                            text = "@${friend.user.username} • ${friend.user.collegeName}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "${friend.user.hscGroup} • Session ${friend.user.sessionYear}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Quick Cheer emoji action
                                    IconButton(
                                        onClick = { onSendCheer(friend.user.id, "🔥") },
                                        modifier = Modifier.testTag("cheer_friend_${friend.user.id}")
                                    ) {
                                        Text("🔥", fontSize = 22.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live Status Box
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (friend.isStudying) {
                                        LiveEmerald.copy(alpha = 0.1f)
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (friend.isStudying) "Studying ${friend.displaySubject}" else "Not studying right now",
                                                fontWeight = if (friend.isStudying) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                color = if (friend.isStudying) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (friend.isStudying && (friend.displayChapter.isNotBlank() || friend.displayPaper.isNotBlank())) {
                                                Text(
                                                    text = "${friend.displayPaper} • ${friend.displayChapter}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        if (friend.isStudying) {
                                            Text(
                                                text = "⏱️ ${StudyBuddyViewModel.formatDuration(friend.initialSeconds)}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = LiveEmerald
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Pending Requests Tab
            if (pendingRequests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    EmptyStateView(
                        icon = Icons.Default.GroupAdd,
                        title = "No pending requests",
                        message = "When other HSC students send you a buddy request, it will appear here."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(pendingRequests) { (friendship, sender) ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StudentAvatar(name = sender.fullName, size = 44.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sender.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${sender.collegeName} • ${sender.hscGroup}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    IconButton(
                                        onClick = { onAcceptRequest(friendship.id) },
                                        modifier = Modifier.testTag("accept_request_${sender.username}")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = "Accept", tint = LiveEmerald)
                                    }
                                    IconButton(
                                        onClick = { onRejectRequest(friendship.id) },
                                        modifier = Modifier.testTag("reject_request_${sender.username}")
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Friend Profile Modal
        selectedFriendForProfile?.let { card ->
            FriendProfileDialog(
                friendCard = card,
                onDismiss = { selectedFriendForProfile = null },
                onRemove = {
                    onRemoveFriend(card.user.id)
                    selectedFriendForProfile = null
                },
                onSendCheer = { cheer ->
                    onSendCheer(card.user.id, cheer)
                }
            )
        }
    }
}

@Composable
fun FriendProfileDialog(
    friendCard: FriendLiveCard,
    onDismiss: () -> Unit,
    onRemove: () -> Unit,
    onSendCheer: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                StudentAvatar(
                    name = friendCard.user.fullName,
                    size = 64.dp,
                    isLive = friendCard.isStudying
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = friendCard.user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "@${friendCard.user.username}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = friendCard.user.collegeName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${friendCard.user.hscGroup} • HSC ${friendCard.user.sessionYear}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Current study status
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (friendCard.isStudying) LiveEmerald.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LivePulseDot(
                                color = if (friendCard.isStudying) LiveEmerald else Color.Gray,
                                isPulsing = friendCard.isStudying
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (friendCard.isStudying) "Currently Studying" else "Not Studying",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (friendCard.isStudying) LiveEmerald else Color.Gray
                            )
                        }
                        if (friendCard.isStudying) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(friendCard.displaySubject, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("${friendCard.displayPaper} • ${friendCard.displayChapter}", fontSize = 11.sp)
                            Text(
                                "Live Duration: ${StudyBuddyViewModel.formatDuration(friendCard.initialSeconds)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = LiveEmerald
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cheer buttons
                Text("Send Study Cheer:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("🔥", "📚", "☕", "💯").forEach { emoji ->
                        FilledTonalButton(
                            onClick = { onSendCheer(emoji) },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(emoji, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onRemove,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.PersonRemove, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove Study Buddy", fontSize = 12.sp)
                }
            }
        }
    }
}
