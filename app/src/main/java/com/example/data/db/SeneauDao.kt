package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ConnectionRequestEntity
import com.example.data.model.SmsNotificationEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConnectionRequestDao {
    @Query("SELECT * FROM connection_requests ORDER BY createdAt DESC")
    fun getAllRequests(): Flow<List<ConnectionRequestEntity>>

    @Query("SELECT * FROM connection_requests WHERE id = :id")
    fun getRequestById(id: Long): Flow<ConnectionRequestEntity?>

    @Query("SELECT * FROM connection_requests WHERE fileNumber = :fileNumber LIMIT 1")
    suspend fun getRequestByFileNumber(fileNumber: String): ConnectionRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ConnectionRequestEntity): Long

    @Update
    suspend fun updateRequest(request: ConnectionRequestEntity)

    @Query("DELETE FROM connection_requests WHERE id = :id")
    suspend fun deleteRequestById(id: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}

@Dao
interface SmsNotificationDao {
    @Query("SELECT * FROM sms_notifications ORDER BY timestamp DESC")
    fun getAllSms(): Flow<List<SmsNotificationEntity>>

    @Query("SELECT COUNT(*) FROM sms_notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSms(sms: SmsNotificationEntity): Long

    @Query("UPDATE sms_notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Query("SELECT * FROM user_account WHERE id = 1 LIMIT 1")
    suspend fun getUserAccountDirect(): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: UserAccountEntity)

    @Query("UPDATE user_account SET balance = :newBalance WHERE id = 1")
    suspend fun updateBalance(newBalance: Double)

    @Query("UPDATE user_account SET preferredLanguage = :lang WHERE id = 1")
    suspend fun updateLanguage(lang: String)

    @Query("UPDATE user_account SET isDarkMode = :darkMode WHERE id = 1")
    suspend fun updateDarkMode(darkMode: Boolean)

    @Query("UPDATE user_account SET isBiometricEnabled = :enabled WHERE id = 1")
    suspend fun updateBiometric(enabled: Boolean)

    @Query("UPDATE user_account SET lastCloudSync = :timestamp WHERE id = 1")
    suspend fun updateCloudSync(timestamp: Long)
}
