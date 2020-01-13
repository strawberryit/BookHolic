package pe.andy.bookholic

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        searchService = BookSearchService(this)
        libraryRecyclerList = LibraryRecyclerList(this, mBinding, searchService)
        bookRecyclerList = BookRecyclerList(this, mBinding, searchService)
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
        // 테스트를 위한 리스트
        //bookRecyclerUi.add(generateTestBooks());
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