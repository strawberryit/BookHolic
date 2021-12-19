package pe.andy.bookholic

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import pe.andy.bookholic.adapter.BookAdapter
import pe.andy.bookholic.adapter.LibraryAdapter
import pe.andy.bookholic.databinding.ActivityMainBinding
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy
import pe.andy.bookholic.service.BookSearchService
import pe.andy.bookholic.test.TestData
import pe.andy.bookholic.ui.ScrollVerticalButton
import pe.andy.bookholic.ui.SearchDoneSnackBar

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

    lateinit var libraryAdapter: LibraryAdapter
    lateinit var bookAdapter: BookAdapter

    lateinit var scrollVerticalButton: ScrollVerticalButton
    lateinit var searchDoneSnackBar: SearchDoneSnackBar
    lateinit var searchView: SearchView

    lateinit var searchService: BookSearchService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        searchService = BookSearchService(this, mBinding)

        setupLibraryRecyclerView()
        setupBookRecyclerView()

        mBinding.loadMore.setOnClickListener {
            hideLoadMore()
            showLoadProgress()

            searchService.search(searchService.query, false)
        }

        scrollVerticalButton = ScrollVerticalButton(mBinding)
        searchDoneSnackBar = SearchDoneSnackBar(mBinding)

        setupFabButton()

        mBinding.bookResultTitle.visibility = View.GONE

        // 테스트를 위한 리스트
        bookAdapter.add(TestData.generateTestBooks())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.apply {
            queryHint = "검색어"
            setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("BookHolic", "onQueryTextSubmit")
                    if (query == null || query == "") {
                        return false
                    }

                    clearFocus()
                    val sQuery = SearchQuery(
                            keyword = query.trim(),
                            field = SearchField.TITLE,
                            sortBy = SortBy.TITLE)
                    searchService.search(sQuery)

                    mBinding.fab.visibility = View.GONE
                    mBinding.fabCancel.visibility = View.VISIBLE

                    return true
                }

                override fun onQueryTextChange(newText: String?) = false
            })
        }

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService.cancelAll()
    }

    private fun setupLibraryRecyclerView() {
        val libraries = searchService.tasks.toMutableList()
        libraryAdapter = LibraryAdapter(libraries)
        with(mBinding.libraryRecyclerview) {
            adapter = libraryAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupBookRecyclerView() {
        bookAdapter = BookAdapter()
        with(mBinding.bookRecyclerview) {
            setHasFixedSize(true)
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupFabButton() {
        mBinding.fab.setOnClickListener {
            searchView.apply {
                if (hasFocus()) {
                    setQuery(query, true)
                }
                else {
                    mBinding.nestedScrollView.scrollTo(0, 0)
                    isIconified = false
                }
            }
        }

        mBinding.fabCancel.setOnClickListener {
            searchService.cancelAll()
            mBinding.fab.visibility = View.VISIBLE
            mBinding.fabCancel.visibility = View.GONE
        }
    }

    fun showLoadMore() {
        mBinding.loadMore.visibility = View.VISIBLE
    }

    fun hideLoadMore() {
        mBinding.loadMore.visibility = View.GONE
    }

    fun showLoadProgress() {
        mBinding.loadProgress.visibility = View.VISIBLE
    }

    fun hideLoadProgress() {
        mBinding.loadProgress.visibility = View.GONE
    }
}