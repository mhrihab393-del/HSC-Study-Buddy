package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ActiveSessionDao
import com.example.data.local.dao.FriendshipDao
import com.example.data.local.dao.GroupStudyDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.StudyRoutineDao
import com.example.data.local.dao.StudySessionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.ActiveSessionEntity
import com.example.data.local.entity.FriendshipEntity
import com.example.data.local.entity.GroupStudyMessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.StudyRoutineEntity
import com.example.data.local.entity.StudySessionEntity
import com.example.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        UserEntity::class,
        StudySessionEntity::class,
        ActiveSessionEntity::class,
        FriendshipEntity::class,
        StudyRoutineEntity::class,
        NotificationEntity::class,
        GroupStudyMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun activeSessionDao(): ActiveSessionDao
    abstract fun friendshipDao(): FriendshipDao
    abstract fun studyRoutineDao(): StudyRoutineDao
    abstract fun notificationDao(): NotificationDao
    abstract fun groupStudyDao(): GroupStudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hsc_study_buddy.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                getInstance(context).populateInitialData()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun populateInitialData() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = dateFormat.format(Date())
        val now = System.currentTimeMillis()

        // 1. Primary Default User: Rimonium
        val mainUser = UserEntity(
            id = "user_rimonium",
            username = "rimonium",
            fullName = "Rimon Hasan",
            email = "rimonium@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Notre Dame College, Dhaka",
            hscGroup = "SCIENCE",
            sessionYear = "2026",
            avatarUrl = "avatar_boy_1",
            selectedMainSubjects = "Physics — 1st Paper,Chemistry — 1st Paper,Higher Mathematics — 1st Paper",
            selectedOptionalSubject = "Biology — 1st Paper (Botany)",
            showStudyStatus = true,
            showSubject = true,
            showChapter = true,
            showStudyDuration = true,
            allowFriendRequests = true
        )
        userDao().insertUser(mainUser)

        // 2. Realistic HSC Friends from Dhaka College, Viqarunnisa, Rajuk Uttara, etc.
        val rafi = UserEntity(
            id = "user_rafi",
            username = "rafi_ndc",
            fullName = "Rafi Ahmed",
            email = "rafi@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Notre Dame College, Dhaka",
            hscGroup = "SCIENCE",
            sessionYear = "2026",
            avatarUrl = "avatar_boy_2",
            selectedMainSubjects = "Physics — 1st Paper,Chemistry — 1st Paper,Higher Mathematics — 1st Paper",
            selectedOptionalSubject = "Biology — 1st Paper (Botany)"
        )
        val hasan = UserEntity(
            id = "user_hasan",
            username = "hasan_dhaka",
            fullName = "Hasan Mahmud",
            email = "hasan@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Dhaka College, Dhaka",
            hscGroup = "SCIENCE",
            sessionYear = "2026",
            avatarUrl = "avatar_boy_3",
            selectedMainSubjects = "Physics — 1st Paper,Chemistry — 1st Paper,Biology — 1st Paper (Botany)",
            selectedOptionalSubject = "Higher Mathematics — 1st Paper"
        )
        val nusrat = UserEntity(
            id = "user_nusrat",
            username = "nusrat_vnc",
            fullName = "Nusrat Jahan",
            email = "nusrat@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Viqarunnisa Noon College",
            hscGroup = "BUSINESS_STUDIES",
            sessionYear = "2026",
            avatarUrl = "avatar_girl_1",
            selectedMainSubjects = "Accounting — 1st Paper,Business Organization and Management — 1st Paper,Finance, Banking and Insurance — 1st Paper",
            selectedOptionalSubject = "Economics — 1st Paper"
        )
        val sadia = UserEntity(
            id = "user_sadia",
            username = "sadia_hcc",
            fullName = "Sadia Islam",
            email = "sadia@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Holy Cross College, Dhaka",
            hscGroup = "HUMANITIES",
            sessionYear = "2026",
            avatarUrl = "avatar_girl_2",
            selectedMainSubjects = "Economics — 1st Paper,Civics and Good Governance — 1st Paper,Logic — 1st Paper",
            selectedOptionalSubject = "Sociology — 1st Paper"
        )
        val tanvir = UserEntity(
            id = "user_tanvir",
            username = "tanvir_rumc",
            fullName = "Tanvir Hossain",
            email = "tanvir@hsc.edu.bd",
            passwordHash = "password123",
            collegeName = "Rajuk Uttara Model College",
            hscGroup = "SCIENCE",
            sessionYear = "2026",
            avatarUrl = "avatar_boy_1"
        )

        userDao().insertUser(rafi)
        userDao().insertUser(hasan)
        userDao().insertUser(nusrat)
        userDao().insertUser(sadia)
        userDao().insertUser(tanvir)

        // 3. Friendships with Main User
        friendshipDao().insertFriendship(FriendshipEntity(requesterId = "user_rimonium", receiverId = "user_rafi", status = "ACCEPTED"))
        friendshipDao().insertFriendship(FriendshipEntity(requesterId = "user_hasan", receiverId = "user_rimonium", status = "ACCEPTED"))
        friendshipDao().insertFriendship(FriendshipEntity(requesterId = "user_nusrat", receiverId = "user_rimonium", status = "ACCEPTED"))
        // Sadia sent a pending request
        friendshipDao().insertFriendship(FriendshipEntity(requesterId = "user_sadia", receiverId = "user_rimonium", status = "PENDING"))

        // 4. Active Sessions for Friends (Live Social Feed)
        // Rafi started 42 minutes ago studying Physics
        val rafiStartTime = now - (42 * 60 * 1000)
        activeSessionDao().setActiveSession(
            ActiveSessionEntity(
                userId = "user_rafi",
                subjectName = "Physics — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Chapter 3: Dynamics (Newton's Laws)",
                studyGoal = "Solve CQ from Dhaka Board 2023",
                startedAtMillis = rafiStartTime,
                isPaused = false,
                accumulatedSeconds = 42 * 60,
                lastResumedAtMillis = rafiStartTime
            )
        )

        // Hasan started 72 minutes ago studying Chemistry
        val hasanStartTime = now - (72 * 60 * 1000)
        activeSessionDao().setActiveSession(
            ActiveSessionEntity(
                userId = "user_hasan",
                subjectName = "Chemistry — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Chapter 2: Qualitative Chemistry",
                studyGoal = "Solubility Product & Salt Analysis",
                startedAtMillis = hasanStartTime,
                isPaused = false,
                accumulatedSeconds = 72 * 60,
                lastResumedAtMillis = hasanStartTime
            )
        )

        // Nusrat started 28 minutes ago studying Accounting
        val nusratStartTime = now - (28 * 60 * 1000)
        activeSessionDao().setActiveSession(
            ActiveSessionEntity(
                userId = "user_nusrat",
                subjectName = "Accounting — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Bank Reconciliation Statement",
                studyGoal = "Complete Practice Set 4",
                startedAtMillis = nusratStartTime,
                isPaused = false,
                accumulatedSeconds = 28 * 60,
                lastResumedAtMillis = nusratStartTime
            )
        )

        // 5. Seed Past Study Sessions for Rimonium (so charts, history, and streak work out of the box)
        val dayMillis = 24 * 60 * 60 * 1000L
        for (i in 0..6) {
            val sessionDate = dateFormat.format(Date(now - (i * dayMillis)))
            val duration1 = (45 + (i * 12) % 40) * 60L
            studySessionDao().insertSession(
                StudySessionEntity(
                    userId = "user_rimonium",
                    subjectName = if (i % 2 == 0) "Physics — 1st Paper" else "Higher Mathematics — 1st Paper",
                    paper = "1st Paper",
                    chapterTopic = if (i % 2 == 0) "Chapter 2: Vectors" else "Chapter 9: Differentiation",
                    studyGoal = "Memorize formulas and solve board MCQ",
                    startTimeMillis = (now - (i * dayMillis) - (duration1 * 1000)),
                    endTimeMillis = (now - (i * dayMillis)),
                    durationSeconds = duration1,
                    dateString = sessionDate,
                    isCompleted = true
                )
            )

            val duration2 = (35 + (i * 15) % 35) * 60L
            studySessionDao().insertSession(
                StudySessionEntity(
                    userId = "user_rimonium",
                    subjectName = if (i % 3 == 0) "Information and Communication Technology (ICT)" else "Chemistry — 1st Paper",
                    paper = if (i % 3 == 0) "Compulsory Single Paper" else "1st Paper",
                    chapterTopic = if (i % 3 == 0) "Chapter 3: Number Systems" else "Chapter 3: Periodic Properties",
                    studyGoal = "Revise short syllabus notes",
                    startTimeMillis = (now - (i * dayMillis) - (duration2 * 1000) - 7200000L),
                    endTimeMillis = (now - (i * dayMillis) - 7200000L),
                    durationSeconds = duration2,
                    dateString = sessionDate,
                    isCompleted = true
                )
            )
        }

        // Seed some sessions for Rafi & Hasan for leaderboard
        for (i in 0..5) {
            val sDate = dateFormat.format(Date(now - (i * dayMillis)))
            studySessionDao().insertSession(
                StudySessionEntity(
                    userId = "user_rafi",
                    subjectName = "Physics — 1st Paper",
                    paper = "1st Paper",
                    chapterTopic = "Chapter 4: Work, Energy & Power",
                    startTimeMillis = now - (i * dayMillis) - 5400000L,
                    endTimeMillis = now - (i * dayMillis),
                    durationSeconds = 5400L,
                    dateString = sDate
                )
            )
            studySessionDao().insertSession(
                StudySessionEntity(
                    userId = "user_hasan",
                    subjectName = "Chemistry — 1st Paper",
                    paper = "1st Paper",
                    chapterTopic = "Chapter 2: Qualitative Chemistry",
                    startTimeMillis = now - (i * dayMillis) - 4800000L,
                    endTimeMillis = now - (i * dayMillis),
                    durationSeconds = 4800L,
                    dateString = sDate
                )
            )
        }

        // 6. Seed Study Routines (Weekly Study Planner)
        studyRoutineDao().insertRoutine(
            StudyRoutineEntity(
                userId = "user_rimonium",
                dayOfWeek = 0, // Saturday
                subjectName = "Physics — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Chapter 3: Dynamics",
                startTime = "19:00",
                endTime = "20:30",
                isCompletedToday = false
            )
        )
        studyRoutineDao().insertRoutine(
            StudyRoutineEntity(
                userId = "user_rimonium",
                dayOfWeek = 0, // Saturday
                subjectName = "Higher Mathematics — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Chapter 9: Differentiation",
                startTime = "21:00",
                endTime = "22:30",
                isCompletedToday = false
            )
        )
        studyRoutineDao().insertRoutine(
            StudyRoutineEntity(
                userId = "user_rimonium",
                dayOfWeek = 1, // Sunday
                subjectName = "Chemistry — 1st Paper",
                paper = "1st Paper",
                chapterTopic = "Chapter 2: Qualitative Chemistry",
                startTime = "19:30",
                endTime = "21:00",
                isCompletedToday = false
            )
        )
        studyRoutineDao().insertRoutine(
            StudyRoutineEntity(
                userId = "user_rimonium",
                dayOfWeek = 2, // Monday
                subjectName = "Information and Communication Technology (ICT)",
                paper = "Compulsory Single Paper",
                chapterTopic = "Chapter 5: C Programming",
                startTime = "20:00",
                endTime = "21:30",
                isCompletedToday = false
            )
        )

        // 7. Seed Notifications
        notificationDao().insertNotification(
            NotificationEntity(
                userId = "user_rimonium",
                senderName = "Sadia Islam",
                title = "New Friend Request",
                message = "Sadia Islam from Holy Cross College sent you a study buddy request.",
                type = "FRIEND_REQUEST"
            )
        )
        notificationDao().insertNotification(
            NotificationEntity(
                userId = "user_rimonium",
                senderName = "Rafi Ahmed",
                title = "Study Cheer 🔥",
                message = "Rafi cheered your 7-day study streak! Keep going!",
                type = "KUDOS"
            )
        )

        // 8. Seed Study Room Cheers
        groupStudyDao().insertMessage(
            GroupStudyMessageEntity(
                senderId = "user_rafi",
                senderName = "Rafi Ahmed",
                senderCollege = "Notre Dame College",
                message = "Cracking Dynamics Board questions! Let's do this!",
                reactionType = "FIRE",
                timestamp = now - (20 * 60 * 1000)
            )
        )
        groupStudyDao().insertMessage(
            GroupStudyMessageEntity(
                senderId = "user_hasan",
                senderName = "Hasan Mahmud",
                senderCollege = "Dhaka College",
                message = "Solubility product numericals are tricky today ☕",
                reactionType = "COFFEE",
                timestamp = now - (10 * 60 * 1000)
            )
        )
    }
}
