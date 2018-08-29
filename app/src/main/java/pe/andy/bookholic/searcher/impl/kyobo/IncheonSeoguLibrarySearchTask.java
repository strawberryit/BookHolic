package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class IncheonSeoguLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "IncheonSeoguLibrary";
    static final String libraryName = "인천서구 구립도서관";
    static final String baseUrl = "http://ebook.issl.go.kr:8080";

    public IncheonSeoguLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new IncheonSeoguLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
