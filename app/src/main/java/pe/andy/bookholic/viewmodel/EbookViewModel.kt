package pe.andy.bookholic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pe.andy.bookholic.database.EbookDatabase
import pe.andy.bookholic.model.Ebook

class EbookViewModel (
    private val ebookDatabase: EbookDatabase
) : ViewModel() {

    private var bookLiveData = ebookDatabase.ebookDao().getAllBooks()

    fun upsert(book: Ebook) {
        viewModelScope.launch {
            ebookDatabase.ebookDao().upsert(book)
        }
    }

    fun upsert(books: List<Ebook>) {
        viewModelScope.launch {
            ebookDatabase.ebookDao().upsertAll(*books.toTypedArray())
        }
    }

    fun clearAllTable() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ebookDatabase.clearAllTables()
            }
        }
    }

    fun observeLiveData(): LiveData<List<Ebook>> {
        return bookLiveData
    }
}