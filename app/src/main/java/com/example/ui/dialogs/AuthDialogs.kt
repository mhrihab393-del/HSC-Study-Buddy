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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.UserEntity
import com.example.data.model.HscGroup
import com.example.data.model.HscSubjectCatalog
import com.example.ui.components.StudentAvatar

@Composable
fun AccountSwitcherDialog(
    users: List<UserEntity>,
    currentUserId: String,
    onSelectUser: (String) -> Unit,
    onOpenAuth: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("account_switcher_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Switch Student Account",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Switch between HSC student profiles to test live social peer activity in real time:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier.height(260.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(users) { user ->
                        val isCurrent = user.id == currentUserId
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectUser(user.id)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StudentAvatar(name = user.fullName, size = 40.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.fullName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "@${user.username} • ${user.collegeName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${user.hscGroup} • HSC ${user.sessionYear}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onDismiss()
                        onOpenAuth()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_or_register_account_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Register New HSC Student / Login")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onLogin: (username: String, pass: String) -> Unit,
    onRegister: (
        username: String,
        fullName: String,
        email: String,
        password: String,
        college: String,
        group: String,
        sessionYear: String,
        selectedMain: List<String>,
        selectedOptional: String
    ) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Login, 1 = Sign Up, 2 = Forgot Password

    // Login fields
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }

    // Sign Up fields
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var collegeName by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf(HscGroup.SCIENCE) }
    var sessionYear by remember { mutableStateOf("2026") }

    // Subjects selection
    val groupSubjects = remember(selectedGroup) {
        when (selectedGroup) {
            HscGroup.SCIENCE -> HscSubjectCatalog.scienceSubjects
            HscGroup.BUSINESS_STUDIES -> HscSubjectCatalog.businessSubjects
            HscGroup.HUMANITIES -> HscSubjectCatalog.humanitiesSubjects
        }
    }
    var selectedMainSubjects by remember(selectedGroup) {
        mutableStateOf(groupSubjects.take(3).map { it.name }.toSet())
    }
    var selectedOptionalSubject by remember(selectedGroup) {
        mutableStateOf(groupSubjects.getOrNull(3)?.name ?: "")
    }

    // Forgot password field
    var forgotEmail by remember { mutableStateOf("") }
    var resetSuccessMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("auth_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HSC Student Portal",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Log In") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Sign Up") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Forgot") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedTab) {
                    0 -> { // LOGIN
                        Text(
                            text = "Log in to your HSC Study Buddy account",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text("Username or Email") },
                            placeholder = { Text("e.g. rimonium or rimonium@hsc.edu.bd") },
                            modifier = Modifier.fillMaxWidth().testTag("login_username_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { onLogin(loginIdentifier, loginPassword) },
                            modifier = Modifier.fillMaxWidth().testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = loginIdentifier.isNotBlank() && loginPassword.isNotBlank()
                        ) {
                            Text("Log In", fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = { selectedTab = 2 },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text("Forgot password?")
                        }
                    }

                    1 -> { // SIGN UP
                        Text(
                            text = "Create your Bangladeshi HSC Student Profile",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Rimon Hasan") },
                            modifier = Modifier.fillMaxWidth().testTag("signup_fullname_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username") },
                            placeholder = { Text("e.g. rimon_ndc") },
                            modifier = Modifier.fillMaxWidth().testTag("signup_username_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            placeholder = { Text("e.g. student@gmail.com") },
                            modifier = Modifier.fillMaxWidth().testTag("signup_email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth().testTag("signup_password_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = collegeName,
                            onValueChange = { collegeName = it },
                            label = { Text("College Name") },
                            placeholder = { Text("e.g. Notre Dame College, Dhaka") },
                            modifier = Modifier.fillMaxWidth().testTag("signup_college_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // HSC Group
                        Text(
                            text = "HSC Group",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            HscGroup.values().forEach { g ->
                                FilterChip(
                                    selected = selectedGroup == g,
                                    onClick = { selectedGroup = g },
                                    label = { Text(g.displayName, fontSize = 12.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        // Session
                        OutlinedTextField(
                            value = sessionYear,
                            onValueChange = { sessionYear = it },
                            label = { Text("HSC Exam Year / Session") },
                            placeholder = { Text("e.g. 2026") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Compulsory Subjects (Automatic)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Bangla 1st & 2nd, English 1st & 2nd, ICT",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Select 3 Main Group Subjects:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            groupSubjects.forEach { s ->
                                val isSelected = selectedMainSubjects.contains(s.name)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        if (isSelected) {
                                            selectedMainSubjects = selectedMainSubjects - s.name
                                        } else if (selectedMainSubjects.size < 3) {
                                            selectedMainSubjects = selectedMainSubjects + s.name
                                        }
                                    },
                                    label = { Text(s.name.split("—").first().trim(), fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Select 4th / Optional Subject:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            groupSubjects.filter { !selectedMainSubjects.contains(it.name) }.forEach { s ->
                                FilterChip(
                                    selected = selectedOptionalSubject == s.name,
                                    onClick = { selectedOptionalSubject = s.name },
                                    label = { Text(s.name.split("—").first().trim(), fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                onRegister(
                                    username,
                                    fullName,
                                    email,
                                    password,
                                    collegeName,
                                    selectedGroup.name,
                                    sessionYear,
                                    selectedMainSubjects.toList(),
                                    selectedOptionalSubject
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("signup_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = username.isNotBlank() && fullName.isNotBlank() && password.isNotBlank() && collegeName.isNotBlank()
                        ) {
                            Text("Create HSC Student Account", fontWeight = FontWeight.Bold)
                        }
                    }

                    2 -> { // FORGOT PASSWORD
                        Text(
                            text = "Reset Student Password",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            label = { Text("Your Registered Email or Username") },
                            placeholder = { Text("e.g. rimonium@hsc.edu.bd") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (resetSuccessMsg != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = resetSuccessMsg ?: "",
                                    modifier = Modifier.padding(12.dp),
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Button(
                            onClick = {
                                resetSuccessMsg = "Password reset instructions sent to $forgotEmail! Default password is 'password123'."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = forgotEmail.isNotBlank()
                        ) {
                            Text("Send Reset Link")
                        }
                    }
                }
            }
        }
    }
}
