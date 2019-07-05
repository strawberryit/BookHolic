package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class GimjeLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "GimjeLibrary";
    static final String libraryName = "김제시립도서관";
    static final String baseUrl = "http://ebook.gjl.or.kr:8000";

    public GimjeLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

        this.setEncoding(Encoding_UTF8);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GimjeLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
