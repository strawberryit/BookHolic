package pe.andy.bookholic.searcher.impl.kyobo;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.KyoboLibrarySearchTask;
import pe.andy.bookholic.searcher.LibrarySearchTask;

public class AnsanLibrarySearchTask extends KyoboLibrarySearchTask {

    @Getter
    final String libraryCode = "AnsanCentralLibrary";
    static final String libraryName = "안산시 중앙도서관";
    static final String baseUrl = "http://ebook.ansan.go.kr";

    public AnsanLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);

    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new AnsanLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
