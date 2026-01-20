package com.tomnylow.flipword.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser?.let { user ->
                User(
                    id = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: ""
                )
            }
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = try {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<Unit> = try {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val profileUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        result.user?.updateProfile(profileUpdate)?.await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut(): Result<Unit> = try {
        firebaseAuth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}