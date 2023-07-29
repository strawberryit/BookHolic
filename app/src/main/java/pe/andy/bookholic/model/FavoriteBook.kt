package pe.andy.bookholic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "favorite")
data class FavoriteBook(
    @PrimaryKey
    val seq: String,
    val title: String,
    val author: String,
    val publisher: String,
    val libraryName: String,
    val thumbnailUrl: String,
    val url: String,
    val date: String,
    val platform: String,
    val platformClass: String,
) {
    companion object {
        fun from(book: Ebook): FavoriteBook {
            return FavoriteBook(
                seq = UUID.randomUUID().toString(),
                title = book.title,
                author = book.author,
                publisher = book.publisher,
                libraryName = book.libraryName,
                thumbnailUrl = book.thumbnailUrl,
                url = book.url,
                date = book.date,
                platform = book.platform,
                platformClass = book.platformClass,
            )
        }
    }
}
