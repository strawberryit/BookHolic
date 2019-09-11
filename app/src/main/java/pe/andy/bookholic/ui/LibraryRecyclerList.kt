package pe.andy.bookholic.ui

import pe.andy.bookholic.MainActivity
import pe.andy.bookholic.adapter.LibraryAdapter
import pe.andy.bookholic.databinding.MainActivityBinding
import pe.andy.bookholic.searcher.LibrarySearchTask

class LibraryRecyclerList(
        mActivity: MainActivity,
        binding: MainActivityBinding
) {

    var libraries = mActivity.searchService.tasks.toMutableList()
    private val libraryAdapter = LibraryAdapter(mActivity, libraries)

    init {

        with(binding.libraryRecyclerview) {
            isNestedScrollingEnabled = false
            adapter = libraryAdapter
        }
    }

    fun set(list: List<LibrarySearchTask>) {
        libraries.clear()
        libraries.addAll(list)
        libraryAdapter.notifyDataSetChanged()
    }

    fun clear() {
        libraries.clear()
        libraryAdapter.notifyDataSetChanged()
    }

    fun refresh() {
        libraryAdapter.notifyDataSetChanged()
    }
}