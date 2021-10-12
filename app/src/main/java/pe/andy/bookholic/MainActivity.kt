package pe.andy.bookholic

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import pe.andy.bookholic.databinding.MainActivityBinding
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy
import pe.andy.bookholic.service.BookSearchService
import pe.andy.bookholic.ui.BookRecyclerList
import pe.andy.bookholic.ui.LibraryRecyclerList
import pe.andy.bookholic.ui.ScrollToTopButton
import pe.andy.bookholic.ui.SearchDoneSnackBar

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: MainActivityBinding

    lateinit var libraryRecyclerList: LibraryRecyclerList
    lateinit var bookRecyclerList: BookRecyclerList
    lateinit var scrollToTopButton: ScrollToTopButton
    lateinit var searchDoneSnackBar: SearchDoneSnackBar
    lateinit var searchView: SearchView

    lateinit var searchService: BookSearchService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = MainActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        searchService = BookSearchService(this, mBinding)

        libraryRecyclerList = LibraryRecyclerList(mBinding, searchService)
        bookRecyclerList = BookRecyclerList(mBinding, searchService)

        scrollToTopButton = ScrollToTopButton(mBinding)
        searchDoneSnackBar = SearchDoneSnackBar(mBinding)

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

        // 테스트를 위한 리스트
        //bookRecyclerList.add(TestData.generateTestBooks());
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
}