package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class GangbukCultureLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "GangbukCultureLibrary";
    static final String libraryName = "강북문화정보도서관";
    static final String baseUrl = "http://ebook.gblib.or.kr";

    public GangbukCultureLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GangbukCultureLibrarySearchTask(this.mActivity));
        return ref.get();
    }
}
