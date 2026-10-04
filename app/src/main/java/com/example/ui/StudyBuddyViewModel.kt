package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActiveSessionEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupStudyMessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudyRoutineEntity
import com.example.data.local.entity.StudySessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.HscGroup
import com.example.data.repository.FriendLiveCard
import com.example.data.repository.LeaderboardItem
import com.example.data.repository.StudyBuddyRepository
import com.example.data.repository.WeeklyStats
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter {
    ALL, TODAY, THIS_WEEK, THIS_MONTH
}

class StudyBuddyViewModel(application: Application) : AndroidViewModel(application) {

    val repository = StudyBuddyRepository(AppDatabase.getInstance(application))

    private val _currentUserId = MutableStateFlow("user_rimonium")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        repository.getUser(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _availableUsers = MutableStateFlow<List<UserEntity>>(emptyList())
    val availableUsers: StateFlow<List<UserEntity>> = _availableUsers.asStateFlow()

    // Active Study Timer
    val activeSession: StateFlow<ActiveSessionEntity?> = _currentUserId.flatMapLatest { id ->
        repository.getActiveSession(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentElapsedSeconds = MutableStateFlow(0L)
    val currentElapsedSeconds: StateFlow<Long> = _currentElapsedSeconds.asStateFlow()

    // Friends Live Cards
    val friendsLiveCards: StateFlow<List<FriendLiveCard>> = _currentUserId.flatMapLatest { id ->
        repository.getFriendsLiveCards(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending friend requests
    val pendingRequests: StateFlow<List<Pair<FriendshipEntity, UserEntity>>> = _currentUserId.flatMapLatest { id ->
        repository.getPendingRequests(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search
    val searchQuery = MutableStateFlow("")
    val searchResults: StateFlow<List<UserEntity>> = combine(_currentUserId, searchQuery) { uid, query ->
        Pair(uid, query)
    }.flatMapLatest { (uid, query) ->
        if (query.isBlank()) flowOf(emptyList()) else repository.searchUsers(query, uid)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // History & Filter
    val historyFilter = MutableStateFlow(HistoryFilter.ALL)
    val selectedSubjectFilter = MutableStateFlow<String?>(null)

    private val rawSessions: StateFlow<List<StudySessionEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getStudySessions(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredSessions: StateFlow<List<StudySessionEntity>> = combine(
        rawSessions,
        historyFilter,
        selectedSubjectFilter
    ) { sessions, filter, subFilter ->
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = dateFormat.format(Date())
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000L)
        val monthAgo = now - (30 * 24 * 60 * 60 * 1000L)

        sessions.filter { s ->
            val matchesTime = when (filter) {
                HistoryFilter.ALL -> true
                HistoryFilter.TODAY -> s.dateString == todayStr
                HistoryFilter.THIS_WEEK -> s.endTimeMillis >= weekAgo
                HistoryFilter.THIS_MONTH -> s.endTimeMillis >= monthAgo
            }
            val matchesSubject = subFilter == null || s.subjectName.contains(subFilter, ignoreCase = true)
            matchesTime && matchesSubject
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Stats
    private val _weeklyStats = MutableStateFlow<WeeklyStats?>(null)
    val weeklyStats: StateFlow<WeeklyStats?> = _weeklyStats.asStateFlow()

    // Leaderboard
    private val _leaderboard = MutableStateFlow<List<LeaderboardItem>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardItem>> = _leaderboard.asStateFlow()

    // Routines
    val routines: StateFlow<List<StudyRoutineEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getRoutines(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedRoutineDay = MutableStateFlow(getCurrentBanglaDayOfWeek())

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = _currentUserId.flatMapLatest { id ->
        repository.getNotifications(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationCount: StateFlow<Int> = _currentUserId.flatMapLatest { id ->
        repository.getUnreadNotificationCount(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Group Study Room
    val groupStudyMessages: StateFlow<List<GroupStudyMessageEntity>> =
        repository.getGroupStudyMessages().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI feedback
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Dark Mode Toggle
    val isDarkMode = MutableStateFlow(false)

    // Last completed session for celebration modal
    private val _lastCompletedSession = MutableStateFlow<StudySessionEntity?>(null)
    val lastCompletedSession: StateFlow<StudySessionEntity?> = _lastCompletedSession.asStateFlow()

    init {
        loadInitialData()
        startLiveTimerTicker()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Give room DB an instant to initialize pre-populated data if brand new
            delay(150)
            refreshUsersList()
            refreshStatsAndLeaderboard()
        }
    }

    private fun startLiveTimerTicker() {
        viewModelScope.launch {
            while (true) {
                val active = activeSession.value
                if (active != null) {
                    val now = System.currentTimeMillis()
                    if (active.isPaused) {
                        _currentElapsedSeconds.value = active.accumulatedSeconds
                    } else {
                        val add = (now - active.lastResumedAtMillis) / 1000
                        _currentElapsedSeconds.value = active.accumulatedSeconds + add
                    }
                } else {
                    _currentElapsedSeconds.value = 0L
                }
                delay(1000)
            }
        }
    }

    fun refreshUsersList() {
        viewModelScope.launch {
            _availableUsers.value = repository.getAllUsersDirect()
        }
    }

    fun refreshStatsAndLeaderboard() {
        viewModelScope.launch {
            val uid = _currentUserId.value
            _weeklyStats.value = repository.calculateStats(uid)
            _leaderboard.value = repository.getLeaderboard(uid)
        }
    }

    fun switchAccount(userId: String) {
        _currentUserId.value = userId
        refreshStatsAndLeaderboard()
        showMessage("Switched to student profile: $userId")
    }

    fun login(identifier: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.login(identifier, pass)
            result.onSuccess { user ->
                _currentUserId.value = user.id
                refreshUsersList()
                refreshStatsAndLeaderboard()
                showMessage("Welcome back, ${user.fullName}!")
                onSuccess()
            }.onFailure {
                showMessage("Login failed: ${it.message}")
            }
        }
    }

    fun register(
        username: String,
        fullName: String,
        email: String,
        password: String,
        college: String,
        group: String,
        sessionYear: String,
        selectedMain: List<String>,
        selectedOptional: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(
                username, fullName, email, password, college, group, sessionYear, selectedMain, selectedOptional
            )
            result.onSuccess { user ->
                _currentUserId.value = user.id
                refreshUsersList()
                refreshStatsAndLeaderboard()
                showMessage("Welcome to HSC Study Buddy, ${user.fullName}!")
                onSuccess()
            }.onFailure {
                showMessage("Sign Up failed: ${it.message}")
            }
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        // Fallback to demo account or clear
        showMessage("Logged out successfully.")
        onLoggedOut()
    }

    // Study Timer Actions
    fun startStudySession(
        subject: String,
        paper: String,
        chapter: String,
        goal: String
    ) {
        viewModelScope.launch {
            repository.startSession(
                userId = _currentUserId.value,
                subjectName = subject,
                paper = paper,
                chapterTopic = chapter,
                studyGoal = goal
            )
            showMessage("Study session started: $subject")
        }
    }

    fun pauseStudySession() {
        viewModelScope.launch {
            repository.pauseSession(_currentUserId.value)
        }
    }

    fun resumeStudySession() {
        viewModelScope.launch {
            repository.resumeSession(_currentUserId.value)
        }
    }

    fun finishStudySession() {
        viewModelScope.launch {
            val session = repository.finishSession(_currentUserId.value)
            if (session != null) {
                _lastCompletedSession.value = session
                refreshStatsAndLeaderboard()
                showMessage("Great job! Session completed (${formatDuration(session.durationSeconds)})")
            }
        }
    }

    fun dismissCompletedSessionModal() {
        _lastCompletedSession.value = null
    }

    fun deleteSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            refreshStatsAndLeaderboard()
            showMessage("Study session deleted.")
        }
    }

    fun cancelStudySession() {
        viewModelScope.launch {
            repository.cancelActiveSession(_currentUserId.value)
            showMessage("Study session cancelled.")
        }
    }

    // Friends
    fun sendFriendRequest(targetUserId: String) {
        viewModelScope.launch {
            val res = repository.sendFriendRequest(_currentUserId.value, targetUserId)
            res.onSuccess {
                showMessage(it)
                searchQuery.value = ""
            }.onFailure {
                showMessage(it.message ?: "Could not send friend request")
            }
        }
    }

    fun acceptFriendRequest(friendshipId: Long) {
        viewModelScope.launch {
            repository.acceptFriendRequest(friendshipId, _currentUserId.value)
            refreshStatsAndLeaderboard()
            showMessage("Friend request accepted!")
        }
    }

    fun rejectFriendRequest(friendshipId: Long) {
        viewModelScope.launch {
            repository.rejectFriendRequest(friendshipId)
            showMessage("Friend request declined.")
        }
    }

    fun removeFriend(friendUserId: String) {
        viewModelScope.launch {
            repository.removeFriend(_currentUserId.value, friendUserId)
            refreshStatsAndLeaderboard()
            showMessage("Friend removed.")
        }
    }

    fun sendCheer(recipientId: String, cheerEmoji: String) {
        viewModelScope.launch {
            currentUser.value?.let { me ->
                repository.sendCheer(me, recipientId, cheerEmoji)
                showMessage("Sent cheer $cheerEmoji to your study buddy!")
            }
        }
    }

    // Routines
    fun addRoutine(
        dayOfWeek: Int,
        subject: String,
        paper: String,
        chapter: String,
        start: String,
        end: String
    ) {
        viewModelScope.launch {
            repository.addRoutine(_currentUserId.value, dayOfWeek, subject, paper, chapter, start, end)
            showMessage("Routine added for ${getDayName(dayOfWeek)}")
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
            showMessage("Routine removed.")
        }
    }

    fun toggleRoutineCompleted(routine: StudyRoutineEntity) {
        viewModelScope.launch {
            repository.markRoutineCompleted(routine.id, !routine.isCompletedToday)
        }
    }

    // Privacy Controls
    fun updatePrivacy(
        showStatus: Boolean,
        showSubject: Boolean,
        showChapter: Boolean,
        showDuration: Boolean,
        allowRequests: Boolean
    ) {
        viewModelScope.launch {
            repository.updatePrivacy(
                _currentUserId.value,
                showStatus,
                showSubject,
                showChapter,
                showDuration,
                allowRequests
            )
            showMessage("Privacy settings updated.")
        }
    }

    // Notifications
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            repository.clearNotifications(_currentUserId.value)
            showMessage("All notifications cleared.")
        }
    }

    // Group Study
    fun sendGroupMessage(text: String, reaction: String) {
        viewModelScope.launch {
            currentUser.value?.let { me ->
                repository.postGroupStudyMessage(me, text, reaction)
            }
        }
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    companion object {
        fun formatDuration(totalSeconds: Long): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        }

        fun formatHoursMinutes(totalSeconds: Long): String {
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            return if (hours > 0) {
                "${hours}h ${minutes}m"
            } else {
                "${minutes}m"
            }
        }

        fun getDayName(day: Int): String {
            return when (day) {
                0 -> "Saturday"
                1 -> "Sunday"
                2 -> "Monday"
                3 -> "Tuesday"
                4 -> "Wednesday"
                5 -> "Thursday"
                6 -> "Friday"
                else -> "Saturday"
            }
        }

        fun getCurrentBanglaDayOfWeek(): Int {
            val cal = Calendar.getInstance()
            return when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> 0
                Calendar.SUNDAY -> 1
                Calendar.MONDAY -> 2
                Calendar.TUESDAY -> 3
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY -> 5
                Calendar.FRIDAY -> 6
                else -> 0
            }
        }
    }
}
