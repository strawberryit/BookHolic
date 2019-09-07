package pe.andy.bookholic.ui;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.adapter.BookAdapter;
import pe.andy.bookholic.databinding.MainActivityBinding;
import pe.andy.bookholic.model.Ebook;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.service.BookSearchService;

public class BookRecyclerUi {

    MainActivity mActivity;
    MainActivityBinding mBinding;

    BookAdapter bookAdapter;
    List<Ebook> bookList;

    public BookRecyclerUi(MainActivity activity) {
        this.mActivity = activity;
        this.mBinding = activity.getMBinding();

        // Recycler View
        mBinding.bookRecyclerview.setNestedScrollingEnabled(false);
        mBinding.bookRecyclerview.setHasFixedSize(true);

        bookList = new ArrayList<>();
        bookAdapter = new BookAdapter(mActivity, bookList);
        mBinding.bookRecyclerview.setAdapter(bookAdapter);

        // Load more
        mBinding.loadMore.setOnClickListener(view -> {
            hideLoadMore();
            showLoadProgress();

            BookSearchService service = mActivity.getSearchService();
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

}
