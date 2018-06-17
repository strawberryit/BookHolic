package pe.andy.bookholic.searcher.impl.epyrus;

import java.lang.ref.SoftReference;

import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.model.SearchQuery;
import pe.andy.bookholic.searcher.EpyrusLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.impl.kyobo.SeodaemonLibrarySearchTask;

public class YangCheonLibrarySearchTask extends EpyrusLibrarySearchTask {

    static final String libraryName = "양천구 e도서관";
    static final String baseUrl = "http://ebook2.yangcheon.go.kr/";

    public YangCheonLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new YangCheonLibrarySearchTask(this.mActivity));
        return ref.get();
    }
}
