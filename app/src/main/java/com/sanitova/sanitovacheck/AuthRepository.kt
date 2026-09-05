package com.sanitova.sanitovacheck

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

/**
 * Central auth manager wrapping Firebase Authentication.
 *
 * Supports:
 * - Email / password sign-up and sign-in
 * - Google Sign-In (via Firebase GoogleAuthProvider)
 * - Password reset
 * - Sign out
 *
 * To enable Google Sign-In:
 * 1. Go to Firebase Console → Authentication → Sign-in method → Google → Enable
 * 2. Add your SHA-1 fingerprint (debug + release) to Firebase project settings
 * 3. Download the updated google-services.json and replace the placeholder
 */
object AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError

    init {
        auth.addAuthStateListener { firebaseAuth ->
            _currentUser.value = firebaseAuth.currentUser
        }
    }

    fun clearError() {
        _authError.value = null
    }

    /** Sign up with email and password. */
    suspend fun signUp(email: String, password: String, displayName: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
            }
            Result.success(result.user!!)
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Sign up failed"
            Result.failure(e)
        }
    }

    /** Sign in with email and password. */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Sign in failed"
            Result.failure(e)
        }
    }

    /** Sign in with Google ID token (from Google Sign-In flow). */
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Google sign-in failed"
            Result.failure(e)
        }
    }

    /** Send password reset email. */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            _authError.value = e.localizedMessage ?: "Failed to send reset email"
            Result.failure(e)
        }
    }

    /** Sign out the current user. */
    fun signOut() {
        auth.signOut()
    }

    /** True if a user is currently signed in. */
    fun isSignedIn(): Boolean = auth.currentUser != null
}
