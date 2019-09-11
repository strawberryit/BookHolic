package pe.andy.bookholic.ui;

import java.util.List;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.adapter.LibraryAdapter;
import pe.andy.bookholic.databinding.MainActivityBinding;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class LibraryRecyclerUi {

    MainActivity mActivity;
    MainActivityBinding mBinding;

    // Library List
    LibraryAdapter libraryAdapter;
    List<LibrarySearchTask> libraryTaskList;

    public LibraryRecyclerUi(MainActivity activity) {
        this.mActivity = activity;
        this.mBinding = activity.getMBinding();

        mBinding.libraryRecyclerview.setNestedScrollingEnabled(false);

        libraryTaskList = mActivity.getSearchService().getTasks();
        libraryAdapter = new LibraryAdapter(mActivity, libraryTaskList);
        mBinding.libraryRecyclerview.setAdapter(libraryAdapter);

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
