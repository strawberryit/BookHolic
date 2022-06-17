package pe.andy.bookholic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.andy.bookholic.database.FavoriteDatabase

class FavoriteBookViewModelFactory(
    private val favoriteDatabase: FavoriteDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteBookViewModel(favoriteDatabase) as T
    }
}