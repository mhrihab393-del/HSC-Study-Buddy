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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.data.local.entity.StudyRoutineEntity
import com.example.data.model.HscGroup
import com.example.data.model.HscSubjectCatalog
import com.example.ui.StudyBuddyViewModel
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.LiveEmerald

@Composable
fun RoutineScreen(
    userGroup: String,
    routines: List<StudyRoutineEntity>,
    selectedDay: Int,
    onSelectDay: (Int) -> Unit,
    onAddRoutine: (day: Int, subject: String, paper: String, chapter: String, start: String, end: String) -> Unit,
    onDeleteRoutine: (Long) -> Unit,
    onToggleCompleted: (StudyRoutineEntity) -> Unit,
    onStartStudyingFromRoutine: (subject: String, paper: String, chapter: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val days = listOf("Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val filteredRoutines = remember(routines, selectedDay) {
        routines.filter { it.dayOfWeek == selectedDay }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("routine_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Day selector tabs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Weekly Study Planner",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Plan your HSC subjects for each day (Saturday – Friday)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days.indices.toList()) { dayIdx ->
                        val isSelected = selectedDay == dayIdx
                        val isToday = StudyBuddyViewModel.getCurrentBanglaDayOfWeek() == dayIdx
                        val count = routines.count { it.dayOfWeek == dayIdx }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectDay(dayIdx) }
                                .testTag("routine_day_tab_$dayIdx"),
                            shape = RoundedCornerShape(14.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = if (isToday && !isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = days[dayIdx].take(3),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isToday) "Today" else "$count slots",
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Routines list for selected day
            if (filteredRoutines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyStateView(
                        icon = Icons.Default.EventNote,
                        title = "No study routine for ${days[selectedDay]}",
                        message = "Tap the + button below to add your HSC subject study slot for this day.",
                        actionButtonText = "+ Add Study Slot",
                        onActionClick = { showAddDialog = true }
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredRoutines) { routine ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (routine.isCompletedToday) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onToggleCompleted(routine) },
                                    modifier = Modifier.testTag("toggle_routine_${routine.id}")
                                ) {
                                    Icon(
                                        imageVector = if (routine.isCompletedToday) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Complete",
                                        tint = if (routine.isCompletedToday) LiveEmerald else Color.Gray,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = routine.subjectName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Text(
                                        text = "${routine.paper} • ${routine.chapterTopic}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Schedule,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "${routine.startTime} – ${routine.endTime}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // 1-tap start studying
                                    IconButton(
                                        onClick = {
                                            onStartStudyingFromRoutine(
                                                routine.subjectName,
                                                routine.paper,
                                                routine.chapterTopic
                                            )
                                        },
                                        modifier = Modifier.testTag("start_from_routine_${routine.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Start Studying Now",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteRoutine(routine.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Routine",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to add routine
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("add_routine_fab"),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Routine Slot")
        }

        if (showAddDialog) {
            AddRoutineDialog(
                userGroup = userGroup,
                day = selectedDay,
                onDismiss = { showAddDialog = false },
                onAdd = { sub, paper, ch, start, end ->
                    onAddRoutine(selectedDay, sub, paper, ch, start, end)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun AddRoutineDialog(
    userGroup: String,
    day: Int,
    onDismiss: () -> Unit,
    onAdd: (subject: String, paper: String, chapter: String, startTime: String, endTime: String) -> Unit
) {
    val hscGroup = try {
        HscGroup.valueOf(userGroup.uppercase())
    } catch (e: Exception) {
        HscGroup.SCIENCE
    }
    val subjects = remember { HscSubjectCatalog.getSubjectsForGroup(hscGroup) }

    var selectedSubject by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "Physics — 1st Paper") }
    var selectedPaper by remember { mutableStateOf("1st Paper") }
    var chapter by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("19:00") }
    var endTime by remember { mutableStateOf("20:30") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("add_routine_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Study Slot (${StudyBuddyViewModel.getDayName(day)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Subject", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { s ->
                        FilterChip(
                            selected = selectedSubject == s.name,
                            onClick = {
                                selectedSubject = s.name
                                selectedPaper = s.papers.firstOrNull() ?: "1st Paper"
                            },
                            label = { Text(s.name.split("—").first().trim(), fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text("Paper", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("1st Paper", "2nd Paper").forEach { p ->
                        FilterChip(
                            selected = selectedPaper == p,
                            onClick = { selectedPaper = p },
                            label = { Text(p, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = chapter,
                    onValueChange = { chapter = it },
                    label = { Text("Chapter / Topic") },
                    placeholder = { Text("e.g. Chapter 3: Dynamics") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        onAdd(
                            selectedSubject,
                            selectedPaper,
                            chapter.ifBlank { "Core Topics" },
                            startTime,
                            endTime
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("confirm_add_routine_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Study Slot", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
