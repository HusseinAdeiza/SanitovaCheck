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
            // Keep the RevenueCat customer tied to the Firebase account so a
            // purchase restores on sign-in instead of staying anonymous.
            SubscriptionRepository.syncUserIdentity(firebaseAuth.currentUser?.uid)
        }
    }

    fun clearError() {
        _authError.value = null
    }

    /**
     * Maps raw Firebase / network exceptions to short, user-friendly messages.
     * Raw strings like "An internal error has occurred. [ Failed to connect to
     * www.googleapis.com/... ]" confuse users; this turns them into actionable text.
     */
    fun friendlyAuthError(e: Exception): String {
        val raw = (e.localizedMessage ?: e.message ?: "").lowercase()
        val isIo = e is java.io.IOException || e.cause is java.io.IOException
        return when {
            isIo ||
            raw.contains("failed to connect") ||
            raw.contains("unable to resolve host") ||
            raw.contains("no address associated") ||
            raw.contains("network error") ||
            raw.contains("timeout") ||
            raw.contains("socket") ||
            raw.contains("connection reset") ->
                "No internet connection. Check your Wi-Fi or mobile data, then try again."
            raw.contains("already in use") ->
                "An account with this email already exists. Use Sign in instead."
            raw.contains("invalid email") || raw.contains("badly formatted") ->
                "That email address doesn't look valid. Please check it and try again."
            raw.contains("weak password") ->
                "Password is too weak. Use at least 6 characters."
            raw.contains("password is invalid") ||
            raw.contains("wrong password") ||
            raw.contains("incorrect") ||
            raw.contains("malformed") ||
            raw.contains("credential") ->
                "Incorrect email or password. Please check them and try again."
            raw.contains("no user record") ->
                "No account found for this email. Please create an account first."
            raw.contains("too many requests") ->
                "Too many attempts. Please wait a minute and try again."
            raw.contains("not allowed") || raw.contains("disabled") ->
                "This sign-in method isn't available right now. Please contact support."
            else -> e.localizedMessage ?: "Something went wrong. Please try again."
        }
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
            _authError.value = friendlyAuthError(e)
            Result.failure(e)
        }
    }

    /** Sign in with email and password. */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            _authError.value = friendlyAuthError(e)
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
            _authError.value = friendlyAuthError(e)
            Result.failure(e)
        }
    }

    /** Send password reset email. */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            _authError.value = friendlyAuthError(e)
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
