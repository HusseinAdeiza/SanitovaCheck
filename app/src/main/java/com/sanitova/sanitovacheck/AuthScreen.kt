package com.sanitova.sanitovacheck

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.sanitova.sanitovacheck.ui.theme.*
import kotlinx.coroutines.launch

private enum class AuthMode { LOGIN, SIGNUP, RESET }

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by AuthRepository.currentUser.collectAsState()
    val authError by AuthRepository.authError.collectAsState()

    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var resetSent by remember { mutableStateOf(false) }

    // Auto-dismiss on success
    LaunchedEffect(currentUser) {
        if (currentUser != null) onAuthSuccess()
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                isLoading = true
                scope.launch {
                    AuthRepository.signInWithGoogle(idToken)
                    isLoading = false
                }
            }
        } catch (e: ApiException) {
            isLoading = false
        }
    }

    fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(context, gso)
        googleSignInLauncher.launch(client.signInIntent)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(8.dp))

        Text(
            when (mode) {
                AuthMode.LOGIN -> "Welcome back"
                AuthMode.SIGNUP -> "Create account"
                AuthMode.RESET -> "Reset password"
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Text(
            when (mode) {
                AuthMode.LOGIN -> "Sign in to sync your scan history across devices"
                AuthMode.SIGNUP -> "Join SanitovaCheck to protect your data"
                AuthMode.RESET -> "Enter your email and we'll send you a reset link"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        // Email field (all modes)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; AuthRepository.clearError() },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        // Display name (signup only)
        if (mode == AuthMode.SIGNUP) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it; AuthRepository.clearError() },
                label = { Text("Full name") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
        }

        // Password field (login + signup)
        if (mode != AuthMode.RESET) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; AuthRepository.clearError() },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = if (mode == AuthMode.SIGNUP) ImeAction.Next else ImeAction.Done
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
        }

        // Confirm password (signup only)
        if (mode == AuthMode.SIGNUP) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; AuthRepository.clearError() },
                label = { Text("Confirm password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
        }

        // Error display
        authError?.let { error ->
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        // Reset sent confirmation
        if (resetSent) {
            Text(
                "Check your email for the reset link.",
                color = RiskLow,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
        }

        // Primary action button
        Button(
            onClick = {
                isLoading = true
                AuthRepository.clearError()
                scope.launch {
                    when (mode) {
                        AuthMode.LOGIN -> {
                            AuthRepository.signIn(email, password)
                        }
                        AuthMode.SIGNUP -> {
                            if (password != confirmPassword) {
                                AuthRepository.signUp("", "", "") // trigger error via catch
                            } else {
                                AuthRepository.signUp(email, password, displayName)
                            }
                        }
                        AuthMode.RESET -> {
                            AuthRepository.sendPasswordReset(email)
                            resetSent = true
                        }
                    }
                    isLoading = false
                }
            },
            enabled = !isLoading && email.isNotBlank() &&
                    (mode == AuthMode.RESET || password.isNotBlank()) &&
                    (mode != AuthMode.SIGNUP || (displayName.isNotBlank() && confirmPassword.isNotBlank())),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    when (mode) {
                        AuthMode.LOGIN -> "Sign In"
                        AuthMode.SIGNUP -> "Create Account"
                        AuthMode.RESET -> "Send Reset Link"
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Google Sign-In (login + signup only)
        if (mode != AuthMode.RESET) {
            OutlinedButton(
                onClick = { launchGoogleSignIn() },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Sign in with Google")
            }
            Spacer(Modifier.height(16.dp))
        }

        // Toggle mode links
        when (mode) {
            AuthMode.LOGIN -> {
                Text(
                    "Forgot password?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { mode = AuthMode.RESET; AuthRepository.clearError() }
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Sign up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { mode = AuthMode.SIGNUP; AuthRepository.clearError() }
                    )
                }
            }
            AuthMode.SIGNUP -> {
                Row {
                    Text("Already have an account? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Sign in",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { mode = AuthMode.LOGIN; AuthRepository.clearError() }
                    )
                }
            }
            AuthMode.RESET -> {
                Row {
                    Text("Remember your password? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Sign in",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { mode = AuthMode.LOGIN; AuthRepository.clearError(); resetSent = false }
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Text(
            "Your data is secured with Firebase Authentication.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
