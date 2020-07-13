package pe.andy.bookholic.ui

import android.content.Context
import android.view.View.GONE
import android.view.View.VISIBLE
import pe.andy.bookholic.adapter.BookAdapter
import pe.andy.bookholic.databinding.MainActivityBinding
import pe.andy.bookholic.model.Ebook
import pe.andy.bookholic.service.BookSearchService

class BookRecyclerList(
        mContext: Context,
        private val binding: MainActivityBinding,
        searchService: BookSearchService
        ) {

    var books = mutableListOf<Ebook>()
    var bookAdapter: BookAdapter = BookAdapter(mContext, books)

    init {
        with(binding.bookRecyclerview) {
            isNestedScrollingEnabled = false
            setHasFixedSize(true)
            adapter = bookAdapter
        }

        binding.loadMore.setOnClickListener {
            hideLoadMore()
            showLoadProgress()

            searchService.apply {
                search(this.query, false)
            }
        }
    }

    fun add(list: List<Ebook>) {
        books.addAll(list)
        bookAdapter.notifyDataSetChanged()
    }

    fun set(list: List<Ebook>) {
        books.clear()
        books.addAll(list)
        bookAdapter.notifyDataSetChanged()
    }

    fun clear() {
        books.clear()
        bookAdapter.notifyDataSetChanged()
    }

    fun showLoadMore() {
        binding.loadMore.visibility = VISIBLE
    }

    fun hideLoadMore() {
        binding.loadMore.visibility = GONE
    }

    fun showLoadProgress() {
        binding.loadProgress.visibility = VISIBLE
    }

    fun hideLoadProgress() {
        binding.loadProgress.visibility = GONE
    }

}