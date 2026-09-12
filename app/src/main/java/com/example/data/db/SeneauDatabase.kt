package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ConnectionRequestEntity
import com.example.data.model.SmsNotificationEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.UserAccountEntity

@Database(
    entities = [
        ConnectionRequestEntity::class,
        TransactionEntity::class,
        SmsNotificationEntity::class,
        UserAccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SeneauDatabase : RoomDatabase() {
    abstract fun connectionRequestDao(): ConnectionRequestDao
    abstract fun transactionDao(): TransactionDao
    abstract fun smsNotificationDao(): SmsNotificationDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: SeneauDatabase? = null

        fun getDatabase(context: Context): SeneauDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SeneauDatabase::class.java,
                    "seneau_connect_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
