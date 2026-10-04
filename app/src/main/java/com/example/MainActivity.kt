package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.StudyBuddyViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.dialogs.AccountSwitcherDialog
import com.example.ui.dialogs.AuthDialog
import com.example.ui.dialogs.NotificationsDialog
import com.example.ui.dialogs.SessionCompletedDialog
import com.example.ui.dialogs.StartStudyDialog
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoutineScreen
import com.example.ui.screens.StudyScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

enum class MainDestination(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "nav_home"),
    FRIENDS("Friends", Icons.Default.People, "nav_friends"),
    STUDY("Study", Icons.Default.Timer, "nav_study"),
    ROUTINE("Routine", Icons.Default.EventNote, "nav_routine"),
    PROFILE("Profile", Icons.Default.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: StudyBuddyViewModel = viewModel()
            val systemDark = isSystemInDarkTheme()
            val isDark by viewModel.isDarkMode.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDark) {
                HscStudyBuddyApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HscStudyBuddyApp(viewModel: StudyBuddyViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val availableUsers by viewModel.availableUsers.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle()
    val liveElapsedSeconds by viewModel.currentElapsedSeconds.collectAsStateWithLifecycle()

    val friendsLiveCards by viewModel.friendsLiveCards.collectAsStateWithLifecycle()
    val pendingRequests by viewModel.pendingRequests.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val filteredSessions by viewModel.filteredSessions.collectAsStateWithLifecycle()
    val historyFilter by viewModel.historyFilter.collectAsStateWithLifecycle()
    val selectedSubjectFilter by viewModel.selectedSubjectFilter.collectAsStateWithLifecycle()

    val weeklyStats by viewModel.weeklyStats.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()

    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val selectedRoutineDay by viewModel.selectedRoutineDay.collectAsStateWithLifecycle()

    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifications by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()

    val groupMessages by viewModel.groupStudyMessages.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val lastCompletedSession by viewModel.lastCompletedSession.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf(MainDestination.HOME) }

    // Dialogs state
    var showStartStudyDialog by remember { mutableStateOf(false) }
    var showAccountSwitcher by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Display user messages via snackbar
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    // BackHandler: If on non-home screen, back button returns to Home
    if (currentScreen != MainDestination.HOME) {
        BackHandler {
            currentScreen = MainDestination.HOME
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 720.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppTopBar(
                    title = "HSC Study Buddy",
                    streakDays = weeklyStats?.streakDays ?: 0,
                    unreadNotifications = unreadNotifications,
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = { viewModel.isDarkMode.value = !isDarkMode },
                    onNotificationsClick = { showNotificationsDialog = true },
                    onAccountClick = { showAccountSwitcher = true }
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("bottom_navigation_bar")
                    ) {
                        MainDestination.values().forEach { destination ->
                            NavigationBarItem(
                                selected = currentScreen == destination,
                                onClick = { currentScreen = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) },
                                modifier = Modifier.testTag(destination.tag)
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Wide Screen Navigation Rail
                if (isWideScreen) {
                    NavigationRail(
                        modifier = Modifier.testTag("wide_navigation_rail")
                    ) {
                        MainDestination.values().forEach { destination ->
                            NavigationRailItem(
                                selected = currentScreen == destination,
                                onClick = { currentScreen = destination },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label
                                    )
                                },
                                label = { Text(destination.label) },
                                modifier = Modifier.testTag(destination.tag)
                            )
                        }
                    }
                }

                // Main Screen Content Box
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    when (currentScreen) {
                        MainDestination.HOME -> {
                            HomeScreen(
                                currentUser = currentUser,
                                activeSession = activeSession,
                                liveElapsedSeconds = liveElapsedSeconds,
                                weeklyStats = weeklyStats,
                                friendsLiveCards = friendsLiveCards,
                                recentSessions = filteredSessions,
                                onStartStudyingClick = { showStartStudyDialog = true },
                                onPauseClick = { viewModel.pauseStudySession() },
                                onResumeClick = { viewModel.resumeStudySession() },
                                onFinishClick = { viewModel.finishStudySession() },
                                onSendCheer = { id, cheer -> viewModel.sendCheer(id, cheer) },
                                onNavigateToFriends = { currentScreen = MainDestination.FRIENDS },
                                onNavigateToStudy = { currentScreen = MainDestination.STUDY }
                            )
                        }

                        MainDestination.FRIENDS -> {
                            FriendsScreen(
                                friendsLiveCards = friendsLiveCards,
                                pendingRequests = pendingRequests,
                                searchResults = searchResults,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                onSendFriendRequest = { viewModel.sendFriendRequest(it) },
                                onAcceptRequest = { viewModel.acceptFriendRequest(it) },
                                onRejectRequest = { viewModel.rejectFriendRequest(it) },
                                onRemoveFriend = { viewModel.removeFriend(it) },
                                onSendCheer = { id, cheer -> viewModel.sendCheer(id, cheer) }
                            )
                        }

                        MainDestination.STUDY -> {
                            StudyScreen(
                                activeSession = activeSession,
                                liveElapsedSeconds = liveElapsedSeconds,
                                sessions = filteredSessions,
                                historyFilter = historyFilter,
                                selectedSubjectFilter = selectedSubjectFilter,
                                groupMessages = groupMessages,
                                onFilterChange = { viewModel.historyFilter.value = it },
                                onSubjectFilterChange = { viewModel.selectedSubjectFilter.value = it },
                                onStartStudyingClick = { showStartStudyDialog = true },
                                onPauseClick = { viewModel.pauseStudySession() },
                                onResumeClick = { viewModel.resumeStudySession() },
                                onFinishClick = { viewModel.finishStudySession() },
                                onDeleteSession = { viewModel.deleteSession(it) },
                                onSendGroupMessage = { text, reaction -> viewModel.sendGroupMessage(text, reaction) }
                            )
                        }

                        MainDestination.ROUTINE -> {
                            RoutineScreen(
                                userGroup = currentUser?.hscGroup ?: "SCIENCE",
                                routines = routines,
                                selectedDay = selectedRoutineDay,
                                onSelectDay = { viewModel.selectedRoutineDay.value = it },
                                onAddRoutine = { day, sub, paper, ch, start, end ->
                                    viewModel.addRoutine(day, sub, paper, ch, start, end)
                                },
                                onDeleteRoutine = { viewModel.deleteRoutine(it) },
                                onToggleCompleted = { viewModel.toggleRoutineCompleted(it) },
                                onStartStudyingFromRoutine = { sub, paper, ch ->
                                    viewModel.startStudySession(sub, paper, ch, "Routine Session")
                                    currentScreen = MainDestination.STUDY
                                }
                            )
                        }

                        MainDestination.PROFILE -> {
                            ProfileScreen(
                                currentUser = currentUser,
                                weeklyStats = weeklyStats,
                                leaderboard = leaderboard,
                                isStudying = activeSession != null,
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.isDarkMode.value = it },
                                onUpdatePrivacy = { st, sub, ch, dur, req ->
                                    viewModel.updatePrivacy(st, sub, ch, dur, req)
                                },
                                onSwitchAccount = { showAccountSwitcher = true },
                                onLogout = {
                                    viewModel.logout {
                                        showAuthDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Modals
        if (showStartStudyDialog) {
            StartStudyDialog(
                userGroup = currentUser?.hscGroup ?: "SCIENCE",
                onDismiss = { showStartStudyDialog = false },
                onStartSession = { subject, paper, chapter, goal ->
                    viewModel.startStudySession(subject, paper, chapter, goal)
                    showStartStudyDialog = false
                    currentScreen = MainDestination.STUDY
                }
            )
        }

        if (showAccountSwitcher) {
            AccountSwitcherDialog(
                users = availableUsers,
                currentUserId = currentUserId,
                onSelectUser = { uid ->
                    viewModel.switchAccount(uid)
                    showAccountSwitcher = false
                },
                onOpenAuth = {
                    showAccountSwitcher = false
                    showAuthDialog = true
                },
                onDismiss = { showAccountSwitcher = false }
            )
        }

        if (showAuthDialog) {
            AuthDialog(
                onDismiss = { showAuthDialog = false },
                onLogin = { id, pass ->
                    viewModel.login(id, pass) {
                        showAuthDialog = false
                    }
                },
                onRegister = { u, fn, em, pass, clg, grp, yr, mainSubs, optSub ->
                    viewModel.register(u, fn, em, pass, clg, grp, yr, mainSubs, optSub) {
                        showAuthDialog = false
                    }
                }
            )
        }

        if (showNotificationsDialog) {
            NotificationsDialog(
                notifications = notifications,
                onDismiss = { showNotificationsDialog = false },
                onMarkAsRead = { viewModel.markNotificationRead(it) },
                onClearAll = { viewModel.clearAllNotifications() }
            )
        }

        lastCompletedSession?.let { session ->
            SessionCompletedDialog(
                session = session,
                streakDays = weeklyStats?.streakDays ?: 1,
                onDismiss = { viewModel.dismissCompletedSessionModal() }
            )
        }
    }
}
