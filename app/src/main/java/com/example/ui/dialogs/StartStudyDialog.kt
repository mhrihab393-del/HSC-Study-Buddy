package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.HscGroup
import com.example.data.model.HscSubjectCatalog

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun StartStudyDialog(
    userGroup: String,
    onDismiss: () -> Unit,
    onStartSession: (subject: String, paper: String, chapter: String, goal: String) -> Unit
) {
    val hscGroup = try {
        HscGroup.valueOf(userGroup.uppercase())
    } catch (e: Exception) {
        HscGroup.SCIENCE
    }

    val availableSubjects = remember(hscGroup) {
        HscSubjectCatalog.getSubjectsForGroup(hscGroup)
    }

    var selectedSubject by remember { mutableStateOf(availableSubjects.firstOrNull()?.name ?: "Physics — 1st Paper") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }

    val currentSubjectInfo = remember(selectedSubject) {
        availableSubjects.find { it.name == selectedSubject } ?: availableSubjects.firstOrNull()
    }

    var selectedPaper by remember(selectedSubject) {
        mutableStateOf(currentSubjectInfo?.papers?.firstOrNull() ?: "1st Paper")
    }

    val chapters = remember(selectedSubject) {
        HscSubjectCatalog.findChapters(selectedSubject)
    }

    var selectedChapter by remember(selectedSubject) {
        mutableStateOf(chapters.firstOrNull() ?: "Chapter 1: Basics")
    }
    var customChapterText by remember { mutableStateOf("") }
    var useManualChapter by remember { mutableStateOf(false) }

    var studyGoal by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("start_study_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Study Session",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Pick your subject and topic. Your friends will see you live studying!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subject Selector
                Text(
                    text = "1. Select Subject",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = subjectMenuExpanded,
                    onExpandedChange = { subjectMenuExpanded = !subjectMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSubject,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("subject_selector_field"),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        availableSubjects.forEach { sub ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = sub.name, fontWeight = FontWeight.Medium)
                                        Text(
                                            text = if (sub.group == "COMPULSORY") "Compulsory" else "Group Subject",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    selectedSubject = sub.name
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Paper Selector
                if (currentSubjectInfo?.papers?.isNotEmpty() == true) {
                    Text(
                        text = "2. Paper",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentSubjectInfo.papers.forEach { paper ->
                            FilterChip(
                                selected = selectedPaper == paper,
                                onClick = { selectedPaper = paper },
                                label = { Text(paper) },
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Chapter Selector
                Text(
                    text = "3. Chapter / Topic",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Select from syllabus or type your own:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    chapters.take(6).forEach { ch ->
                        val shortTitle = if (ch.length > 25) ch.take(22) + "..." else ch
                        FilterChip(
                            selected = !useManualChapter && selectedChapter == ch,
                            onClick = {
                                selectedChapter = ch
                                useManualChapter = false
                            },
                            label = { Text(shortTitle, fontSize = 12.sp) },
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customChapterText,
                    onValueChange = {
                        customChapterText = it
                        if (it.isNotBlank()) useManualChapter = true
                    },
                    label = { Text("Or custom topic/chapter name") },
                    placeholder = { Text("e.g. Chapter 3: Dynamics (CQ Solve)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("custom_chapter_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Optional Study Goal
                Text(
                    text = "4. Study Goal (Optional)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = studyGoal,
                    onValueChange = { studyGoal = it },
                    placeholder = { Text("e.g. Solve 10 MCQs + Complete formula sheet") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_goal_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val finalChapter = if (useManualChapter && customChapterText.isNotBlank()) {
                                customChapterText.trim()
                            } else {
                                selectedChapter
                            }
                            onStartSession(
                                selectedSubject,
                                selectedPaper,
                                finalChapter,
                                studyGoal.trim()
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("confirm_start_study_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Start Studying", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
