package pe.andy.bookholic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pe.andy.bookholic.model.FavoriteBook

@Database(entities = [FavoriteBook::class], version = 1)
@TypeConverters(FavoriteTypeConverter::class)
abstract class FavoriteDatabase : RoomDatabase() {

    abstract fun favoriteBookDao(): FavoriteBookDao

    companion object {
        @Volatile
        var INSTANCE: FavoriteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): FavoriteDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteDatabase::class.java,
                    "favorite.db"
                ).fallbackToDestructiveMigration()
                    .build()
            }

            return INSTANCE as FavoriteDatabase
        }
    }
}