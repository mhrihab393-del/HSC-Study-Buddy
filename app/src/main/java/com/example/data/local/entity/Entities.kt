package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true), Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val collegeName: String,
    val hscGroup: String, // SCIENCE, BUSINESS_STUDIES, HUMANITIES
    val sessionYear: String,
    val avatarUrl: String = "",
    val selectedMainSubjects: String = "",
    val selectedOptionalSubject: String = "",
    val showStudyStatus: Boolean = true,
    val showSubject: Boolean = true,
    val showChapter: Boolean = true,
    val showStudyDuration: Boolean = true,
    val allowFriendRequests: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_sessions",
    indices = [Index(value = ["userId"]), Index(value = ["dateString"])]
)
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val subjectName: String,
    val paper: String,
    val chapterTopic: String,
    val studyGoal: String = "",
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationSeconds: Long,
    val dateString: String, // Format: YYYY-MM-DD
    val isCompleted: Boolean = true
)

@Entity(tableName = "active_sessions")
data class ActiveSessionEntity(
    @PrimaryKey val userId: String,
    val subjectName: String,
    val paper: String,
    val chapterTopic: String,
    val studyGoal: String = "",
    val startedAtMillis: Long,
    val isPaused: Boolean = false,
    val accumulatedSeconds: Long = 0,
    val lastResumedAtMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "friendships",
    indices = [Index(value = ["requesterId", "receiverId"], unique = true)]
)
data class FriendshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val requesterId: String,
    val receiverId: String,
    val status: String, // PENDING, ACCEPTED, REJECTED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "study_routines",
    indices = [Index(value = ["userId"])]
)
data class StudyRoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val dayOfWeek: Int, // 0 = Saturday, 1 = Sunday, ..., 6 = Friday (Bangladeshi study week)
    val subjectName: String,
    val paper: String,
    val chapterTopic: String = "",
    val startTime: String, // HH:MM
    val endTime: String,   // HH:MM
    val isCompletedToday: Boolean = false,
    val lastCompletedDate: String = ""
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["userId"])]
)
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val senderName: String,
    val title: String,
    val message: String,
    val type: String, // FRIEND_REQUEST, FRIEND_ACCEPTED, KUDOS, ROUTINE_REMINDER
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "group_study_messages")
data class GroupStudyMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderId: String,
    val senderName: String,
    val senderCollege: String,
    val message: String,
    val reactionType: String = "", // FIRE, BOOK, COFFEE, HUNDRED
    val timestamp: Long = System.currentTimeMillis()
)
