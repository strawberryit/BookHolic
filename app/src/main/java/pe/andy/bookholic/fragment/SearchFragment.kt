package pe.andy.bookholic.fragment

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import pe.andy.bookholic.BuildConfig
import pe.andy.bookholic.R
import pe.andy.bookholic.adapter.BookAdapter
import pe.andy.bookholic.adapter.LibraryAdapter
import pe.andy.bookholic.database.FavoriteDatabase
import pe.andy.bookholic.databinding.BottomSheetAddFavoriteBinding
import pe.andy.bookholic.databinding.FragmentSearchBinding
import pe.andy.bookholic.model.FavoriteBook
import pe.andy.bookholic.model.SearchField
import pe.andy.bookholic.model.SearchQuery
import pe.andy.bookholic.model.SortBy
import pe.andy.bookholic.service.BookSearchService
import pe.andy.bookholic.test.TestData
import pe.andy.bookholic.ui.ScrollVerticalButton
import pe.andy.bookholic.ui.SearchDoneSnackBar
import pe.andy.bookholic.viewmodel.FavoriteBookViewModel
import pe.andy.bookholic.viewmodel.FavoriteBookViewModelFactory

class SearchFragment : Fragment(R.layout.fragment_search) {

    val binding by viewBinding(FragmentSearchBinding::bind)

    lateinit var libraryAdapter: LibraryAdapter
    lateinit var bookAdapter: BookAdapter

    lateinit var scrollVerticalButton: ScrollVerticalButton
    lateinit var searchDoneSnackBar: SearchDoneSnackBar
    lateinit var searchView: SearchView

    lateinit var previewBottomSheetDialog: BottomSheetDialog


    lateinit var favoriteViewModel: FavoriteBookViewModel

    lateinit var searchService: BookSearchService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchService = BookSearchService(this, binding)

        setupLibraryRecyclerView()
        setupBookRecyclerView()

        binding.loadMore.setOnClickListener {
            hideLoadMore()
            showLoadProgress()

            searchService.search(searchService.query, false)
        }

        scrollVerticalButton = ScrollVerticalButton(binding)
        searchDoneSnackBar = SearchDoneSnackBar(binding)

        setupFabButton()

        binding.bookResultTitle.visibility = View.GONE

        previewBottomSheetDialog = BottomSheetDialog(requireContext())


        val database = FavoriteDatabase.getInstance(this.requireContext())
        val viewModelFactory = FavoriteBookViewModelFactory(database)
        favoriteViewModel = ViewModelProvider(this, viewModelFactory)[FavoriteBookViewModel::class.java]

        // 테스트를 위한 리스트
        if (BuildConfig.DEBUG) {
            bookAdapter.add(TestData.generateTestBooks())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
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

                    binding.fab.visibility = View.GONE
                    binding.fabCancel.visibility = View.VISIBLE

                    return true
                }

                override fun onQueryTextChange(newText: String?) = false
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchService.cancelAll()
    }

    private fun setupLibraryRecyclerView() {
        val libraries = searchService.tasks.toMutableList()
        libraryAdapter = LibraryAdapter(libraries)
        with(binding.libraryRecyclerview) {
            adapter = libraryAdapter
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
        }
    }

    private fun setupBookRecyclerView() {
        bookAdapter = BookAdapter(onItemClick = { ebook ->
            val dialogBinding = BottomSheetAddFavoriteBinding.inflate(
                layoutInflater,
            )

            val dialogPeekHeight = Resources.getSystem().displayMetrics.heightPixels / 5 * 2

            with(dialogBinding) {
                tvBookTitle.text = ebook.title

                ivFavorite.setOnClickListener {
                    favoriteViewModel.upsert(FavoriteBook.from(ebook))

                    Toast.makeText(context, "Saved - ${ebook.title}", Toast.LENGTH_SHORT)
                        .show()

                    previewBottomSheetDialog.dismiss()
                }

                ivOpenInBrowser.setOnClickListener {
                    if (ebook.url.isNotEmpty()) {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ebook.url))
                        startActivity(browserIntent)
                    }
                }

                ivClose.setOnClickListener {
                    previewBottomSheetDialog.dismiss()
                }

                with(webview) {
                    loadUrl(ebook.url)
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            request?.url?.let {
                                view?.loadUrl(it.toString())
                            }
                            return false
                        }

                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            dialogBinding.pbLoading.visibility = View.VISIBLE

                        }
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            dialogBinding.pbLoading.visibility = View.GONE
                        }
                    }
                    settings.javaScriptEnabled = true
                }

                nsvWebview.minimumHeight = dialogPeekHeight
            }

            with(previewBottomSheetDialog) {
                setContentView(dialogBinding.root)
                dismissWithAnimation = true

                findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )?.let {
                    BottomSheetBehavior.from(it).apply {
                        peekHeight = dialogPeekHeight
                        state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

                show()
            }
        })

        with(binding.bookRecyclerview) {
            setHasFixedSize(true)
            adapter = bookAdapter
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
        }
    }

    private fun setupFabButton() {
        binding.fab.setOnClickListener {
            searchView.apply {
                if (hasFocus()) {
                    setQuery(query, true)
                }
                else {
                    binding.nestedScrollView.scrollTo(0, 0)
                    isIconified = false
                }
            }
        }

        binding.fabCancel.setOnClickListener {
            searchService.cancelAll()
            binding.fab.visibility = View.VISIBLE
            binding.fabCancel.visibility = View.GONE
        }
    }

    fun showLoadMore() {
        binding.loadMore.visibility = View.VISIBLE
    }

    fun hideLoadMore() {
        binding.loadMore.visibility = View.GONE
    }

    fun showLoadProgress() {
        binding.loadProgress.visibility = View.VISIBLE
    }

    fun hideLoadProgress() {
        binding.loadProgress.visibility = View.GONE
    }

}