package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActiveSessionEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupStudyMessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudyRoutineEntity
import com.example.data.local.entity.StudySessionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class FriendLiveCard(
    val user: UserEntity,
    val friendshipId: Long,
    val isStudying: Boolean,
    val isPaused: Boolean,
    val displaySubject: String,
    val displayPaper: String,
    val displayChapter: String,
    val studyGoal: String,
    val startedAtMillis: Long,
    val initialSeconds: Long
)

data class LeaderboardItem(
    val user: UserEntity,
    val weeklySeconds: Long,
    val streakDays: Int,
    val rank: Int,
    val isCurrentUser: Boolean
)

data class WeeklyStats(
    val totalSecondsThisWeek: Long,
    val totalSecondsToday: Long,
    val dailySeconds: Map<String, Long>, // "Sat", "Sun", "Mon", ...
    val subjectSeconds: Map<String, Long>,
    val streakDays: Int,
    val totalSessionsCount: Int
)

class StudyBuddyRepository(private val db: AppDatabase) {

    private val userDao = db.userDao()
    private val sessionDao = db.studySessionDao()
    private val activeDao = db.activeSessionDao()
    private val friendshipDao = db.friendshipDao()
    private val routineDao = db.studyRoutineDao()
    private val notificationDao = db.notificationDao()
    private val groupStudyDao = db.groupStudyDao()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Current user
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    suspend fun getUserDirect(userId: String): UserEntity? = userDao.getUserByIdDirect(userId)

    suspend fun getAllUsersDirect(): List<UserEntity> = userDao.getAllUsersDirect()

    // Auth
    suspend fun login(identifier: String, password: String):Result<UserEntity> {
        val user = userDao.getUserByUsernameOrEmail(identifier)
        return if (user != null && user.passwordHash == password) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid username/email or password"))
        }
    }

    suspend fun register(
        username: String,
        fullName: String,
        email: String,
        password: String,
        college: String,
        group: String,
        sessionYear: String,
        selectedMain: List<String>,
        selectedOptional: String
    ): Result<UserEntity> {
        val existing = userDao.getUserByUsernameOrEmail(username)
        if (existing != null) {
            return Result.failure(Exception("Username already taken"))
        }
        val existingEmail = userDao.getUserByUsernameOrEmail(email)
        if (existingEmail != null) {
            return Result.failure(Exception("Email already registered"))
        }

        val newUser = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            username = username.trim().lowercase(),
            fullName = fullName.trim(),
            email = email.trim().lowercase(),
            passwordHash = password,
            collegeName = college.trim(),
            hscGroup = group,
            sessionYear = sessionYear.trim(),
            selectedMainSubjects = selectedMain.joinToString(","),
            selectedOptionalSubject = selectedOptional,
            avatarUrl = "avatar_boy_1"
        )
        userDao.insertUser(newUser)
        return Result.success(newUser)
    }

    suspend fun updateUserProfile(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun updatePrivacy(
        userId: String,
        showStatus: Boolean,
        showSubject: Boolean,
        showChapter: Boolean,
        showDuration: Boolean,
        allowRequests: Boolean
    ) {
        userDao.updatePrivacy(userId, showStatus, showSubject, showChapter, showDuration, allowRequests)
    }

    // Active Study Session
    fun getActiveSession(userId: String): Flow<ActiveSessionEntity?> = activeDao.getActiveSession(userId)

    suspend fun startSession(
        userId: String,
        subjectName: String,
        paper: String,
        chapterTopic: String,
        studyGoal: String
    ) {
        val now = System.currentTimeMillis()
        val session = ActiveSessionEntity(
            userId = userId,
            subjectName = subjectName,
            paper = paper,
            chapterTopic = chapterTopic,
            studyGoal = studyGoal,
            startedAtMillis = now,
            isPaused = false,
            accumulatedSeconds = 0,
            lastResumedAtMillis = now
        )
        activeDao.setActiveSession(session)
    }

    suspend fun pauseSession(userId: String) {
        val active = activeDao.getActiveSessionDirect(userId) ?: return
        if (!active.isPaused) {
            val now = System.currentTimeMillis()
            val additionalSec = (now - active.lastResumedAtMillis) / 1000
            val totalSec = active.accumulatedSeconds + additionalSec
            activeDao.setActiveSession(
                active.copy(
                    isPaused = true,
                    accumulatedSeconds = totalSec,
                    lastResumedAtMillis = now
                )
            )
        }
    }

    suspend fun resumeSession(userId: String) {
        val active = activeDao.getActiveSessionDirect(userId) ?: return
        if (active.isPaused) {
            activeDao.setActiveSession(
                active.copy(
                    isPaused = false,
                    lastResumedAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun finishSession(userId: String): StudySessionEntity? {
        val active = activeDao.getActiveSessionDirect(userId) ?: return null
        val now = System.currentTimeMillis()
        val additionalSec = if (active.isPaused) 0 else (now - active.lastResumedAtMillis) / 1000
        val finalSeconds = (active.accumulatedSeconds + additionalSec).coerceAtLeast(1)

        val completed = StudySessionEntity(
            userId = userId,
            subjectName = active.subjectName,
            paper = active.paper,
            chapterTopic = active.chapterTopic,
            studyGoal = active.studyGoal,
            startTimeMillis = active.startedAtMillis,
            endTimeMillis = now,
            durationSeconds = finalSeconds,
            dateString = dateFormat.format(Date(now)),
            isCompleted = true
        )
        sessionDao.insertSession(completed)
        activeDao.clearActiveSession(userId)
        return completed
    }

    suspend fun cancelActiveSession(userId: String) {
        activeDao.clearActiveSession(userId)
    }

    // Study History
    fun getStudySessions(userId: String): Flow<List<StudySessionEntity>> =
        sessionDao.getSessionsForUser(userId)

    suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSession(sessionId)
    }

    // Friends & Live Study Feed
    fun getFriendsLiveCards(currentUserId: String): Flow<List<FriendLiveCard>> {
        return combine(
            friendshipDao.getAcceptedFriendships(currentUserId),
            activeDao.getAllActiveSessions()
        ) { friendships, activeSessions ->
            val activeMap = activeSessions.associateBy { it.userId }
            val cards = mutableListOf<FriendLiveCard>()

            for (f in friendships) {
                val friendId = if (f.requesterId == currentUserId) f.receiverId else f.requesterId
                val friendUser = userDao.getUserByIdDirect(friendId) ?: continue
                val active = activeMap[friendId]

                val isStudying = active != null && friendUser.showStudyStatus
                val isPaused = active?.isPaused ?: false

                val subjectText = if (isStudying && friendUser.showSubject) {
                    active?.subjectName ?: ""
                } else if (isStudying) {
                    "Private Subject"
                } else {
                    "Not studying right now"
                }

                val paperText = if (isStudying && friendUser.showSubject) active?.paper ?: "" else ""
                val chapterText = if (isStudying && friendUser.showChapter) active?.chapterTopic ?: "" else ""
                val goalText = if (isStudying && friendUser.showChapter) active?.studyGoal ?: "" else ""

                val now = System.currentTimeMillis()
                val elapsed = if (active != null && friendUser.showStudyDuration) {
                    val add = if (active.isPaused) 0 else (now - active.lastResumedAtMillis) / 1000
                    active.accumulatedSeconds + add
                } else {
                    0L
                }

                cards.add(
                    FriendLiveCard(
                        user = friendUser,
                        friendshipId = f.id,
                        isStudying = isStudying,
                        isPaused = isPaused,
                        displaySubject = subjectText,
                        displayPaper = paperText,
                        displayChapter = chapterText,
                        studyGoal = goalText,
                        startedAtMillis = active?.startedAtMillis ?: 0L,
                        initialSeconds = elapsed
                    )
                )
            }
            // Sort studying friends first
            cards.sortedWith(compareByDescending<FriendLiveCard> { it.isStudying }.thenBy { it.user.fullName })
        }
    }

    // Pending friend requests received
    fun getPendingRequests(currentUserId: String): Flow<List<Pair<FriendshipEntity, UserEntity>>> {
        return combine(
            friendshipDao.getPendingRequestsReceived(currentUserId),
            flow { emit(userDao.getAllUsersDirect()) }
        ) { requests, allUsers ->
            val userMap = allUsers.associateBy { it.id }
            requests.mapNotNull { req ->
                userMap[req.requesterId]?.let { sender ->
                    Pair(req, sender)
                }
            }
        }
    }

    fun searchUsers(query: String, currentUserId: String): Flow<List<UserEntity>> =
        userDao.searchUsers(query, currentUserId)

    suspend fun sendFriendRequest(currentUserId: String, targetUserId: String): Result<String> {
        val target = userDao.getUserByIdDirect(targetUserId) ?: return Result.failure(Exception("User not found"))
        if (!target.allowFriendRequests) {
            return Result.failure(Exception("This student has disabled friend requests in privacy settings."))
        }
        val existing = friendshipDao.getFriendshipBetween(currentUserId, targetUserId)
        if (existing != null) {
            return Result.failure(Exception("Request already exists or already friends!"))
        }

        val currentUser = userDao.getUserByIdDirect(currentUserId)
        val friendship = FriendshipEntity(
            requesterId = currentUserId,
            receiverId = targetUserId,
            status = "PENDING"
        )
        friendshipDao.insertFriendship(friendship)

        // Add notification for receiver
        notificationDao.insertNotification(
            NotificationEntity(
                userId = targetUserId,
                senderName = currentUser?.fullName ?: "A student",
                title = "Study Buddy Request",
                message = "${currentUser?.fullName ?: "Someone"} from ${currentUser?.collegeName ?: "HSC"} sent you a study buddy request.",
                type = "FRIEND_REQUEST"
            )
        )
        return Result.success("Friend request sent to ${target.fullName}")
    }

    suspend fun acceptFriendRequest(friendshipId: Long, currentUserId: String) {
        friendshipDao.updateStatus(friendshipId, "ACCEPTED")
        val currentUser = userDao.getUserByIdDirect(currentUserId)
        // Find who requested it to notify them
        // Notify them
        notificationDao.insertNotification(
            NotificationEntity(
                userId = currentUserId,
                senderName = "System",
                title = "Study Buddy Added",
                message = "You are now study buddies! You can see each other's live study progress.",
                type = "FRIEND_ACCEPTED"
            )
        )
    }

    suspend fun rejectFriendRequest(friendshipId: Long) {
        friendshipDao.deleteFriendship(friendshipId)
    }

    suspend fun removeFriend(userA: String, userB: String) {
        friendshipDao.deleteFriendshipBetween(userA, userB)
    }

    suspend fun sendCheer(sender: UserEntity, recipientId: String, cheerEmoji: String) {
        notificationDao.insertNotification(
            NotificationEntity(
                userId = recipientId,
                senderName = sender.fullName,
                title = "Study Cheer! $cheerEmoji",
                message = "${sender.fullName} sent you a study cheer: Keep pushing for HSC! $cheerEmoji",
                type = "KUDOS"
            )
        )
    }

    // Weekly Study Planner / Routine
    fun getRoutines(userId: String): Flow<List<StudyRoutineEntity>> =
        routineDao.getRoutinesForUser(userId)

    suspend fun addRoutine(
        userId: String,
        dayOfWeek: Int,
        subjectName: String,
        paper: String,
        chapter: String,
        startTime: String,
        endTime: String
    ) {
        routineDao.insertRoutine(
            StudyRoutineEntity(
                userId = userId,
                dayOfWeek = dayOfWeek,
                subjectName = subjectName,
                paper = paper,
                chapterTopic = chapter,
                startTime = startTime,
                endTime = endTime
            )
        )
    }

    suspend fun updateRoutine(routine: StudyRoutineEntity) {
        routineDao.updateRoutine(routine)
    }

    suspend fun deleteRoutine(id: Long) {
        routineDao.deleteRoutine(id)
    }

    suspend fun markRoutineCompleted(id: Long, completed: Boolean) {
        val todayStr = dateFormat.format(Date())
        routineDao.markRoutineCompleted(id, completed, todayStr)
    }

    // Notifications
    fun getNotifications(userId: String): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    fun getUnreadNotificationCount(userId: String): Flow<Int> =
        notificationDao.getUnreadCount(userId)

    suspend fun markNotificationAsRead(id: Long) = notificationDao.markAsRead(id)
    suspend fun clearNotifications(userId: String) = notificationDao.clearAllForUser(userId)

    // Group Study Room
    fun getGroupStudyMessages(): Flow<List<GroupStudyMessageEntity>> =
        groupStudyDao.getRecentMessages()

    suspend fun postGroupStudyMessage(user: UserEntity, text: String, reaction: String) {
        groupStudyDao.insertMessage(
            GroupStudyMessageEntity(
                senderId = user.id,
                senderName = user.fullName,
                senderCollege = user.collegeName,
                message = text,
                reactionType = reaction
            )
        )
    }

    // Weekly Stats and Charts
    suspend fun calculateStats(userId: String): WeeklyStats {
        val sessions = sessionDao.getAllSessionsForUserDirect(userId)
        val todayStr = dateFormat.format(Date())
        val dayMillis = 24 * 60 * 60 * 1000L
        val now = System.currentTimeMillis()

        // 7 days daily breakdown
        val cal = Calendar.getInstance()
        val dayNames = listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
        val dailyMap = mutableMapOf<String, Long>()
        dayNames.forEach { dailyMap[it] = 0L }

        var weekTotalSec = 0L
        var todayTotalSec = 0L
        val subjectMap = mutableMapOf<String, Long>()

        val distinctDates = sessions.map { it.dateString }.distinct().sortedDescending()
        var streak = 0
        // Calculate consecutive day streak
        var checkCal = Calendar.getInstance()
        val todaySessionExists = distinctDates.contains(todayStr)
        if (!todaySessionExists) {
            // Check if streak was kept until yesterday
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }
        while (true) {
            val dStr = dateFormat.format(checkCal.time)
            if (distinctDates.contains(dStr)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }

        val weekAgoMillis = now - (7 * dayMillis)
        for (s in sessions) {
            if (s.dateString == todayStr) {
                todayTotalSec += s.durationSeconds
            }
            if (s.endTimeMillis >= weekAgoMillis) {
                weekTotalSec += s.durationSeconds
                val sCal = Calendar.getInstance().apply { timeInMillis = s.endTimeMillis }
                val dayOfWeek = sCal.get(Calendar.DAY_OF_WEEK)
                // Calendar.SUNDAY = 1, SATURDAY = 7
                val dayLabel = when (dayOfWeek) {
                    Calendar.SATURDAY -> "Sat"
                    Calendar.SUNDAY -> "Sun"
                    Calendar.MONDAY -> "Mon"
                    Calendar.TUESDAY -> "Tue"
                    Calendar.WEDNESDAY -> "Wed"
                    Calendar.THURSDAY -> "Thu"
                    Calendar.FRIDAY -> "Fri"
                    else -> "Sat"
                }
                dailyMap[dayLabel] = (dailyMap[dayLabel] ?: 0L) + s.durationSeconds
            }

            // Subject wise aggregation
            val cleanSub = s.subjectName.split("—").firstOrNull()?.trim() ?: s.subjectName
            subjectMap[cleanSub] = (subjectMap[cleanSub] ?: 0L) + s.durationSeconds
        }

        return WeeklyStats(
            totalSecondsThisWeek = weekTotalSec,
            totalSecondsToday = todayTotalSec,
            dailySeconds = dailyMap,
            subjectSeconds = subjectMap,
            streakDays = streak,
            totalSessionsCount = sessions.size
        )
    }

    // Friend Leaderboard
    suspend fun getLeaderboard(currentUserId: String): List<LeaderboardItem> {
        val friendships = friendshipDao.getAcceptedFriendships(currentUserId).first()
        val friendIds = friendships.map { if (it.requesterId == currentUserId) it.receiverId else it.requesterId }
        val allParticipantIds = (friendIds + currentUserId).distinct()

        val items = mutableListOf<LeaderboardItem>()
        val weekAgoMillis = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)

        for (id in allParticipantIds) {
            val user = userDao.getUserByIdDirect(id) ?: continue
            val sessions = sessionDao.getAllSessionsForUserDirect(id)
            val weekSec = sessions.filter { it.endTimeMillis >= weekAgoMillis }.sumOf { it.durationSeconds }
            
            // Calculate streak
            val dates = sessions.map { it.dateString }.distinct().sortedDescending()
            var streak = 0
            val checkCal = Calendar.getInstance()
            if (!dates.contains(dateFormat.format(checkCal.time))) {
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            }
            while (dates.contains(dateFormat.format(checkCal.time))) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            }

            items.add(
                LeaderboardItem(
                    user = user,
                    weeklySeconds = weekSec,
                    streakDays = streak,
                    rank = 0,
                    isCurrentUser = id == currentUserId
                )
            )
        }

        return items.sortedByDescending { it.weeklySeconds }.mapIndexed { index, item ->
            item.copy(rank = index + 1)
        }
    }
}
