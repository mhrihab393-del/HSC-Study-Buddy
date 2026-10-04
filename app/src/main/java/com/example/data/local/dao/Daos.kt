package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ActiveSessionEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupStudyMessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudyRoutineEntity
import com.example.data.local.entity.StudySessionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserByIdDirect(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:identifier) OR LOWER(email) = LOWER(:identifier) LIMIT 1")
    suspend fun getUserByUsernameOrEmail(identifier: String): UserEntity?

    @Query("SELECT * FROM users WHERE id != :currentUserId AND (LOWER(username) LIKE '%' || LOWER(:query) || '%' OR LOWER(fullName) LIKE '%' || LOWER(:query) || '%' OR LOWER(collegeName) LIKE '%' || LOWER(:query) || '%')")
    fun searchUsers(query: String, currentUserId: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id != :currentUserId")
    fun getAllOtherUsers(currentUserId: String): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    suspend fun getAllUsersDirect(): List<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET showStudyStatus = :showStatus, showSubject = :showSubject, showChapter = :showChapter, showStudyDuration = :showDuration, allowFriendRequests = :allowRequests WHERE id = :userId")
    suspend fun updatePrivacy(
        userId: String,
        showStatus: Boolean,
        showSubject: Boolean,
        showChapter: Boolean,
        showDuration: Boolean,
        allowRequests: Boolean
    )
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY endTimeMillis DESC")
    fun getSessionsForUser(userId: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE userId = :userId AND dateString = :dateString ORDER BY endTimeMillis DESC")
    fun getSessionsForDate(userId: String, dateString: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE userId = :userId")
    suspend fun getAllSessionsForUserDirect(userId: String): List<StudySessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity): Long

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSession(id: Long)
}

@Dao
interface ActiveSessionDao {
    @Query("SELECT * FROM active_sessions WHERE userId = :userId")
    fun getActiveSession(userId: String): Flow<ActiveSessionEntity?>

    @Query("SELECT * FROM active_sessions WHERE userId = :userId")
    suspend fun getActiveSessionDirect(userId: String): ActiveSessionEntity?

    @Query("SELECT * FROM active_sessions")
    fun getAllActiveSessions(): Flow<List<ActiveSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setActiveSession(session: ActiveSessionEntity)

    @Query("DELETE FROM active_sessions WHERE userId = :userId")
    suspend fun clearActiveSession(userId: String)
}

@Dao
interface FriendshipDao {
    @Query("SELECT * FROM friendships WHERE (requesterId = :userId OR receiverId = :userId) AND status = 'ACCEPTED'")
    fun getAcceptedFriendships(userId: String): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE receiverId = :userId AND status = 'PENDING'")
    fun getPendingRequestsReceived(userId: String): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE requesterId = :userId AND status = 'PENDING'")
    fun getPendingRequestsSent(userId: String): Flow<List<FriendshipEntity>>

    @Query("SELECT * FROM friendships WHERE (requesterId = :userA AND receiverId = :userB) OR (requesterId = :userB AND receiverId = :userA) LIMIT 1")
    suspend fun getFriendshipBetween(userA: String, userB: String): FriendshipEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendship(friendship: FriendshipEntity): Long

    @Query("UPDATE friendships SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM friendships WHERE id = :id")
    suspend fun deleteFriendship(id: Long)

    @Query("DELETE FROM friendships WHERE (requesterId = :userA AND receiverId = :userB) OR (requesterId = :userB AND receiverId = :userA)")
    suspend fun deleteFriendshipBetween(userA: String, userB: String)
}

@Dao
interface StudyRoutineDao {
    @Query("SELECT * FROM study_routines WHERE userId = :userId ORDER BY dayOfWeek ASC, startTime ASC")
    fun getRoutinesForUser(userId: String): Flow<List<StudyRoutineEntity>>

    @Query("SELECT * FROM study_routines WHERE userId = :userId AND dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getRoutinesForDay(userId: String, dayOfWeek: Int): Flow<List<StudyRoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: StudyRoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: StudyRoutineEntity)

    @Query("DELETE FROM study_routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    @Query("UPDATE study_routines SET isCompletedToday = :completed, lastCompletedDate = :dateStr WHERE id = :id")
    suspend fun markRoutineCompleted(id: Long, completed: Boolean, dateStr: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearAllForUser(userId: String)
}

@Dao
interface GroupStudyDao {
    @Query("SELECT * FROM group_study_messages ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMessages(limit: Int = 30): Flow<List<GroupStudyMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: GroupStudyMessageEntity)
}
