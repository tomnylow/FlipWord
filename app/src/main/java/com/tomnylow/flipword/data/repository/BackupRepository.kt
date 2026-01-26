package com.tomnylow.flipword.data.repository

import androidx.room.withTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tomnylow.flipword.data.local.CardDao
import com.tomnylow.flipword.data.local.DeckDao
import com.tomnylow.flipword.data.local.FlipWordDatabase
import com.tomnylow.flipword.data.local.model.BackupEntity
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import javax.inject.Inject

class BackupRepository @Inject constructor(
    private val database: FlipWordDatabase,
    private val deckDao: DeckDao,
    private val cardDao: CardDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val json: Json
) {

    suspend fun pushBackup(): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")

            val decks = deckDao.getAllDecksSnapshot()
            val cards = cardDao.getAllCardsSnapshot()

            val backupData = BackupEntity(decks = decks, cards = cards)
            val jsonString = json.encodeToString(backupData)
            withTimeout(10000L) {
                val firebaseData = hashMapOf(
                    FIELD_DATA to jsonString,
                    FIELD_DATE to FieldValue.serverTimestamp()
                )

                firestore.collection(COLLECTION_USERS).document(userId)
                    .collection(COLLECTION_BACKUPS).document(DOCUMENT_LATEST)
                    .set(firebaseData)
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    suspend fun fetchBackup(): Result<Unit> {
        return try {
            val userId =
                auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
            withTimeout(15000L) {
                val snapshot = firestore.collection(COLLECTION_USERS).document(userId)
                    .collection(COLLECTION_BACKUPS).document(DOCUMENT_LATEST)
                    .get()
                    .await()

                if (!snapshot.exists()) throw Exception("Backup not found")

                val jsonString = snapshot.getString(FIELD_DATA)
                    ?: throw Exception("Backup data is empty")

                val backupData = json.decodeFromString<BackupEntity>(jsonString)

                database.withTransaction {
                    deckDao.deleteAllDecks()

                    deckDao.insertDecks(backupData.decks)
                    cardDao.insertCards(backupData.cards)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private companion object {
        const val COLLECTION_USERS = "users"
        const val COLLECTION_BACKUPS = "backups"
        const val DOCUMENT_LATEST = "latest"
        const val FIELD_DATA = "data"
        const val FIELD_DATE = "backupDate"
    }
}