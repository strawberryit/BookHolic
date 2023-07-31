package pe.andy.bookholic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pe.andy.bookholic.model.Ebook

@Database(entities = [Ebook::class], version = 1)
abstract class EbookDatabase : RoomDatabase() {

    abstract fun ebookDao(): EbookDao

    companion object {
        @Volatile
        var INSTANCE: EbookDatabase? = null

        @Synchronized
        fun getInstance(context: Context): EbookDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    EbookDatabase::class.java,
                ).build()
            }

            return INSTANCE as EbookDatabase
        }
    }
}