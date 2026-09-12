package com.sanitova.sanitovacheck

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
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
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
    var localError by remember { mutableStateOf<String?>(null) }

    // Auto-dismiss on success
    LaunchedEffect(currentUser) {
        if (currentUser != null) onAuthSuccess()
    }

    // Clear local error when auth error clears
    LaunchedEffect(authError) {
        if (authError == null) localError = null
    }

    // Credential Manager instance (modern Google Sign-In)
    val credentialManager = remember { CredentialManager.create(context) }

    fun launchGoogleSignIn() {
        val webClientId = context.getString(R.string.default_web_client_id)
        if (webClientId.isBlank() || webClientId == "YOUR_WEB_CLIENT_ID") {
            localError = "Google Sign-In is not configured. Please use email/password instead."
            return
        }

        val googleIdOption = com.google.android.libraries.identity.googleid.GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        isLoading = true
        localError = null
        AuthRepository.clearError()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                val credential = result.credential
                val idToken = when (credential) {
                    is androidx.credentials.PasswordCredential -> {
                        isLoading = false
                        localError = "Password credentials are not supported. Please use your Google account."
                        return@launch
                    }
                    is androidx.credentials.CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                googleIdTokenCredential.idToken
                            } catch (e: GoogleIdTokenParsingException) {
                                isLoading = false
                                localError = "Failed to parse Google credential. Please try again."
                                return@launch
                            }
                        } else {
                            isLoading = false
                            localError = "Unsupported credential type. Please try again."
                            return@launch
                        }
                    }
                    else -> {
                        isLoading = false
                        localError = "Unexpected credential type. Please try again."
                        return@launch
                    }
                }

                // Sign in to Firebase with the Google ID token
                AuthRepository.signInWithGoogle(idToken)
                isLoading = false
            } catch (e: GetCredentialException) {
                isLoading = false
                localError = when {
                    e is androidx.credentials.exceptions.NoCredentialException ->
                        "No Google accounts found. Please add a Google account to your device or use email/password."
                    e is androidx.credentials.exceptions.GetCredentialCancellationException ->
                        "Google sign-in was cancelled."
                    e is androidx.credentials.exceptions.GetCredentialProviderConfigurationException ->
                        "Google Play Services is not available. Please use email/password."
                    else -> "Google sign-in failed. Please try email/password instead."
                }
            } catch (e: Exception) {
                isLoading = false
                localError = "Google sign-in error: ${e.localizedMessage ?: "Unknown error"}"
            }
        }
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
            onValueChange = { email = it; AuthRepository.clearError(); localError = null },
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
                onValueChange = { displayName = it; AuthRepository.clearError(); localError = null },
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
                onValueChange = { password = it; AuthRepository.clearError(); localError = null },
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
                onValueChange = { confirmPassword = it; AuthRepository.clearError(); localError = null },
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

        // Error display (Firebase + local)
        val errorToShow = localError ?: authError
        if (errorToShow != null) {
            Text(
                errorToShow,
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
                localError = null
                scope.launch {
                    when (mode) {
                        AuthMode.LOGIN -> {
                            AuthRepository.signIn(email, password)
                        }
                        AuthMode.SIGNUP -> {
                            if (password != confirmPassword) {
                                localError = "Passwords do not match"
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
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign in with Google")
                }
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
                    modifier = Modifier.clickable { mode = AuthMode.RESET; AuthRepository.clearError(); localError = null }
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Sign up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { mode = AuthMode.SIGNUP; AuthRepository.clearError(); localError = null }
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
                        modifier = Modifier.clickable { mode = AuthMode.LOGIN; AuthRepository.clearError(); localError = null }
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
                        modifier = Modifier.clickable { mode = AuthMode.LOGIN; AuthRepository.clearError(); localError = null; resetSent = false }
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
