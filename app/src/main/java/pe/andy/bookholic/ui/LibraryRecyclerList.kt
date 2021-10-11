package pe.andy.bookholic.ui

import pe.andy.bookholic.adapter.LibraryAdapter
import pe.andy.bookholic.databinding.MainActivityBinding
import pe.andy.bookholic.searcher.LibrarySearchTask
import pe.andy.bookholic.service.BookSearchService

class LibraryRecyclerList(
        binding: MainActivityBinding,
        searchService: BookSearchService
) {
    var libraries = searchService.tasks.toMutableList()
    private val libraryAdapter = LibraryAdapter(libraries)

    init {
        with(binding.libraryRecyclerview) {
            isNestedScrollingEnabled = false
            adapter = libraryAdapter
        }
    }

    fun set(list: List<LibrarySearchTask>) {
        libraries.clear()
        libraries.addAll(list)
        libraryAdapter.notifyItemRangeInserted(0, list.size)
    }

    fun clear() {
        libraries.clear()
        libraryAdapter.notifyDataSetChanged()
    }

    fun refresh() {
        libraryAdapter.notifyDataSetChanged()
    }
}