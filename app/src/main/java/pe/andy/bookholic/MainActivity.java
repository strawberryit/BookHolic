package pe.andy.bookholic;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import pe.andy.bookholic.databinding.MainActivityBinding;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchField;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.model.SortBy;
import pe.andy.bookholic.service.SearchService;
import pe.andy.bookholic.ui.BookRecyclerUi;
import pe.andy.bookholic.ui.LibraryRecyclerUi;
import pe.andy.bookholic.ui.ScrollToTopButton;
import pe.andy.bookholic.ui.SearchDoneSnackBar;

public class MainActivity extends AppCompatActivity {

    @Getter
    MainActivityBinding mBinding;

    @Getter LibraryRecyclerUi libraryRecyclerUi;
    @Getter BookRecyclerUi bookRecyclerUi;
    @Getter ScrollToTopButton scrollToTopButton;
    @Getter SearchDoneSnackBar searchDoneSnackBar;

    SearchView searchView;

    @Getter SearchService searchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        searchService = new SearchService(this);

        libraryRecyclerUi = new LibraryRecyclerUi(this);
        bookRecyclerUi = new BookRecyclerUi(this);
        scrollToTopButton = new ScrollToTopButton(this);
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
