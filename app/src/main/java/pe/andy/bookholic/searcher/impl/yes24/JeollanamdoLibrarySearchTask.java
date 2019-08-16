package pe.andy.bookholic.searcher.impl.yes24;

import java.lang.ref.SoftReference;

import lombok.Getter;
import pe.andy.bookholic.MainActivity;
import pe.andy.bookholic.searcher.LibrarySearchTask;
import pe.andy.bookholic.searcher.Yes24TypeBLibrarySearchTask;

public class JeollanamdoLibrarySearchTask extends Yes24TypeBLibrarySearchTask {

    @Getter
    final String libraryCode = "";
    static final String libraryName = "전라남도 도립도서관";
    static final String baseUrl = "http://152.99.134.221:8080";

    public JeollanamdoLibrarySearchTask(MainActivity activity) {
        super(activity, libraryName, baseUrl);
    }

    @Override
    public LibrarySearchTask create() {
        SoftReference<LibrarySearchTask> ref = new SoftReference<>(new JeollanamdoLibrarySearchTask(this.mActivity));
        return ref.get();
    }

}
