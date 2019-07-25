package pe.andy.bookholic.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.adapter.LibraryAdapter;
import pe.andy.bookholic.databinding.ActivityMainBinding;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class LibraryRecyclerUi {

    MainActivity mActivity;
    ActivityMainBinding mBinding;

    // Library List
    LibraryAdapter libraryAdapter;
    List<LibrarySearchTask> libraryTaskList;

    public LibraryRecyclerUi(MainActivity activity) {
        this.mActivity = activity;
        this.mBinding = activity.getBinding();

        mBinding.libraryRecyclerview.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager libraryLayoutMgr = new LinearLayoutManager(mActivity);
        libraryLayoutMgr.setAutoMeasureEnabled(true);
        mBinding.libraryRecyclerview.setLayoutManager(libraryLayoutMgr);

        libraryTaskList = mActivity.getSearchService().getTasks();
        libraryAdapter = new LibraryAdapter(mActivity, libraryTaskList);
        mBinding.libraryRecyclerview.setAdapter(libraryAdapter);

    }

    <T> T findViewById(int resId) {
        return (T) mActivity.findViewById(resId);
    }

    public void add(List<LibrarySearchTask> list) {
        libraryTaskList.addAll(list);
        libraryAdapter.notifyDataSetChanged();
    }

    public void set(List<LibrarySearchTask> list) {
        libraryTaskList.clear();
        libraryTaskList.addAll(list);
        libraryAdapter.notifyDataSetChanged();
    }

    public void clear() {
        libraryTaskList.clear();
        libraryAdapter.notifyDataSetChanged();
    }

    public void refresh() {
        libraryAdapter.notifyDataSetChanged();
    }
}
