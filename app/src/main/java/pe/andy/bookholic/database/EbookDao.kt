package pe.andy.bookholic.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pe.andy.bookholic.model.Ebook

@Dao
interface EbookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(book: Ebook)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(vararg book: Ebook)

    @Query("""
        SELECT *
        FROM ebook
        ORDER BY title
    """)
    fun getAllBooks(): LiveData<List<Ebook>>

}