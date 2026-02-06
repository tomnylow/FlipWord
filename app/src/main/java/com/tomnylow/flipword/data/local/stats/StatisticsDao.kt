package com.tomnylow.flipword.data.local.stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tomnylow.flipword.domain.model.DeckProgress
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface StatisticsDao {
    @Query("SELECT COUNT(*) FROM cards WHERE interval > 0")
    fun getTotalLearnedWords(): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_logs")
    fun getTotalAnswersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM study_logs WHERE isCorrect == 1")
    fun getCorrectAnswersCount(): Flow<Int>

    @Query("SELECT DISTINCT date FROM study_logs ORDER BY date DESC")
    fun getStudyDates(): Flow<List<LocalDate>>

    @Query(
        """
        SELECT 
            deckId, 
            COUNT(*) as totalCards, 
            SUM(CASE WHEN interval > 0 THEN 1 ELSE 0 END) as learnedCards 
        FROM cards 
        GROUP BY deckId
    """
    )
    fun getDecksProgressRaw(): Flow<List<DeckProgress>>
    @Insert
    fun insertLog(studyLogEntity: StudyLogEntity)
}

