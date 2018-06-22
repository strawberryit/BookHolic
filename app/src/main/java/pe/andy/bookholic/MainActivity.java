package pe.andy.bookholic;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import pe.andy.bookholic.adapter.BookAdapter;
import pe.andy.bookholic.adapter.LibraryAdapter;
import pe.andy.bookholic.databinding.ActivityMainBinding;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.service.SearchService;
import pe.andy.bookholic.ui.BookRecyclerUi;
import pe.andy.bookholic.ui.LibraryRecyclerUi;
import pe.andy.bookholic.util.SSLConnect;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;
    public ActivityMainBinding getBinding() { return this.mBinding; }

    @Getter LibraryRecyclerUi libraryRecyclerUi;
    @Getter BookRecyclerUi bookRecyclerUi;

    SearchView searchView;

    @Getter SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        searchService = new SearchService(this);

        libraryRecyclerUi = new LibraryRecyclerUi(this);
        bookRecyclerUi = new BookRecyclerUi(this);

        mBinding.fab.setOnClickListener(view -> {
            if (searchView.hasFocus()) {
                searchView.setQuery(searchView.getQuery(), true);
            }
            else {
                mBinding.nestedScrollView.scrollTo(0, 0);
                searchView.setIconified(false);
            }
        });

        // Test Data
        //bookRecyclerUi.add(generateTestBooks());

        /*
        SearchField sField = SearchField.valueOf("TITLE");
        SearchQuery query = SearchQuery.builder()
                .keyword("과학")
                .field(sField)
                .page(1)
                .build();

        List<Ebook> books =
                //generateTestBooks();
                searchService.search(query);

        BookAdapter adapter = new BookAdapter(this, books);
        booklistView.setAdapter(adapter);
        */

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

                SearchQuery sQuery = SearchQuery.builder()
                        .field(SearchField.TITLE)
                        .keyword(query.trim())
                        .page(1)
                        .sortBy(SortBy.TITLE)
                        .build();

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

    List<Ebook> generateTestBooks() {
        Ebook b1 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편 테스트")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://seoullib.barob.co.kr/resources/images/Lsize/PRD000115946.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(2)
                .setDate("2018-01-01");

        Ebook b2 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/8638926.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01");

        Ebook b3 = new Ebook("테스트 도서관")
                .setSeq("1234")
                .setTitle("인프라 엔지니어의 교과서-시스템 구축과 관리편")
                .setAuthor("기술평론사 편집부")
                .setPublisher("길벗")
                .setLibraryName("테스트 도서관")
                .setPlatform("교보문고")
                .setThumbnailUrl("http://elib.seoul.go.kr/resources/images/YES24/Lsize/7042064.jpg")
                .setUrl("http://elib.seoul.go.kr/ebooks/detail.do?no=PRD000115946")
                .setCountTotal(5)
                .setCountRent(5)
                .setDate("2018-01-01");


        return Arrays.asList(b1, b2, b3, b1, b2, b3);
    }

}
