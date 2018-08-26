package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class SeongBukLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "SeongBukLibrary";
    static final String libraryName = "성북정보도서관";
    static final String baseUrl = "http://elibrary.sblib.seoul.kr";

    public SeongBukLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SeongBukLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
