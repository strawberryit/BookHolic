package pe.andy.bookholic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pe.andy.bookholic.database.FavoriteDatabase
import pe.andy.bookholic.model.FavoriteBook

class FavoriteBookViewModel(
    private val favoriteDatabase: FavoriteDatabase
) : ViewModel() {

    private var favoriteBookLiveData = favoriteDatabase.favoriteBookDao().getAllFavoriteBooks()

    fun upsert(book: FavoriteBook) {
        viewModelScope.launch {
            favoriteDatabase.favoriteBookDao().upsert(book)
        }
    }

    fun delete(book: FavoriteBook) {
        viewModelScope.launch {
            favoriteDatabase.favoriteBookDao().delete(book)
        }
    }

    fun observeLiveData(): LiveData<List<FavoriteBook>> {
        return favoriteBookLiveData
    }
}