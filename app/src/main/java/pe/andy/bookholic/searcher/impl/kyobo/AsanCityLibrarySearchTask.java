package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class AsanCityLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "AsanCityLibrary";
    static final String libraryName = "아산시립도서관";
    static final String baseUrl = "http://ebook.ascl.or.kr:8090";

    public AsanCityLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new AsanCityLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
