package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class GimpoLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "GimpoLibrary";
    static final String libraryName = "김포시도서관";
    static final String baseUrl = "http://ebook.gimpo.go.kr:8091";

    public GimpoLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GimpoLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
