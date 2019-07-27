package pe.andy.bookholic.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.adapter.BookAdapter;
import pe.andy.bookholic.databinding.ActivityMainBinding;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.service.SearchService;

public class BookRecyclerUi {

    MainActivity mActivity;
    ActivityMainBinding mBinding;

    BookAdapter bookAdapter;
    List<Ebook> bookList;

    public BookRecyclerUi(MainActivity activity) {
        this.mActivity = activity;
        this.mBinding = activity.getBinding();

        // Recycler View
        mBinding.bookRecyclerview.setNestedScrollingEnabled(false);
        mBinding.bookRecyclerview.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(mActivity);
        layoutMgr.setAutoMeasureEnabled(true);
        mBinding.bookRecyclerview.setLayoutManager(layoutMgr);

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(mActivity, bookList);
        mBinding.bookRecyclerview.setAdapter(bookAdapter);

        // Load more
        mBinding.loadMore.setOnClickListener(view -> {
            hideLoadMore();
            showLoadProgress();

            SearchService service = mActivity.getSearchService();
            SearchQuery sQuery = service.getQuery();
            service.search(sQuery, false);
        });

    }

    public void add(List<Ebook> list) {
        bookList.addAll(list);
        bookAdapter.notifyDataSetChanged();
    }

    public void set(List<Ebook> list) {
        bookList.clear();
        bookList.addAll(list);
        bookAdapter.notifyDataSetChanged();
    }

    public void clear() {
        bookList.clear();
        bookAdapter.notifyDataSetChanged();
    }

    public void showLoadMore() {
        mBinding.loadMore.setVisibility(View.VISIBLE);
    }
    public void hideLoadMore() {
        mBinding.loadMore.setVisibility(View.GONE);
    }

    public void showLoadProgress() {
        mBinding.loadProgress.setVisibility(View.VISIBLE);
    }
    public void hideLoadProgress() {
        mBinding.loadProgress.setVisibility(View.GONE);
    }

    /*

    Activity activity;

    // Book
    public static RecyclerView bookRecyclerView;
    public static BookAdapter bookAdapter;
    public static List<Ebook> ebooks;

    public static TextView loadMore;
    public static ProgressBar loadProgress;

    public BookRecyclerUi(Activity activity) {
        this.activity = activity;
    }

    public void setup() {
        // 책 목록
        bookRecyclerView = (RecyclerView) activity.findViewById(R.id.booklist);
        bookRecyclerView.setNestedScrollingEnabled(false);
        bookRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutMgr = new LinearLayoutManager(activity);
        layoutMgr.setAutoMeasureEnabled(true);
        bookRecyclerView.setLayoutManager(layoutMgr);

        ebooks = new ArrayList<>();
        bookAdapter = new BookAdapter(activity, ebooks);
        bookRecyclerView.setAdapter(bookAdapter);

        loadMore = activity.findViewById(R.id.load_more);
        loadMore.setOnClickListener(view -> {
            BookRecyclerUi.hideLoadMore();
            BookRecyclerUi.showLoadProgress();

            SearchQuery sQuery = MainActivity.searchService.getQuery();
            MainActivity.searchService.search(sQuery, false);
        });

        loadProgress = activity.findViewById(R.id.load_progress);
    }

    public static void add(List<Ebook> list) {
        ebooks.addAll(list);
        bookAdapter.notifyDataSetChanged();
    }

    public static void set(List<Ebook> list) {
        ebooks.clear();
        ebooks.addAll(list);
        bookAdapter.notifyDataSetChanged();
    }

    public static void clear() {
        ebooks.clear();
        bookAdapter.notifyDataSetChanged();
    }

    public static void showLoadMore() {
        loadMore.setVisibility(View.VISIBLE);
    }
    public static void hideLoadMore() {
        loadMore.setVisibility(View.GONE);
    }

    public static void showLoadProgress() {
        loadProgress.setVisibility(View.VISIBLE);
    }
    public static void hideLoadProgress() {
        loadProgress.setVisibility(View.GONE);
    }
    */
}
