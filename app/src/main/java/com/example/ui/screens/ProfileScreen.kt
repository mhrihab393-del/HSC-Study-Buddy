package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.repository.LeaderboardItem
import com.example.data.repository.WeeklyStats
import com.example.ui.StudyBuddyViewModel
import com.example.ui.components.LiveStatusBadge
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.BadgeGold
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.LiveEmerald

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    weeklyStats: WeeklyStats?,
    leaderboard: List<LeaderboardItem>,
    isStudying: Boolean,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    onUpdatePrivacy: (status: Boolean, sub: Boolean, ch: Boolean, dur: Boolean, req: Boolean) -> Unit,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var profileTab by remember { mutableStateOf(0) } // 0 = Stats & Overview, 1 = Leaderboard, 2 = Privacy & Settings

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    StudentAvatar(
                        name = currentUser?.fullName ?: "Student",
                        size = 72.dp,
                        isLive = isStudying
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = currentUser?.fullName ?: "HSC Student",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${currentUser?.username ?: "student"} • ${currentUser?.email ?: ""}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentUser?.collegeName ?: "College"} (${currentUser?.hscGroup ?: "Science"})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "HSC Examination Session ${currentUser?.sessionYear ?: "2026"}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    LiveStatusBadge(isStudying = isStudying)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metrics Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "🔥 ${weeklyStats?.streakDays ?: 0} Days",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = FlameOrange
                            )
                            Text("Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = StudyBuddyViewModel.formatHoursMinutes(weeklyStats?.totalSecondsThisWeek ?: 0L),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("This Week", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${weeklyStats?.totalSessionsCount ?: 0}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text("Sessions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(selectedTabIndex = profileTab) {
                Tab(
                    selected = profileTab == 0,
                    onClick = { profileTab = 0 },
                    text = { Text("Statistics", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = profileTab == 1,
                    onClick = { profileTab = 1 },
                    text = { Text("Leaderboard", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = profileTab == 2,
                    onClick = { profileTab = 2 },
                    text = { Text("Privacy", fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        // Tab Content
        when (profileTab) {
            0 -> {
                // STATISTICS TAB
                // Daily Study Bar Chart (Canvas)
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Daily Study Hours (Last 7 Days)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            val dailySec = weeklyStats?.dailySeconds ?: emptyMap()
                            val daysList = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
                            val maxSeconds = (dailySec.values.maxOrNull() ?: 1L).coerceAtLeast(3600L)

                            val primaryBarColor = MaterialTheme.colorScheme.primary
                            val containerBarColor = MaterialTheme.colorScheme.surfaceVariant

                            // Chart Canvas
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val barWidth = size.width / (daysList.size * 2f)
                                    val spacing = size.width / daysList.size

                                    daysList.forEachIndexed { index, day ->
                                        val sec = dailySec[day] ?: 0L
                                        val heightFraction = (sec.toFloat() / maxSeconds).coerceIn(0.05f, 1f)
                                        val barHeight = (size.height - 24.dp.toPx()) * heightFraction

                                        val x = (index * spacing) + (spacing - barWidth) / 2f
                                        val y = size.height - 24.dp.toPx() - barHeight

                                        // Background slot bar
                                        drawRoundRect(
                                            color = containerBarColor,
                                            topLeft = Offset(x, 0f),
                                            size = Size(barWidth, size.height - 24.dp.toPx()),
                                            cornerRadius = CornerRadius(8.dp.toPx())
                                        )

                                        // Filled value bar
                                        drawRoundRect(
                                            color = primaryBarColor,
                                            topLeft = Offset(x, y),
                                            size = Size(barWidth, barHeight),
                                            cornerRadius = CornerRadius(8.dp.toPx())
                                        )
                                    }
                                }
                            }

                            // Day labels
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                daysList.forEach { day ->
                                    val sec = dailySec[day] ?: 0L
                                    val hrs = (sec / 3600f)
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(day, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(String.format("%.1fh", hrs), fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Subject-wise Study Distribution
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Subject-wise Distribution",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(Icons.Default.PieChart, contentDescription = null, tint = Color(0xFF7C3AED))
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            val subMap = weeklyStats?.subjectSeconds ?: emptyMap()
                            val totalSec = (weeklyStats?.totalSecondsThisWeek ?: 1L).coerceAtLeast(1L)

                            if (subMap.isEmpty()) {
                                Text(
                                    text = "Complete study sessions to see your subject breakdown.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                subMap.entries.sortedByDescending { it.value }.take(5).forEach { (sub, sec) ->
                                    val fraction = (sec.toFloat() / totalSec).coerceIn(0f, 1f)
                                    val percentage = (fraction * 100).toInt()

                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(sub, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "${StudyBuddyViewModel.formatHoursMinutes(sec)} ($percentage%)",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = { fraction },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(8.dp)
                                                .clip(CircleShape),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // FRIEND LEADERBOARD TAB
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Weekly Friend Leaderboard", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "Friendly peer motivation! Consistency beats cramming.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(leaderboard) { item ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Rank Badge
                            Surface(
                                shape = CircleShape,
                                color = when (item.rank) {
                                    1 -> BadgeGold
                                    2 -> Color(0xFF94A3B8)
                                    3 -> Color(0xFFD97706)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "#${item.rank}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (item.rank <= 3) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            StudentAvatar(name = item.user.fullName, size = 42.dp)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (item.isCurrentUser) "${item.user.fullName} (You)" else item.user.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = item.user.collegeName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "🔥 ${item.streakDays} Day Streak",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FlameOrange
                                )
                            }

                            Text(
                                text = StudyBuddyViewModel.formatHoursMinutes(item.weeklySeconds),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            2 -> {
                // PRIVACY & SETTINGS TAB
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Privacy Controls",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Control what your study buddies see about your sessions:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            var showStatus by remember(currentUser) { mutableStateOf(currentUser?.showStudyStatus ?: true) }
                            var showSubject by remember(currentUser) { mutableStateOf(currentUser?.showSubject ?: true) }
                            var showChapter by remember(currentUser) { mutableStateOf(currentUser?.showChapter ?: true) }
                            var showDuration by remember(currentUser) { mutableStateOf(currentUser?.showStudyDuration ?: true) }
                            var allowRequests by remember(currentUser) { mutableStateOf(currentUser?.allowFriendRequests ?: true) }

                            PrivacyRow(
                                title = "Show Current Study Status",
                                subtitle = "Let friends know when you are actively studying",
                                checked = showStatus,
                                onCheckedChange = {
                                    showStatus = it
                                    onUpdatePrivacy(showStatus, showSubject, showChapter, showDuration, allowRequests)
                                }
                            )

                            PrivacyRow(
                                title = "Show Subject",
                                subtitle = "Share subject name (e.g. Physics, Chemistry)",
                                checked = showSubject,
                                onCheckedChange = {
                                    showSubject = it
                                    onUpdatePrivacy(showStatus, showSubject, showChapter, showDuration, allowRequests)
                                }
                            )

                            PrivacyRow(
                                title = "Show Chapter / Topic",
                                subtitle = "Share specific chapter (e.g. Dynamics, Qualitative Chem)",
                                checked = showChapter,
                                onCheckedChange = {
                                    showChapter = it
                                    onUpdatePrivacy(showStatus, showSubject, showChapter, showDuration, allowRequests)
                                }
                            )

                            PrivacyRow(
                                title = "Show Live Study Duration",
                                subtitle = "Show live ticking timer to friends",
                                checked = showDuration,
                                onCheckedChange = {
                                    showDuration = it
                                    onUpdatePrivacy(showStatus, showSubject, showChapter, showDuration, allowRequests)
                                }
                            )

                            PrivacyRow(
                                title = "Allow Friend Requests",
                                subtitle = "Allow other HSC students to find & add you",
                                checked = allowRequests,
                                onCheckedChange = {
                                    allowRequests = it
                                    onUpdatePrivacy(showStatus, showSubject, showChapter, showDuration, allowRequests)
                                }
                            )
                        }
                    }
                }

                // App Theme Settings
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "App Preferences",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Comfortable night study theme", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = onToggleDarkMode
                                )
                            }
                        }
                    }
                }

                // Account Management
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "Account Management",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = onSwitchAccount,
                                modifier = Modifier.fillMaxWidth().testTag("profile_switch_account_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.SwitchAccount, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Switch Student Profile / Register Account")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onLogout,
                                modifier = Modifier.fillMaxWidth().testTag("profile_logout_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Log Out")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
