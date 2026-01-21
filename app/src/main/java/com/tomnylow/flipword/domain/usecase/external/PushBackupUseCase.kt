package com.tomnylow.flipword.domain.usecase.external

import com.tomnylow.flipword.data.repository.BackupRepository
import javax.inject.Inject

class PushBackupUseCase @Inject constructor(
    private val backupRepository: BackupRepository
){
    suspend operator fun invoke(): Result<Unit> =
        backupRepository.pushBackup()

}