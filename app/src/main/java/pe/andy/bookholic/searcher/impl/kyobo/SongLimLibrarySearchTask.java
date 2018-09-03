package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class SongLimLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "SongLimLibrary";
    static final String libraryName = "인천동구 송림도서관";
    static final String baseUrl = "http://songlimlib.icdonggu.go.kr:8091";

    public SongLimLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SongLimLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
