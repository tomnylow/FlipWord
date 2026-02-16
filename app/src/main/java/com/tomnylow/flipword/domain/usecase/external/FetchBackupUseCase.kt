package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.data.repository.BackupRepository
import javax.inject.Inject

class FetchBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
){
    suspend operator fun invoke(): Result<Unit> =
        backupRepository.fetchBackup()

}