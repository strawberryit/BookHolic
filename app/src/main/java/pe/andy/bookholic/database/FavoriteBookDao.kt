package pe.andy.bookholic.database

import androidx.lifecycle.LiveData
import androidx.room.*
import pe.andy.bookholic.model.FavoriteBook

@Dao
interface FavoriteBookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: FavoriteBook)

    @Delete
    suspend fun delete(book: FavoriteBook)

    @Query("""
        SELECT *
        FROM favorite
    """)
    fun getAllFavoriteBooks(): LiveData<List<FavoriteBook>>
}