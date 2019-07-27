package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class SuncheonLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "SuncheonLibrary";
    static final String libraryName = "순천시립도서관";
    static final String baseUrl = "http://libebook.sc.go.kr";

    public SuncheonLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SuncheonLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
