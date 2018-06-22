package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class GangJinLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "GangjinLibrary";
    static final String libraryName = "강진군 전자도서관";
    static final String baseUrl = "http://www.gjlib.go.kr:8080";

    public GangJinLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new GangJinLibrarySearchTask(this.mActivity));
        return ref.get();
    }
}
