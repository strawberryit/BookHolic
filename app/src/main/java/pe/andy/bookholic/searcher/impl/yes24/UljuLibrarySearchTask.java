package pe.andy.bookholic.searcher.impl.yes24;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.Yes24TypeBLibrarySearchTask;

public class UljuLibrarySearchTask extends Yes24TypeBLibrarySearchTask {

    @Getter final String libraryCode = "";
    static final String libraryName = "울주통합도서관";
    static final String baseUrl = "http://uljuebook.ulju.ulsan.kr";

    public UljuLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new UljuLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
