package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class SeodaemonLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "";
    static final String libraryName = "서대문 이진아 기념도서관";
    static final String baseUrl = "http://ebook.sdm.or.kr/";

    public SeodaemonLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new SeodaemonLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
