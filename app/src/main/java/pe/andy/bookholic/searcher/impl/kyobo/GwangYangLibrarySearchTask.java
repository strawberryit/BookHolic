package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class GwangYangLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "GwangYangLibrary";
    static final String libraryName = "광양시립도서관";
    static final String baseUrl = "http://ebook.gwangyang.go.kr:81";

    public GwangYangLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GwangYangLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
