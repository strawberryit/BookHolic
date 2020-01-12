package pe.andy.bookholic;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import lombok.Getter;
import pe.andy.bookholic.databinding.MainActivityBinding;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.service.BookSearchService;
import pe.andy.bookholic.ui.BookRecyclerList;
import pe.andy.bookholic.ui.LibraryRecyclerList;
import pe.andy.bookholic.ui.ScrollToTopButton;
import pe.andy.bookholic.ui.SearchDoneSnackBar;

public class MainActivity extends AppCompatActivity {

    @Getter
    MainActivityBinding mBinding;

    public LibraryRecyclerList libraryRecyclerList;
    public BookRecyclerList bookRecyclerList;
    @Getter public ScrollToTopButton scrollToTopButton;
    @Getter public SearchDoneSnackBar searchDoneSnackBar;

    SearchView searchView;

    @Getter public BookSearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        searchService = new BookSearchService(this);

        libraryRecyclerList = new LibraryRecyclerList(this, this.mBinding, searchService);
        bookRecyclerList = new BookRecyclerList(this, mBinding, searchService);
        scrollToTopButton = new ScrollToTopButton(this.mBinding);
        searchDoneSnackBar = new SearchDoneSnackBar(this.mBinding);

        mBinding.fab.setOnClickListener(view -> {
            if (searchView.hasFocus()) {
                searchView.setQuery(searchView.getQuery(), true);
            }
            else {
                mBinding.nestedScrollView.scrollTo(0, 0);
                searchView.setIconified(false);
            }
        });

        // 테스트를 위한 리스트
        //bookRecyclerUi.add(generateTestBooks());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("검색어");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("BookHolic", "onQueryTextSubmit");
                searchView.clearFocus();

                SearchQuery sQuery = new SearchQuery(
                        query.trim(),
                        SearchField.TITLE,
                        SortBy.TITLE);

                searchService.search(sQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        searchService.cancelAll();
    }

}
