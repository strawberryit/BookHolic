package pe.andy.bookholic.service;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.impl.GangdongLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.GangnamLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.GyeongsanLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.GyunggidoCyberLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.SeoulEduLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.SeoulLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.UijeongbuLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.epyrus.YangCheonLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.AnsanLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.AsanCityLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.GangJinLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.GangbukCultureLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.GimjeLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.GimpoLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.IncheonSeoguLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.SeodaemonLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.SeongBukLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.SongLimLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.YeosuLibrarySearcher;
import pe.andy.bookholic.searcher.impl.yes24.GyeongjuLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.yes24.JeollanamdoLibrarySearchTask;
import pe.andy.bookholic.searcher.impl.yes24.YeouiDigitalLibrarySearchTask;

public class SearchService {

    private MainActivity mActivity;

    @Getter
    List<LibrarySearchTask> tasks;

    @Getter @Setter
    SearchQuery query;

    public SearchService(MainActivity activity) {
        this.mActivity = activity;

        this.createNewTaskGroup();
    }

    private void createNewTaskGroup() {
        if (tasks != null)
            tasks.clear();

        // List.clear() 메서드를 사용하기 위해 새로 ArrayList 생성
        tasks = new ArrayList<>(Arrays.asList(
                    new SeoulLibrarySearchTask(mActivity),
                    new YeosuLibrarySearcher(mActivity),
                    new SeodaemonLibrarySearchTask(mActivity),
                    new GyeongjuLibrarySearchTask(mActivity),
                    new YangCheonLibrarySearchTask(mActivity),
                    new SeoulEduLibrarySearchTask(mActivity),
                    new GyunggidoCyberLibrarySearchTask(mActivity),
                    new GangdongLibrarySearchTask(mActivity),
                    new GangnamLibrarySearchTask(mActivity),
                    new GyeongsanLibrarySearchTask(mActivity),
                    new AnsanLibrarySearchTask(mActivity),
                    new GangJinLibrarySearchTask(mActivity),
                    new UijeongbuLibrarySearchTask(mActivity),
                    new JeollanamdoLibrarySearchTask(mActivity),
                    new GimpoLibrarySearchTask(mActivity),
                    new SeongBukLibrarySearchTask(mActivity),
                    new AsanCityLibrarySearchTask(mActivity),
                    new IncheonSeoguLibrarySearchTask(mActivity),
                    new SongLimLibrarySearchTask(mActivity),
                    new GangbukCultureLibrarySearchTask(mActivity),
                    new GimjeLibrarySearchTask(mActivity)
            )
        );
    }

    private void setQueryOnAllTask(SearchQuery query) {
        tasks.forEach(t -> t.setQuery(query));
    }

    public void search(final SearchQuery query) {
        search(query, true);
    }

    public void search(final SearchQuery query, final boolean isFresh) {
        this.query = query;

        if (isFresh) {
            mActivity.getBookRecyclerUi().clear();
            query.setPage(1);

            this.createNewTaskGroup();
            mActivity.getLibraryRecyclerUi().set(tasks);

            setQueryOnAllTask(query);
            tasks.stream()
                    .parallel()
                    .forEach(AsyncTask::execute);
        }
        else {
            setQueryOnAllTask(query);

            tasks = tasks.stream()
                    .filter(t -> (t.getResultCount() > 0 && t.hasNext()))
                    .map(t -> {
                        try {
                            t.cancel(true);

                            t = t.create();
                            t.setQuery(query.nextPage());

                            mActivity.getLibraryRecyclerUi().refresh();
                            return t;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .collect(Collectors.toList());

            tasks.stream()
                    .parallel()
                    .filter(Objects::nonNull)
                    .filter(LibrarySearchTask::isTaskPending)
                    .forEach(AsyncTask::execute);
        }
    }

    public void cancelAll() {
        tasks.forEach(t -> t.cancel(true));
    }

    public boolean isFinished() {
        return tasks.stream()
                .noneMatch(LibrarySearchTask::isProgress);
    }

    public boolean isAllLastPage() {
        return tasks.stream()
                .noneMatch(LibrarySearchTask::hasNext);
    }
}
