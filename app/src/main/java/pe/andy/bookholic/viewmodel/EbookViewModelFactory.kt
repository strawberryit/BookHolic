package pe.andy.bookholic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pe.andy.bookholic.database.EbookDatabase

class EbookViewModelFactory(
    private val ebookDatabase: EbookDatabase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EbookViewModel(ebookDatabase) as T
    }
}